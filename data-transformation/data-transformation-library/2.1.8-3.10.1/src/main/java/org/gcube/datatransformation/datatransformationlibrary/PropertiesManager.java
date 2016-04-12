package org.gcube.datatransformation.datatransformationlibrary;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * This class supplies the DataTransformationLibrary components with the properties defined in the "dtslib.properties" file.
 */
public class PropertiesManager {

	/**
	 * The singleton {@link Properties} object.
	 */
	private static Properties dtsLibProperties = new Properties();
	
	/**
	 * Logs operations performed by {@link PropertiesManager} class.
	 */
	private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);
	
	/**
	 * The properties file name.
	 */
	private static final String DTSLIBPROPERTIESFILE = "/dtslib.properties";
	static{
		try {
			/* 
			 * Getting the current-globus class loader which finds DataTransformationLibrary jar in GLOBUS_LOCATION/lib
			 * The system classloader doesn't work... 
			 * */
			dtsLibProperties.load(PropertiesManager.class.getResourceAsStream(DTSLIBPROPERTIESFILE));
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			dtsLibProperties.storeToXML(os, null);
			os.flush();
			log.trace("Loaded Properties: " + os.toString());
			
			log.info("Managed to load dts lib properties");
		} catch (Exception e) {
			log.error("Did not manage to load dts lib properties", e);
		}
	}
	
	/**
	 * Returns the properties for DataTransformationLibrary.
	 * 
	 * @return The singleton properties object.
	 */
	public static Properties getDTSLibProperties(){
		return dtsLibProperties;
	}
	
	/**
	 * Returns the value of the property with name propertyName.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property.
	 */
	public static String getPropertyValue(String propertyName, String defaultValue){
		return dtsLibProperties.getProperty(propertyName, defaultValue);
	}
	
	/**
	 * Returns the value of the property with name propertyName in milliseconds.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property in milliseconds.
	 */
	public static long getInMillisPropertyValue(String propertyName, String defaultValue){
		return Long.parseLong(dtsLibProperties.getProperty(propertyName, defaultValue)) * 1000;
	}
	
	/**
	 * Returns the value of the property with name propertyName in long datatype.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property in long datatype.
	 */
	public static long getLongPropertyValue(String propertyName, String defaultValue){
		return Long.parseLong(dtsLibProperties.getProperty(propertyName, defaultValue));
	}
	
	/**
	 * Returns the value of the property with name propertyName in int datatype.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property in int datatype.
	 */
	public static int getIntPropertyValue(String propertyName, String defaultValue){
		return Integer.parseInt(dtsLibProperties.getProperty(propertyName, defaultValue));
	}
	
	/**
	 * Returns the value of the property with name propertyName in double datatype.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property in double datatype.
	 */
	public static double getDoublePropertyValue(String propertyName, String defaultValue){
		return Double.parseDouble(dtsLibProperties.getProperty(propertyName, defaultValue));
	}
	/**
	 * Returns the value of the property with name propertyName in boolean datatype.
	 * 
	 * @param propertyName The name of the property.
	 * @param defaultValue The default value if the property is not contained in the properties file. 
	 * @return The value of the property in boolean datatype.
	 */
	public static boolean getBooleanPropertyValue(String propertyName, String defaultValue){
		return Boolean.parseBoolean(dtsLibProperties.getProperty(propertyName, defaultValue));
	}
}
