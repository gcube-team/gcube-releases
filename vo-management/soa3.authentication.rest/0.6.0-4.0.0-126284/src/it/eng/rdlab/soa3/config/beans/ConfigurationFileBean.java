package it.eng.rdlab.soa3.config.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationFileBean 
{
	private Log log;
	
	private String basePath,
					filePath,
					classpathFilePath;
	private InputStream configurationStream;
	
	public ConfigurationFileBean(String basePath, String filePath, String classpathFilePath) 
	{
		this.log = LogFactory.getLog(this.getClass());
		this.basePath = basePath;
		this.filePath = filePath;
		this.classpathFilePath = classpathFilePath;
		this.configurationStream = null;
	}
	
	public void init ()
	{
		if (basePath != null && basePath.startsWith("$"))
		{
			basePath = basePath.substring(1);
			String evnVar = System.getProperty(basePath);
			
			if (evnVar == null) 
			{
				log.warn("Unable to find env variable "+evnVar );
				basePath = "";
			}
			else basePath = evnVar;
			
			log.debug("Base path = "+basePath);
		}
		else if (basePath == null) basePath = "";
		
		String configurationFile = basePath+File.separatorChar+filePath;
		this.log.debug("Configuration file = "+configurationFile);
		
		try 
		{
			this.configurationStream = new FileInputStream(configurationFile);
		} catch (Exception e) 
		{
			this.log.error("Unable to use the configuration file ",e);
			
			if (this.classpathFilePath != null)
			{
				this.log.debug("Using default file "+this.classpathFilePath);
				this.configurationStream = this.getClass().getResourceAsStream(this.classpathFilePath);
			}
			else log.error("Unable to find a valid configuration");
		}
	}

	public InputStream getConfigurationStream() {
		return configurationStream;
	}
	
	

}
