package org.gcube.datatransformation.datatransformationlibrary.reports;

import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <p><tt>Report</tt> is the class responsible to maintain all the {@link Record}s for one transformationUnit.</p> 
 */
public class Report {

	protected HashMap<String, Record> records = new HashMap<String, Record>();
	
	private RecordWriter<GenericRecord> writer = null;
	
	private static Logger log = LoggerFactory.getLogger(Report.class);
	
	protected String reportID;
	
	/**
	 * Initializes a new <tt>Report</tt>.
	 * 
	 * @throws Exception If the {@link RSXMLWriter} in which the <tt>Report</tt> will be written could not be created.
	 */
	public Report() throws Exception {
		try {
			RecordDefinition[] defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition("objectID"), new StringFieldDefinition("reportID"), new StringFieldDefinition("report")})) };
			writer = new RecordWriter<GenericRecord>(new TCPWriterProxy(), defs, RecordWriter.DefaultBufferCapacity,
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
		} catch (Exception e) {
			log.error("Could not create RSXMLWriter",e);
			throw new Exception("Could not create RSXMLWriter");
		}
	}
	
	/**
	 * Returns the endpoint of the <tt>ResultSet</tt> which contains the <tt>Report</tt>.
	 * 
	 * @return The endpoint of the <tt>ResultSet</tt> which contains the <tt>Report</tt>.
	 * @throws Exception If the {@link RSLocator} of the <tt>ResultSet</tt> could not be retrieved.
	 */
	public String getReportEndpoint() throws Exception {
		try {
			throw new Exception("Not implemented yet.");
//			log.debug("Creating RSLocator at scope "+DTSScope.getScope());
//			return writer.getRSLocator(new RSResourceWSRFType(), DTSScope.getScope()).getLocator();
		} catch (Exception e) {
			log.error("Could not create RSLocator of the report result set", e);
			throw new Exception("Could not create RSLocator of the report result set");
		}
	}
	
	private Record createRecord(String objectID){
		Record record = new Record();
		record.report=this;
		record.objectID=objectID;
		records.put(objectID, record);
		return record;
	}
	
	/**
	 * Returns the <tt>Record</tt> of the given <tt>DataElement</tt> ID.
	 * 
	 * @param objectID The <tt>DataElement</tt> ID.
	 * @return The <tt>Record</tt>.
	 */
	public Record getRecord(String objectID){
		Record rec = records.get(objectID);
		if(rec==null){
			rec = createRecord(objectID);
		}
		return rec;
	}
	
	protected void commitRecord(Record record){
		//Write record...
		String payload = record.toString();
		try {
			GenericRecord rec = new GenericRecord();
			rec.setFields(new Field[] { new StringField(record.objectID), new StringField(ReportManager.getReportID()), new StringField(payload) });
			writer.put(rec);
			records.remove(record.objectID);
		} catch (Exception e) {
			log.error("Could not add record into result set",e);
		}
	}
	
	protected void close(){
		try {
			records=null;
			writer.close();
			writer=null;
		} catch (Exception e) {
			log.error("Could not close RSXMLWriter",e);
		}
	}
}
