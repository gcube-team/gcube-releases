package org.gcube.datatransformation.adaptors.common.constants;

public class ConstantNames {
	
	//properties file
	public static final String PROPERTIES_FILE = "deploy.properties";
//	public static final String DEPLOY_FILEPATH = "deploy.properties";

	// the property names within the above file
	public static final String SCOPENAME = "scope";
	public static final String RESOURCE_FOLDERNAME_PATH = "resourcesFoldername"; //where a local copy of the resources, (sync'd with the server) will be kept 
	public static final String HOSTNAME = "hostname";
	public static final String PORT = "port";

	//for all harvesters
	public static final String SERVICE_CLASS = "HarvesterService";
	public static final String RESOURCE_CLASS = "HarvesterResource";
	public static final String ENDPOINT_KEY = "resteasy-servlet";
	public static final String DEFAULT_RESOURCES_FOLDERNAME = "/tmp/resources";
	
	//for DB harvesters
	public static final String DBSOURCETYPE = "RelationalDB"; 
	public static final String DBPROPS_FILEPATH = "DBProps.xml"; // Database structure properties file path
	public static final String SERVICE_NAME_DB = "DBService";
	public static final String RESOURCE_NAME_PREF_DB = "DBResource";
	
	//for OAIPMH harvesters
	public static final String OAIPMHSOURCETYPE = "OAI-PMH"; 
	public static final String SERVICE_NAME_OAIPMH = "OAIPMHService";
	public static final String RESOURCE_NAME_PREF_OAIPMH = "OAIPMHResource";
	
	//for Tree collection harvesters
	public static final String TREESOURCETYPE = "Tree"; 
	public static final String SERVICE_NAME_TREE = "TreeService";
	public static final String RESOURCE_NAME_PREF_TREE = "TreeResource";
	
	
	
	
}
