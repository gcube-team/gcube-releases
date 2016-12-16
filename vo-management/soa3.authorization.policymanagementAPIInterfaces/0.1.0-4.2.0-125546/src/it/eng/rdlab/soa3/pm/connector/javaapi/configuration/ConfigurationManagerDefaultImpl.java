package it.eng.rdlab.soa3.pm.connector.javaapi.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Configuration Manager
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class ConfigurationManagerDefaultImpl implements ConfigurationManager
{
	
	private Logger logger;
	private Properties properties;
	
	protected ConfigurationManagerDefaultImpl ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		Properties files = getConfigurationFiles();
		String root = files.getProperty(ConfigurationConstants.CONFIGURATION_ROOT,"");
		String path = files.getProperty(ConfigurationConstants.CONFIGURATION_FILE,"");
		
		if (root != null && root.startsWith("$"))
		{
			root = System.getProperty(root.substring(1));
			logger.debug("Actual root "+root);
		}
	
		path = root+path;
		logger.debug("Configuration path = "+path);
		
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(path));
		} catch (Exception e) 
		{
			this.logger.warn("Configuration file not found",e);
			this.logger.warn("Using default values");
			
			try {
				this.properties.load(this.getClass().getResourceAsStream(ConfigurationConstants.DEFAULT_PROP_FILE));
			} catch (IOException e1) 
			{
				this.logger.error("Unable to find properties from the classpath",e);
			}

		} 
	}
	
	private Properties getConfigurationFiles ()
	{
		Properties fileProperties = new Properties();
		try 
		{
			fileProperties.load(this.getClass().getResourceAsStream(ConfigurationConstants.CONFIGURATION_RES_FILE));
		} catch (IOException e1) 
		{
			this.logger.error("Unable to find properties from the classpath",e1);
			
		}
		
		return fileProperties;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPolicyManagerUrl ()
	{
		return this.properties.getProperty(ConfigurationConstants.POLICY_REPOSITORY_URL,ConfigurationConstants.POLICY_REPOSITORY_DEFAULT_URL);
	}

	@Override
	public String getAuthQueryEndpoint() 
	{
		return this.properties.getProperty(ConfigurationConstants.AUTHZ_QUERY_ENDPOINT,ConfigurationConstants.AUTHZ_QUERY_DEFAULT_ENDPOINT);

	}

	@Override
	public String getPolicyLoaderUrl() 
	{
		return this.properties.getProperty(ConfigurationConstants.POLICY_LOADER_URL);

	}

	@Override
	public boolean getIndeterminateDecision() 
	{
		
		logger.debug("Indeterminate decision");
		boolean indeterminateDecision = false;
		
		try
		{
			String indeterminateDecisionString =  this.properties.getProperty(ConfigurationConstants.POLICY_LOADER_URL);
			logger.debug("Indeterminate decision String "+indeterminateDecisionString);
			indeterminateDecision = indeterminateDecisionString.equalsIgnoreCase("true");
			
		} catch (Exception e)
		{
			logger.debug("Parameter not found");
		}
		
		logger.debug("Indeterminate decision "+indeterminateDecision);
		return indeterminateDecision;
	}
	
	@Override
	public boolean explicitFinalStatement() 
	{
		logger.debug("Indeterminate decision");
		boolean explicitFinalStatement = false;
		
		try
		{
			String explicitFinalStatementString =  this.properties.getProperty(ConfigurationConstants.EXPLICIT_FINAL_STATEMENT);
			logger.debug("Explicit final statement String "+explicitFinalStatementString);
			explicitFinalStatement = explicitFinalStatementString.equalsIgnoreCase("true");
			
		} catch (Exception e)
		{
			logger.debug("Parameter not found");
		}
		
		logger.debug("Explicit final statement  "+explicitFinalStatement);
		return explicitFinalStatement;
	}

}
