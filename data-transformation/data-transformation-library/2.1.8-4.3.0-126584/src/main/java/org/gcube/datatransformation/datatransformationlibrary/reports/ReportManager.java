package org.gcube.datatransformation.datatransformationlibrary.reports;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>ReportManager</tt> is responsible to maintain reports for the performed transformations.
 */
public class ReportManager {

	private static HashMap<String, Report> reports = new HashMap<String, Report>();
	private static Logger log = LoggerFactory.getLogger(ReportManager.class);
	
	//Report ID...
    private static InheritableThreadLocal<String> reportID = new InheritableThreadLocal<String>();
    private static InheritableThreadLocal<Boolean> reportEnabled = new InheritableThreadLocal<Boolean>(){
    	protected Boolean initialValue(){
			return false;
		}
    };
    
    protected static String getReportID() {
    	log.trace("Returning report ID "+reportID.get());
        return reportID.get();
    }
    
    /**
     * Checks if the reporting mechanism is enabled for the transformationUnit which is performed.
     * 
     * @return true if the reporting mechanism is enabled.
     */
    public static boolean isReportingEnabled() {
        return reportEnabled.get();
    }
    
    /**
     * Initializes the reporting mechanism.
     * 
     * @param create If true then a new report is created and the reporting mechanism is enabled.
     * @throws Exception If the report could not be created.
     */
    public static void initializeReport(boolean create) throws Exception {
    	if(create){
			createReport();
    	}else{
    		//Due to the thread locality of the variables and the thread pooling of the container...
    		reportID.set(null);
    		reportEnabled.set(false);
    	}
    	
    }
    
	protected static Report createReport() throws Exception {
		Report report=null;
		try {
			report = new Report();
		} catch (Exception e) {
			log.error("Could not create report",e);
			reportID.set(null);
    		reportEnabled.set(false);
			throw new Exception("Could not create report");
		}
		String newReportID = UUID.randomUUID().toString();
		report.reportID = newReportID; 
		reportID.set(newReportID);
		reportEnabled.set(true);
		reports.put(reportID.get(), report);
		
		return report;
	}
	
	/**
	 * Returns the report.
	 * 
	 * @return The report
	 * @throws Exception If no report could be found.
	 */
	public static Report getReport() throws Exception {
		Report report = reports.get(reportID.get());
		if(report==null){
			throw new Exception("Report for transaction "+reportID.get()+" does not exist");
		}
		return report;
	}
	
	/**
	 * Closes the report.
	 */
	public static void closeReport() {
		if(isReportingEnabled()){
			try {
				Report report = getReport();
				log.debug("Closing report with id "+report.reportID);
				Set<Entry<String, Record>> uncommitedRecords = report.records.entrySet();
				if(!uncommitedRecords.isEmpty()){
					log.warn("There are uncommited records in the report");
					Iterator<Entry<String, Record>> it = uncommitedRecords.iterator();
					while(it.hasNext()){
						Entry<String, Record> entry = it.next();
						Record rec = entry.getValue();
						log.warn("ObjectID "+entry.getKey());
						log.warn("Payload "+rec.toString());
					}
					report.records.clear();
				}
				report.close();
				reports.remove(reportID.get());
			} catch (Exception e) {
				log.error("Could not close repornt ",e);
			}
		}
	}
	
	/**
	 * If the reporting mechanism is enabled then a record is appended in the report.
	 * 
	 * @param objectID The id of the <tt>DataElement</tt>.
	 * @param msg The message of the record. 
	 * @param status The status of the logged operation.
	 * @param type The type of the logged operation.
	 */
	public static void manageRecord(String objectID, String msg, Status status, Type type){
		try {
//			log.trace("ReportingEnabled: "+isReportingEnabled());
			if(isReportingEnabled()){
				Record rec = getReport().getRecord(objectID);
				rec.setRecord(msg, status, type);
			}
		} catch (Exception e) {
			log.error("Reporting failed ",e);
		}
	}
}


   

   
