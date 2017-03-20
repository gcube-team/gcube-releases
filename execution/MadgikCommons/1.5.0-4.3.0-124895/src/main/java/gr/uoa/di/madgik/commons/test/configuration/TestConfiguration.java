package gr.uoa.di.madgik.commons.test.configuration;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gpapanikos
 */
public class TestConfiguration
{

	private static Logger logger = Logger.getLogger(TestConfiguration.class.getName());

	/**
	 * 
	 * @param args sdfg
	 * @throws java.lang.Exception sgdf
	 */
	public static void main(String[] args) throws Exception
	{
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Run... " + ConfigurationManager.GetGonfigurationFile().getAbsolutePath());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetIntegerParameter("intValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetFloatParameter("floatValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetDoubleParameter("doubleValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetShortParameter("shortValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetBooleanParameter("booleanValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetByteParameter("byteValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetLongParameter("longValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetStringParameter("stringValue1").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetStringParameter("stringValue2").toString());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, ConfigurationManager.GetParameter("objectValue1").toString());
	}
}
