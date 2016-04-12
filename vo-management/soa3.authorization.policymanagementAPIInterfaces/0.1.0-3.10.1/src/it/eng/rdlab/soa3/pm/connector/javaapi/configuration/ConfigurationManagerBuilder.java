package it.eng.rdlab.soa3.pm.connector.javaapi.configuration;

/**
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class ConfigurationManagerBuilder 
{
	
	private static ConfigurationManager instance;
	
	/**
	 * 
	 * Provides the current instance of configuration manager
	 * 
	 * @return the configuration manager
	 */
	public static ConfigurationManager getConfigurationManager ()
	{
		if (instance == null) instance = new ConfigurationManagerDefaultImpl ();
		
		return instance;
	}
	
	/**
	 * 
	 * Sets a configuration manager instance different than the default one
	 * 
	 * @param newInstance
	 */
	public static void setConfigurationManagerInstance (ConfigurationManager newInstance)
	{
		instance = newInstance;
	}

}
