package org.gcube.data.analysis.statisticalmanager;

import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static Properties properties=new Properties();;
	
	public static String getConfigPath() {
		return ServiceContext.getContext().getProperty("configDir") + "/cfg/";
	}	
	
	static {		
		try {			
			properties.load(Configuration.class.getResourceAsStream("/configuration.properties"));
		} catch (IOException e) {
			e.printStackTrace(System.err);			
		}
		
	}
	
	
	public static String getProperty(String propertyName){
		return properties.getProperty(propertyName);
	}
	
	
	//************************ PROPERTIES NAMES
	
	//*********************** JMS
	public static String JMS_SERVICE_CLASS="JMS_SERVICE_CLASS";
	public static String JMS_SERVICE_NAME="JMS_SERVICE_NAME";
	public static String JMS_TOPIC="JMS_TOPIC";
	public static String JMS_MESSAGE_COMPUTATION_ID="JMS_MESSAGE_COMPUTATION_ID";
	public static String JMS_MESSAGE_SCOPE="JMS_MESSAGE_SCOPE";
	public static String JMS_MESSAGE_REQUEST="JMS_MESSAGE_REQUEST";
	public static String JMS_SERVICE_ENDPOINT_NAME="JMS_SERVICE_ENDPOINT_NAME";
	public static String JMS_SERVICE_ENDPOINT_CATEGORY="JMS_SERVICE_ENDPOINT_CATEGORY";
	
	
	//*********************** IS
	
	public static final String RUNTIME_RESOURCE_DB ="RUNTIME_RESOURCE_DB"; 
	public static final String GR_SECONDARY_TYPE = "GR_SECONDARY_TYPE";
	
	public static final String SERVICE_PARAMETER_DATABASE_NAME="SERVICE_PARAMETER_DATABASE_NAME";
	public static final String SERVICE_PARAMETER_DIALECT="SERVICE_PARAMETER_DIALECT";
	public static final String SERVICE_PARAMETER_DRIVER="SERVICE_PARAMETER_DRIVER";
	
	//*********************** HL
	
	public static final String WS_APPLICATION_FOLDER="WS_APPLICATION_FOLDER";
	
	//*********************** Algorithm
	public static final String SKIP_FAULTY_ALGORITHMS="SKIP_FAULTY_ALGORITHMS";
	
	//*********************** DataSpace
	public static final String FORCE_COMPUTATION_REMOVAL="FORCE_COMPUTATION_REMOVAL";
}
