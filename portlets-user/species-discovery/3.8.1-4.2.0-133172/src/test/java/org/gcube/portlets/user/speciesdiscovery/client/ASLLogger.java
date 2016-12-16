/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.accesslogger.model.LoginToVreAccessLogEntry;
import org.gcube.application.framework.accesslogger.model.SimpleSearchAccessLogEntry;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 4, 2013
 *
 */
public class ASLLogger {
	
	
	static AccessLogger log = AccessLogger.getAccessLogger();
	static LoginToVreAccessLogEntry loginEntry = new LoginToVreAccessLogEntry();
	static String user = "test.user";
	static String scope = "/gcube/devsec/devVRE";
	
//	static final String SPECIES = "SPECIES";
//	static final String DATASOURCE_SEARCHING = "DATASOURCE SEARCHING";
//	static final String DATASOURCE_DOWLOADED = "DATASOURCE DOWLOADED";
//	static final String TAXA_QUERY = "TAXA QUERY";
//	static final String OCCURRENCE_QUERY = "OCCURRENCE QUERY";
//	
	
	public static void main(String[] args) {
		
	
		
		
		/*
		* The first argument is the username of the user that performs the action
		* The second argument is the name of the working VO/VRE
		*/
		log.logEntry(user, scope, loginEntry);
		 
//		String collections[][] = new String[2][2];
//		collections[0][0] = "Earth images";
//		collections[0][1] = "12345";
//		collections[1][0] = "Landsat 7";
//		collections[1][1] = "54321";
//		
//		
//		SimpleSearchAccessLogEntry simpleEntry = new SimpleSearchAccessLogEntry(collections, "satellite, sea");
//		/*
//		* The first argument is the username of the user that performs the action
//		* The second argument is the name of the working VO/VRE
//		*/
//		log.logEntry(user, scope, simpleEntry);
//		
		
		
		//DATA SOURCE
		
		String query[][] = new String[3][3];
		query[0][0] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getName();
		query[0][1] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getId();
		query[1][0] = SPDAccessLoggerType.BY_SCIENTIFIC_NAME.getName();
		query[1][1] = SPDAccessLoggerType.BY_SCIENTIFIC_NAME.getId();
//		dataSources[1][0] = "Carcharodon carcharias";
//		dataSources[1][1] = SPDAccessLoggerType.TERM_SEARCHED.getId();
		query[2][0] = "GBIF";
		query[2][1] = SPDAccessLoggerType.DATASOURCE_USED.getId();
		
		SimpleSearchAccessLogEntry simpleEntry = new SimpleSearchAccessLogEntry(query, "Carcharodon carcharias");
		
		log.logEntry(user, scope, simpleEntry);
		
		
		//species
		query = new String[3][3];
		query[0][0] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getName();
		query[0][1] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getId();
		query[1][0] = SPDAccessLoggerType.BY_SCIENTIFIC_NAME.getName();
		query[1][1] = SPDAccessLoggerType.BY_SCIENTIFIC_NAME.getId();
//		dataSources[1][0] = "Carcharodon carcharias";
//		dataSources[1][1] = SPDAccessLoggerType.TERM_SEARCHED.getId();
		query[2][0] = "GBIF; OBIS";
		query[2][1] = SPDAccessLoggerType.DATASOURCE_USED.getId();
		
		simpleEntry = new SimpleSearchAccessLogEntry(query, "Carcharodon carcharias; sarda");
		
		log.logEntry(user, scope, simpleEntry);
		
		
		//species
		String job[][] = new String[3][3];
		job[0][0] = SPDAccessLoggerType.JOB_SUBMITTED.getName();
		job[0][1] = SPDAccessLoggerType.JOB_SUBMITTED.getId();
		job[1][0] = SPDAccessLoggerType.FOR_TAXON.getName();
		job[1][1] = SPDAccessLoggerType.FOR_TAXON.getId();
//		dataSources[1][0] = "Carcharodon carcharias";
//		dataSources[1][1] = SPDAccessLoggerType.TERM_SEARCHED.getId();
		job[2][0] = "GBIF; OBIS";
		job[2][1] = SPDAccessLoggerType.DATASOURCE_JOB_USED.getId();
		
		simpleEntry = new SimpleSearchAccessLogEntry(job, "Taxa 1- sarda");
		
		log.logEntry(user, scope, simpleEntry);
		
//		//species
//		
//		species = new String[2][2];
//		species[0][0] = "Stock";
//		species[0][1] = SPDAccessLoggerType.OCCURRENCE_QUERIED.getId();
//		species[1][0] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getName();
//		species[1][1] = SPDAccessLoggerType.SEARCH_OCCURRENCE.getId();
//		
//		simpleEntry = new SimpleSearchAccessLogEntry(species, "Stock");
//		log.logEntry(user, scope, simpleEntry);
		
		System.out.println("completed");
	}
	
	

}
