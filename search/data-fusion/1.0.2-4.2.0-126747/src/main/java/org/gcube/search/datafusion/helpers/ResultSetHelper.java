package org.gcube.search.datafusion.helpers;

import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.gcube.search.datafusion.datatypes.RankedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public class ResultSetHelper implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final long RSTIMEOUT = 60;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetHelper.class);
	
	/**
	 * Initializes the gRS2 for writing
	 */
	public static void initializeGRS2(String hostname) {
		TCPConnectionManager.Init(new ConnectionManagerConfig(hostname, new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
	}
	
	/**
	 * Gets the actual records from the rec and at the same time is writing them in a gRS2 result set.
	 * 
	 * @param recs
	 * @param index
	 * @param fields the union of all fields of all the records
	 * @return gRS2 locator
	 * @throws Exception
	 */
	public static URI multiGetAndWrite(final List<RankedRecord> recs, Directory index, final Set<String> fields) throws Exception {
		final RecordWriter<GenericRecord> rsWriter = initRSWriterForFusedHits(fields);
		final IndexReader reader = DirectoryReader.open(index);
		
		rsWriter.emit(new KeyValueEvent("resultsNumberFinal", String.valueOf(recs.size())));
		
		Runnable writerRun = new Runnable() {
			public void run() {
				try {
					int cnt = 0;
					
					for (RankedRecord rec : recs) {
						Integer docID = rec.getLuceneDocID();
						Document doc = reader.document(docID);
						
						GenericRecord rsRec = RecordHelper.luceneToRSRecord(doc, fields, RankedRecord.calcScore(rec));
						
						while (!rsWriter.put(rsRec, RSTIMEOUT, TimeUnit.SECONDS )) {
							// while the reader hasn't stopped reading
							if (rsWriter.getStatus() != Status.Open)
								break;
						}
						
						//logger.info("wrote rec." + rsRec.getField(0));
						cnt++;
					}
					LOGGER.info("Num of recs written " + cnt);
					if (rsWriter.getStatus() != Status.Dispose)
						rsWriter.close();
				} catch (Exception e) {
						try {
							if (rsWriter.getStatus() != Status.Dispose)
								rsWriter.close();
						} catch (Exception ex) {
							LOGGER.error("Error while closing RS writer.", ex);
							//ex.printStackTrace();
						}
						LOGGER.error("Error while writing the results : ", e);
					}
				
//					for (String field : fields) {
//						System.out.print(((StringField)rec.getField(field)).getPayload());
//						System.out.print(" ");
//					}
//					logger.info("");
//					
//					logger.info("Record : "  + rec.getID());
				}
			
		};
		new Thread(writerRun).start();
		
		LOGGER.info("results locator : " + rsWriter.getLocator());
		
		return rsWriter.getLocator();
	}
	
	/**
	 * Gets the actual records from the rec and then writes them in a gRS2 result set.
	 * 
	 * @param recs
	 * @param index
	 * @param fields the union of all fields of all the records
	 * @return gRS2 locator
	 * @throws Exception
	 */
	public static URI multiGetAndWriteNoStream(final List<RankedRecord> recs, Directory index, final Set<String> fields) throws Exception {
		long starttime = 0;
		long endtime = 0;
		starttime = System.currentTimeMillis();
		List<GenericRecord> rerankedRecords = multiget(recs, index, fields);
		endtime = System.currentTimeMillis();
		LOGGER.info(" ~> multiget time : " + (endtime- starttime) / 1000.0 + " secs");
		
		
		starttime = System.currentTimeMillis();
		URI locator = writeRecords(rerankedRecords, fields);
		endtime = System.currentTimeMillis();
		LOGGER.info(" ~> writeRecords time : " + (endtime- starttime) / 1000.0 + " secs");
		
		return locator;
	}
	
	/**
	 * Gets the actual records from recs.
	 * 
	 * @param recs
	 * @param index
	 * @param fieldsName
	 * @return a list of {@link GenericRecord}s retrieved from lucene
	 * @throws Exception
	 */
	private static List<GenericRecord> multiget(List<RankedRecord> recs, Directory index, Set<String> fieldsName) throws Exception {
		List<GenericRecord> rsRecs = new ArrayList<GenericRecord>();
		IndexReader reader = DirectoryReader.open(index);
		
		for (RankedRecord rec : recs) {
			Integer docID = rec.getLuceneDocID();
			
			Document doc = null;
			if (docID != null)
				doc = reader.document(docID);
			
			GenericRecord rsRec = RecordHelper.luceneToRSRecord(doc, fieldsName, RankedRecord.calcScore(rec));
			rsRecs.add(rsRec);
		}
		return rsRecs;
	}
	
	/**
	 * Writes the records in a gRS2 result set
	 * @param recs
	 * @param fields the union of all fields of all the records
	 * @return gRS2 locator
	 * @throws Exception
	 */
	private static URI writeRecords(final List<GenericRecord> recs, final Set<String> fields) throws Exception {
		final RecordWriter<GenericRecord> rsWriter = initRSWriterForFusedHits(fields);
		
		rsWriter.emit(new KeyValueEvent("resultsNumberFinal", String.valueOf(recs.size())));
		
		Runnable writerRun = new Runnable() {
			public void run() {
				try {
					int cnt = 0;
					LOGGER.info("NUm of recs to write " + recs.size());	
					for (GenericRecord rec : recs) {
							//rsWriter.put(rec);
						
							while (!rsWriter.put(rec, RSTIMEOUT, TimeUnit.SECONDS)) {
								// while the reader hasn't stopped reading
								if (rsWriter.getStatus() != Status.Open)
									break;
							}
							//logger.info("wrote rec." + rec.getField(0));
							cnt++;
						}
				
					LOGGER.info("NUm of recs written " + cnt);
					if (rsWriter.getStatus() != Status.Dispose)
						rsWriter.close();
				} catch (GRS2WriterException e) {
						try {
							if (rsWriter.getStatus() != Status.Dispose)
								rsWriter.close();
						} catch (Exception ex) {
							LOGGER.error("Error while closing RS writer.", ex);
							//ex.printStackTrace();
						}
					}
//					for (String field : fields) {
//						System.out.print(((StringField)rec.getField(field)).getPayload());
//						System.out.print(" ");
//					}
//					logger.info("");
//					
//					logger.info("Record : "  + rec.getID());
				}
			
		};
		new Thread(writerRun).start();
		
		LOGGER.info("results locator : " + rsWriter.getLocator());
		
		return rsWriter.getLocator();
	}
	
	

	/**
	 * Initializes the result set from the fields
	 * 
	 * @param fields
	 * @return the initialized {@link RecordWriter}
	 * @throws Exception
	 */
	private static RecordWriter<GenericRecord> initRSWriterForFusedHits(Set<String> fields) throws Exception {
		LOGGER.info("Initializing gRS2 writer");
		LOGGER.info("(1/3) getting field definitions");
		FieldDefinition[] fieldDef = null;
		try {
			fieldDef = createFieldDefinition(fields);
		} catch (Exception e) {
			LOGGER.error("Could not create field definition: ", e);
			throw new Exception(e);
		}

		LOGGER.info("(2/3) creating record definitions");
		RecordDefinition[] definition = new RecordDefinition[] { new GenericRecordDefinition(fieldDef) };

		LOGGER.info("(3/3) creating rsWriter");
		return new RecordWriter<GenericRecord>(new TCPWriterProxy(), definition);

	}
	
	/**
	 * Creates field definitions from fields
	 * 
	 * @param fields
	 * @return an array of {@link FieldDefinition}s created
	 * @throws Exception
	 */
	private static FieldDefinition[] createFieldDefinition(Set<String> fields)
			throws Exception {
		ArrayList<FieldDefinition> fieldDef = new ArrayList<FieldDefinition>();
	
		FieldDefinition[] fd = null;
		
		for (String field : fields) {
			fieldDef.add(new StringFieldDefinition(field));
		}
		
		fd = fieldDef.toArray(new FieldDefinition[fieldDef.size()]);

		return fd;
	}
	
	
	
	public static void printRecords(List<GenericRecord> recs, Set<String> fields) throws GRS2RecordDefinitionException, GRS2BufferException {
		for (GenericRecord rec : recs) {
			for (String field : fields) {
				LOGGER.info(((StringField)rec.getField(field)).getPayload());
			}
			LOGGER.info("");
			LOGGER.info("Record : "  + rec.getID());
		}
	}
	
	/**
	 * Gets the names of the fields for the given record
	 * @param rsRec
	 * @return list of field names
	 * @throws GRS2RecordDefinitionException
	 */
	public static List<String> getRSRecFields(GenericRecord rsRec) throws GRS2RecordDefinitionException {
		List<String> fields = new ArrayList<String>();
		
		for (gr.uoa.di.madgik.grs.record.field.Field f : rsRec.getFields()) {
			String fieldName = f.getFieldDefinition().getName();
			fields.add(fieldName);
		}
		
		return fields;
	}
	
}
