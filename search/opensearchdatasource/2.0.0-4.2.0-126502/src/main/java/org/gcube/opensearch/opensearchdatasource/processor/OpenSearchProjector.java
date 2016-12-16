package org.gcube.opensearch.opensearchdatasource.processor;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.opensearch.opensearchlibrary.OpenSearchDataSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchProjector implements Runnable {
	
	public static long TimeoutDef = 180;
	public static TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private static Logger logger = LoggerFactory.getLogger(OpenSearchProjector.class.getName());
	private URI inLocator = null;
	private Map<String, String> projectedFields = null;
	private Map<String, Integer> fieldPositions = null;
	private RecordWriter<GenericRecord> writer = null;
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	public OpenSearchProjector(URI inLocator, RecordDefinition projectionRecordDefinitions[], 
			Map<String, String> projectedFields, Map<String, Integer> fieldPositions) throws Exception {
		this.inLocator = inLocator;
		this.projectedFields = projectedFields;
		this.fieldPositions = fieldPositions;

		this.writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), projectionRecordDefinitions, 50, 
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
	}
	
	public void setReaderTimeout(long timeout, TimeUnit timeUnit) {
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	public URI getProjectionLocator() throws Exception {
		return this.writer.getLocator();
	}
	
	//@Override
	public void run() {
		ForwardReader<Record> reader = null;
		try {
			reader = new ForwardReader<Record>(this.inLocator);
			
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
				Field[] fields = new Field[this.projectedFields.size()+2];
				fields[0] = new StringField(((StringField)rec.getField(OpenSearchDataSourceConstants.COLLECTION_FIELD)).getPayload());
				fields[1] = new StringField(((StringField)rec.getField(OpenSearchDataSourceConstants.OBJECTID_FIELD)).getPayload());
				if(this.projectedFields.get(OpenSearchDataSourceConstants.LANGUAGE_FIELD) != null) {
					fields[this.fieldPositions.get(OpenSearchDataSourceConstants.LANGUAGE_FIELD)] = new StringField();
					if(rec.getField(OpenSearchDataSourceConstants.LANGUAGE_FIELD) == null)
						((StringField)fields[this.fieldPositions.get(OpenSearchDataSourceConstants.LANGUAGE_FIELD)]).setPayload("*");
					else
						((StringField)fields[this.fieldPositions.get(OpenSearchDataSourceConstants.LANGUAGE_FIELD)]).setPayload(((StringField)rec.getField(OpenSearchDataSourceConstants.LANGUAGE_FIELD)).getPayload());
				}
				
				for(Map.Entry<String, String> projectedField: this.projectedFields.entrySet()) {
					int position = this.fieldPositions.get(projectedField.getKey());
					StringField field = (StringField)rec.getField(projectedField.getValue());
					fields[position] = new StringField(field!=null?field.getPayload():"");
				}
				
				outRec.setFields(fields);
				if(!writer.put(outRec, 60, TimeUnit.SECONDS) ) {
					if(writer.getStatus() == IBuffer.Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
			}
		}catch(Exception e) {
			logger.error("Could not perform result projection", e);
		}finally {
			try { if(reader != null) reader.close(); } catch(Exception e) { logger.warn("Could not close reader", e); }
			try { writer.close(); } catch(Exception e) { logger.warn("Could not close writer", e);}
		}

	}

}
