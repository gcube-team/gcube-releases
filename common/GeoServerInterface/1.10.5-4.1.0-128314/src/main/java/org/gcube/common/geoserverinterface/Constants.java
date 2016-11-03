/**
 * 
 */
package org.gcube.common.geoserverinterface;

import java.io.IOException;
import java.util.Properties;

/**
 * @author ceras
 *
 */
public class Constants {	

	private static final String PROPERTIES_FILE_NAME = "geoserverInterface.properties";
	
	
	
//	private static boolean isInitialized = false;
	
	private static int connectionTimeOut;
	private static boolean log4jEnabled;
	private static String geoserverDataAbsolutePath;

	private static int geonetwork_group_id;
	
	
	private static String genericResourceSecondaryType;
	private static String genericResourceName;
	private static long metadataConfigurationTimeToLive;
	
	static {
		Properties properties = new Properties();
		try {
			properties.load(Constants.class.getResourceAsStream("/"+PROPERTIES_FILE_NAME));
//			isInitialized = true;
			
			connectionTimeOut = Integer.parseInt(properties.getProperty("connectionTimeOut", "0"));
			log4jEnabled = Boolean.parseBoolean(properties.getProperty("log4jEnabled", "false"));
			geoserverDataAbsolutePath = properties.getProperty("geoserverDataAbsolutePath", "/usr/share/apache-tomcat-6.0.33/webapps/geoserver/data/data/");
			
			geonetwork_group_id=Integer.parseInt(properties.getProperty("geonetworkGroupId", "2"));
			
			
			
			genericResourceSecondaryType=properties.getProperty("genericResourceSecondaryType","ISO");
			genericResourceName=properties.getProperty("genericResourceName","MetadataConstants");
			metadataConfigurationTimeToLive=Long.parseLong(properties.getProperty("metadataConfigurationTimeToLive", "12000"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getConnectionTimeOut() {		
//		if (!isInitialized)
//			init();
		return connectionTimeOut;
	}
	
	public static void main(String args[]) {
		System.out.println("connection time out="+Constants.getConnectionTimeOut());
	}

	/**
	 * @return
	 */
	public static boolean log4jEnabled() {
//		if (!isInitialized)
//			init();
		return log4jEnabled;
	}

	/**
	 * @return
	 */
	public static String getGeoserverDataAbsolutePath() {
		return geoserverDataAbsolutePath ;
	}

	public static int getGeoNetworkPublishGroupId(){
		return geonetwork_group_id;
	}
	
	public static String getGenericResourceName() {
		return genericResourceName;
	}
	public static String getGenericResourceSecondaryType() {
		return genericResourceSecondaryType;
	}
	public static long getMetadataConfigurationTimeToLive() {
		return metadataConfigurationTimeToLive;
	}	
	
}
