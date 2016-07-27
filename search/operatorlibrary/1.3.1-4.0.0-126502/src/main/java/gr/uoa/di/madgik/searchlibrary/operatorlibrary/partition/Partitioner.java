package gr.uoa.di.madgik.searchlibrary.operatorlibrary.partition;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Partitioner {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(Partitioner.class.getName());

	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private TimeUnit timeUnit;

	/**
	 * 
	 */
	IRecordWriter<Record> output;
	private RecordDefinition[] defs;
	private HashMap<Integer, IRecordWriter<Record>> writers;
	
	private int cnt = 0;

	public Partitioner(IRecordWriter<Record> output, RecordDefinition[] defs, long timeout, TimeUnit timeUnit) {
		this.output = output;
		this.defs = defs;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		writers = new HashMap<Integer, IRecordWriter<Record>>();
	}
	
	public IRecordWriter<Record> getWriter(String field) throws GRS2Exception {
		if (writers.containsKey(field.hashCode()))
				return writers.get(field.hashCode());
		
		RecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), defs, RecordWriter.DefaultBufferCapacity,
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, timeout, timeUnit);
		
		writers.put(field.hashCode(), writer);
		
		logger.debug("Created (" + ++cnt + ") result set: " + writer.getLocator());
		
		GenericRecord rec = new GenericRecord();
		rec.setFields(new Field[] { new StringField(writer.getLocator().toASCIIString()) });
		
		if(!output.importRecord(rec, 60, TimeUnit.SECONDS)){
			if(output.getStatus() == Status.Open)
				logger.warn("Consumer has timed out");
		}		
		
		return writer;
	}

	public void closeAll() {
		for (IRecordWriter<Record> w : writers.values()) {
			try {
				w.close();
			} catch (GRS2WriterException e) {
				try {
					logger.warn("Could not close locator " + w.getLocator() + ". Continuing to next");
				} catch (GRS2WriterException e1) {
					logger.warn("Could not close locator. Continuing to next");
				}
			}
		}
	}
}
