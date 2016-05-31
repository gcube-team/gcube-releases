package org.gcube.application.framework.oaipmh.objectmappers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.gcube.application.framework.oaipmh.constants.MetadataConstants;
import org.gcube.application.framework.oaipmh.verbcontainers.Identify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all OAI-PMH info about a repository.
 * @author nikolas
 *
 */
public class Repository {
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Repository.class);
	
	
	private String name;
	private String baseURL;
	private ArrayList <String> adminEmail;
	private Date earliestDatestamp;
	private String deletedRecord; //could also be Enum, to be type-safe {no,persistent,transient} 
	private String granularity;
	private String protocolVersion;

	private ArrayList<String> supportedMetadataPrefixes; //this holds the metadata prefixes e.g. "oai_dc", for standard metadata formats
	private CustomMetadataXSD customMetadataXSD; //the object which holds the custom metadata information and generates the XSD
	private OAIDCMetadataXSD oaiDcMetadataXSD; //the object which holds the oai-dc metadata information (does not create an XSD file)
	
	private RecordTemplateCustom recordTemplateCustom;
	private RecordTemplateDC recordTemplateDC;
	
	//optional
	private Properties sets; //this includes the (setName,setSpec) pairs for selective harvesting
	
	
	
	public Repository(String name, String baseURL, ArrayList <String> adminEmail, Date earliestDatestamp, RecordTemplateDC recordTemplateDC, RecordTemplateCustom recordTemplateCustom, Properties sets) throws MalformedURLException {
		if(supportedMetadataPrefixes==null)
			this.supportedMetadataPrefixes = new ArrayList<String>();
		this.granularity = "YYYY-MM-DD";  //don't change this, unless you know what you 're doing
		this.name = name;
		this.baseURL = baseURL;
		this.adminEmail = adminEmail;
		this.deletedRecord = "no"; //that's always "no" if our repository can't handle history of records
		this.protocolVersion = "2.0"; //this implementation conforms to version 2.0
		this.earliestDatestamp = earliestDatestamp;
		this.oaiDcMetadataXSD = new OAIDCMetadataXSD();
		if(recordTemplateDC!=null){
			this.supportedMetadataPrefixes.add("oai_dc");
			this.recordTemplateDC = recordTemplateDC;
		}
		this.supportedMetadataPrefixes.add("custom");
		this.recordTemplateCustom = recordTemplateCustom;
		URL url = new URL(baseURL);
		this.customMetadataXSD = new CustomMetadataXSD(name, url.getHost() , url.getPort());
		//the sets are optional
		if((sets==null)||sets.isEmpty()){
			this.sets = new Properties();
			this.sets.put(name, "Single set which contains all records of the repository");
		}
		else
			this.sets = sets;
	}
	
	
	
	/**
	 * 
	 * @param name could be same as baseURL
	 * @param baseURL 
	 * @param adminEmail
	 * @param sets this should include any (setSpec, setName) pairs for selective harvesting. If none is set (=null), a default one with the repository name (as setSpec) is assigned.
	 * @throws MalformedURLException 
	 */
	public Repository(String name, String baseURL, ArrayList <String> adminEmail, Date earliestDatestamp, RecordTemplateDC recordTemplateDC, Properties sets) throws MalformedURLException {
		if(supportedMetadataPrefixes==null)
			this.supportedMetadataPrefixes = new ArrayList<String>();
		this.granularity = "YYYY-MM-DD";  //don't change this, unless you know what you 're doing
		this.name = name;
		this.baseURL = baseURL;
		this.adminEmail = adminEmail;
		this.deletedRecord = "no"; //that's always "no" if our repository can't handle history of records
		this.protocolVersion = "2.0"; //this implementation conforms to version 2.0
		this.earliestDatestamp = earliestDatestamp;
		this.supportedMetadataPrefixes.add(MetadataConstants.DCNAME);
		this.recordTemplateDC = recordTemplateDC;
		this.oaiDcMetadataXSD = new OAIDCMetadataXSD();
		//the sets are optional
		if((sets==null)||sets.isEmpty()){
			this.sets = new Properties();
			this.sets.put(name, "Single set which contains all records of the repository");
		}
		else
			this.sets = sets;
	}
	
	
	/**
	 * 
	 * @param name could be same as baseURL
	 * @param baseURL 
	 * @param adminEmail
	 * @param sets this should include any (setSpec, setName) pairs for selective harvesting. If none is set (=null), a default one with the repository name (as setSpec) is assigned.
	 * @throws MalformedURLException 
	 */
	public Repository(String name, String baseURL, ArrayList <String> adminEmail, Date earliestDatestamp, RecordTemplateCustom recordTemplateCustom, Properties sets) throws MalformedURLException {
		this.supportedMetadataPrefixes = new ArrayList<String>();
		this.granularity = "YYYY-MM-DD";  //don't change this, unless you know what you 're doing
		this.name = name;
		this.baseURL = baseURL;
		this.adminEmail = adminEmail;
		this.deletedRecord = "no"; //that's always "no" if our repository can't handle history of records
		this.protocolVersion = "2.0"; //this implementation conforms to version 2.0
		this.earliestDatestamp = earliestDatestamp;
		this.supportedMetadataPrefixes.add("custom");
		this.recordTemplateCustom = recordTemplateCustom;
		URL url = new URL(baseURL);
		this.customMetadataXSD = new CustomMetadataXSD(name, url.getHost() , url.getPort());
		//the sets are optional
		if((sets==null)||sets.isEmpty()){
			this.sets = new Properties();
			this.sets.put(name, "Single set which contains all records of the repository");
		}
		else
			this.sets = sets;
	}
	
	

