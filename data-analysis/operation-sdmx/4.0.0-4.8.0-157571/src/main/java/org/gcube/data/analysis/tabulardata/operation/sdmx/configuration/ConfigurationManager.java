package org.gcube.data.analysis.tabulardata.operation.sdmx.configuration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager 
{
	private Logger logger;
	private Properties genericConfiguration;
	private static ConfigurationManager instance;
	
	private ConfigurationManager ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.genericConfiguration = new Properties();
		try
		{
			this.genericConfiguration.load(this.getClass().getResourceAsStream("/generic-settings.properties"));
			
		} catch (Exception e)
		{
			this.logger.error("Unable to load configuration file",e);
		}
		
	}

	public static ConfigurationManager getInstance ()
	{
		if (instance == null) instance = new ConfigurationManager();
		
		return instance;
	}
	
	public String getValue (String name)
	{
		return this.genericConfiguration.getProperty(name);
	}
	

}
