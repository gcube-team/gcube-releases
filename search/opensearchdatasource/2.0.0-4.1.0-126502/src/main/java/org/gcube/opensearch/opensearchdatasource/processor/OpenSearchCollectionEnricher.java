package org.gcube.opensearch.opensearchdatasource.processor;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.opensearch.opensearchlibrary.OpenSearchDataSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchCollectionEnricher implements Runnable {
	public static long TimeoutDef = 180;
	public static TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private static Logger logger = LoggerFactory.getLogger(OpenSearchProjector.class.getName());
	private Object synch = new Object();
	private URI inLocator = null;
	private URI outLocator = null;
	private String collection = null;
	private RecordWriter<GenericRecord> writer = null;
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	private RecordDefinition[] getRecordDefinitions(RecordDefinition[] readerRecordDefs) throws Exception {
		RecordDefinition[] defs = new RecordDefinition[readerRecordDefs.length];
		int i = 0;
		for(RecordDefinition def : readerRecordDefs) {
			
			RecordDefinition tmp = def.getClass().newInstance();
			tmp.copyFrom(def);
			FieldDefinition[] fieldDefs = new FieldDefinition[tmp.getDefinitionSize()+1];
			fieldDefs[0] = new StringFieldDefinition(OpenSearchDataSourceConstants.COLLECTION_FIELD);
			for(int f = 0; f < def.getDefinitionSize(); f++)
				fieldDefs[f+1] = tmp.getDefinition(f);
			
			defs[i] = new GenericRecordDefinition(fieldDefs);
			defs[i].setTransportDirective(tmp.getTransportDirective());
			
			i++;
		}
		return defs;
	}
	
	public OpenSearchCollectionEnricher(String collection, URI inLocator) throws Exception {
		this.collection = collection;
		this.inLocator = inLocator;
	}
	
	public void setReaderTimeout(long timeout, TimeUnit timeUnit) {
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	public URI getLocator() throws Exception {
		synchronized(synch) {
			while(outLocator == null) {
				try { synch.wait(); } catch(InterruptedException e) { }
			}
		}
		return outLocator;
	}
	
	//@Override
	public void run() {
		ForwardReader<Record> reader = null;
		try {
			reader = new ForwardReader<Record>(this.inLocator);
			this.writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), getRecordDefinitions(reader.getRecordDefinitions()), 50, 
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
			this.outLocator = writer.getLocator();
			synchronized(synch) {
				synch.notify();
			}
			
			while(true) {
				if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				
				Record rec = reader.get(this.timeout, this.timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
				
				if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
				
				BufferEvent event = reader.receive();
				if(event != null) {
					try { writer.emit(event); } 
					catch(Exception e) { logger.warn("Could not emit event") ; }
				}
				
				GenericRecord outRec = new GenericRecord();
				Field[] fields = new Field[rec.getFields().length+1];
				fields[0] = new StringField(this.collection);
				for(int i = 1; i < fields.length; i++)
					fields[i] = rec.getField(i-1);
				
				outRec.setFields(fields);
				outRec.setDefinitionIndex(rec.getDefinitionIndex());
				rec.hide();
				if(!writer.put(outRec, 60, TimeUnit.SECONDS) ) {
					if(writer.getStatus() == IBuffer.Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
			}
		}catch(Exception e) {
			logger.error("Could not perform result enriching", e);
		}finally {
			try { if(reader != null) reader.close(); } catch(Exception e) { logger.warn("Could not close reader", e); }
			try { writer.close(); } catch(Exception e) { logger.warn("Could not close writer", e);}
		}

	}
}
