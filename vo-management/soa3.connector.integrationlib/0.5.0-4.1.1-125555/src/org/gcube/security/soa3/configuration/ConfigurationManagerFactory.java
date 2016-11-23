package org.gcube.security.soa3.configuration;

import org.gcube.security.soa3.configuration.impl.DefaultConfigurationManager;

public class ConfigurationManagerFactory 
{
	
	private static ConfigurationManager configuration;
	
	public static void setConfigurationManager (ConfigurationManager configurationManager)
	{
		configuration = configurationManager;
	}
	
	public static ConfigurationManager getConfigurationManager ()
	{
		if (configuration == null) configuration = new DefaultConfigurationManager ();
		
		return configuration;
	}

}
