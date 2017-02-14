package gr.uoa.di.madgik.taskexecutionlogger.utils;

import gr.uoa.di.madgik.taskexecutionlogger.exceptions.PropertiesFileRetrievalException;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class for file operations
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FileUtils {
	
	/**
	 * Retrieves the value of a property of the given file
	 * @param fileName The file's path
	 * @param propertyName The property's name
	 * @return The property's value or null
	 * @throws PropertiesFileRetrievalException
	 */
	public static String getPropertyValue(String fileName, String propertyName) throws PropertiesFileRetrievalException {
		Properties props = new Properties();
		String value = null;
		try {	
			URL res = Thread.currentThread().getContextClassLoader().getResource(fileName);

			if (res == null)
			{
				throw new PropertiesFileRetrievalException("Properties file could not be found");
			}
			props.load(res.openStream());
			value = props.getProperty(propertyName);
			if (value == null)
				throw new PropertiesFileRetrievalException("Requested property could not be found");
		}
		catch(IOException e) {
			throw new PropertiesFileRetrievalException(e.getMessage(), e.getCause());
		}
		return value;
	}

}