//	/**
//	 * initiates the "customMetadataXSD" object
//	 * SHOULD ALSO CALL THE POPULATE TO POPULATE WITH THE MAPPINGS
//	 * @param name
//	 * @param hostname
//	 * @param port
//	 */
//	public void initiateCustomMetadataXSD(String hostname, int port){
//		customMetadataXSD = new CustomMetadataXSD(recordTemplate.getRecordName(), hostname, port);
//	}
	
//	public void addCustomFieldXSD(String fieldName){
//		customMetadataXSD.addCustomField(fieldName, "0", "1", "string");
//	}
	
	/**
	 * SHOULD be called just after creating a new Repository (needs to be run just after the Repository class constructor)
	 * @throws TransformerException
	 */
	public void materializeXSDonFilesystem() throws TransformerException{
		customMetadataXSD.materializeXSDonFilesystem(recordTemplateCustom);
	}
	
	
	public String getName(){
		return name;
	}
	
	public String getBaseURL(){
		return baseURL;
	}
	
	public ArrayList<String> getAdminEMails(){
		return adminEmail;
	}
	
	public String typeDeletedRecord(){
		return deletedRecord;
	}
	
	public Properties getSets(){
		return sets;
	}
	
	public String getProtocolVersion(){
		return protocolVersion;
	}
	
	public String getEarliestDatestamp(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(earliestDatestamp);
	}
	
	public String getGranularity(){
		return granularity;
	}
	
	public ArrayList<String> getSupportedMetadataPrefixes(){
		return supportedMetadataPrefixes;
	}
	
	public CustomMetadataXSD getCustomMetadataXSD(){
		return customMetadataXSD;
	}
	
	public OAIDCMetadataXSD getOAIDCMetadataXSD(){
		return oaiDcMetadataXSD;
	}
	
	
	public RecordTemplateCustom getRecordTemplateCustom(){
		return recordTemplateCustom;
	}
	
	public RecordTemplateDC getRecordTemplateDC(){
		return recordTemplateDC;
	}
	
	
	/**
	 * this function checks whether all mandatory info for the repository is set
	 * @return
	 */
//	public boolean checkAllMandatoryFieldsSet(){
//		
//	}
	
}
