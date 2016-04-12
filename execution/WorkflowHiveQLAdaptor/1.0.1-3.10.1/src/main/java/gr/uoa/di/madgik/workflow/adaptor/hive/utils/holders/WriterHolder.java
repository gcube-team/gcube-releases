package gr.uoa.di.madgik.workflow.adaptor.hive.utils.holders;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A resultset writer holder for writing resultset locators.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class WriterHolder {
	private Logger log = LoggerFactory.getLogger(WriterHolder.class.getName());
	/** {@link RecordWriter} used by merger. */
	private RecordWriter<GenericRecord> writer;

	private static final String fieldName = "Locator";

	/**
	 * @throws Exception
	 */
	public WriterHolder() throws Exception {
		RecordDefinition[] defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition(fieldName) })) };
		writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), defs);
	}

	/**
	 * Get the merger URI
	 * 
	 * @return the merger's locator
	 */
	public URI getLocator() {
		try {
			return writer.getLocator();
		} catch (GRS2WriterException e) {
			return null;
		}
	}

	/**
	 * Delegate a new RS uri for merging
	 * 
	 * @param uri The uri that will be delegated. 
	 * @throws Exception
	 */
	public void put(String uri) throws Exception {
		// while the merger hasn't stopped reading  
//		if (writer.getStatus() != Status.Open)
//			throw new Exception("Merger's resultSet has closed unexpectendly.");
		
		GenericRecord rec = new GenericRecord();

		// Only a string field is added to the record as per locator
		rec.setFields(new Field[] { new StringField(uri) });

		// if the buffer is in maximum capacity for the specified
		// interval don;t wait any more
		try {
			if (!writer.put(rec, 60, TimeUnit.SECONDS))
				throw new GRS2WriterException("Buffer capacity reached");
		} catch (GRS2WriterException e) {
			log.warn("Could not add a locator to resultset");
		}

		log.info("resultset succefully added");
	}
	
	/**
	 * @throws Exception
	 */
	public void close() throws Exception {
		writer.close();
	}
}
