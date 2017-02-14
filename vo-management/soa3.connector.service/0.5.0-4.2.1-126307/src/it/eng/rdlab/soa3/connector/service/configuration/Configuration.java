package it.eng.rdlab.soa3.connector.service.configuration;

import it.eng.rdlab.soa3.connector.utils.SecurityManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Configuration manager singleton class
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class Configuration implements ConfigurationDefaults
{

	private static Configuration instance;
	private Properties 	genericProperties,
						defaultProperties,
						serviceUrlMap;
	private Log logger;
	
	private final String 	CONFIGURATION_ROOT = "CONFIGURATION_ROOT",
							CONFIGURATION_FILE= "CONFIGURATION_FILE",
							SERVICE_MAP_FILE = "SERVICE_MAP_FILE";
	
	private final String	DEFAULT_PROP_FILE = "/it/eng/rdlab/soa3/connector/service/configuration/configuration.properties",
							PROP_FILES = "/it/eng/rdlab/soa3/connector/service/configuration/configurationfiles.properties";
	
	private Configuration ()
	{
		logger = LogFactory.getLog(this.getClass());
		Properties files = getConfigurationFiles();
		String root = files.getProperty(CONFIGURATION_ROOT,"");
		String configurationFilePath = files.getProperty(CONFIGURATION_FILE,"");
		String serviceMapPath = files.getProperty(SERVICE_MAP_FILE,"");
		
		if (root != null && root.startsWith("$"))
		{
			root = System.getProperty(root.substring(1));
			logger.debug("Actual root "+root);
		}

		this.serviceUrlMap = defineServiceUrlMap(root,serviceMapPath);
		this.genericProperties = getPropertiesFromFile(root,configurationFilePath);
		this.defaultProperties = getDefaultProperties();
		
	}
	

	public static Configuration getInstance ()
	{
		if (instance == null) instance = new Configuration();
		
		return instance;
	}
	
	/**
	 * 
	 * Session validity
	 * 
	 * @return
	 */
	public long getAuthValidity ()
	{
		return getLongProperty(ConfigurationLabels.AUTH_SESSION, DEFAULT_AUTH_VALIDITY)*60000;
	}

	/**
	 * 
	 * Soa3 endopoint
	 * 
	 * @return
	 */
	public String getSoa3Endpoint ()
	{
		return getStringProperty(ConfigurationLabels.SOA3_ENDPOINT, DEFAULT_SOA3_URL);
	}
	
	/**
	 * 
	 * Service name
	 * 
	 * @return
	 */
	public String getServiceName ()
	{
		return getStringProperty(ConfigurationLabels.SERVICE_NAME,DEFAULT_SERVICE_NAME);
	}
	
	
	public String getCertFile ()
	{
		return this.genericProperties.getProperty(ConfigurationLabels.CERT_FILE, SecurityManager.DEFAULT_CERT_FILE);
		
	}
	
	public String getKeyFile ()
	{
		return this.genericProperties.getProperty(ConfigurationLabels.KEY_FILE, SecurityManager.DEFAULT_KEY_FILE);
		
	}
	
	public String getTrustDir ()
	{
		return this.genericProperties.getProperty(ConfigurationLabels.TRUST_DIR, SecurityManager.DEFAULT_TRUST_DIR);
		
	}
	
	public String getTrustExt ()
	{
		return this.genericProperties.getProperty(ConfigurationLabels.TRUST_FILE_EXTENSION, SecurityManager.DEFAULT_TRUST_FILE_EXTENSION);
		
	}
	
	public String getService (String serviceName)
	{
		return serviceUrlMap.getProperty(serviceName);
	}
	
	public boolean isAuthorizationEnabled ()
	{
		return getBooleanProperty (ConfigurationLabels.AUTHZ_ENABLED,DEFAULT_AUTHZ_ENABLED);
	}
	
	public String getDefaultOrganization ()
	{
		return getStringProperty(ConfigurationLabels.DEFAULT_ORGANIZATION, DEFAULT_ORGANIZATION_NAME);
	}
	
	public String getGCubeScope ()
	{
		return getStringProperty(ConfigurationLabels.GCUBE_SCOPE, DEFAULT_SCOPE);
	}
	
	/* **********************************************************************************************************************
	 * 
	 * Private Methods
	 * 
	 * ***********************************************************************************************************************/
	 
	
	/**
	 * 
	 * @param root
	 * @param configurationFilePath
	 * @return
	 */
		private Properties getPropertiesFromFile (String root,String configurationFilePath)
		{
			String path = root+configurationFilePath;
			logger.debug("Loading properties from "+path);
			
			Properties properties = new Properties();
			
			try 
			{
				properties.load(new FileInputStream(path));
				logger.debug("Operation completed");
			} 
			catch (Exception e) 
			{
				this.logger.warn("Configuration file not found",e);
			} 
			
			return properties;
		}
		
		/**
		 * 
		 * @return
		 */
		private Properties getDefaultProperties ()
		{
			logger.debug("Loading default properties file");
			
			Properties properties = new Properties();
			
			try 
			{
				properties.load(this.getClass().getResourceAsStream(DEFAULT_PROP_FILE));

			} catch (IOException e) 
			{
				this.logger.error("Unable to find properties from the classpath",e);
			}
			
			return properties;
		}
		
		/**
		 * 
		 * @return
		 */
		private Properties getConfigurationFiles ()
		{
			Properties fileProperties = new Properties();
			try 
			{
				fileProperties.load(this.getClass().getResourceAsStream(PROP_FILES));
			} catch (IOException e1) 
			{
				this.logger.error("Unable to find properties from the classpath",e1);
				
			}
			
			return fileProperties;
		}
		
		/**
		 * 
		 * @param root
		 * @param path
		 * @return
		 */
		private Properties defineServiceUrlMap (String root, String path)
		{
			this.logger.debug("Loading service urls");
			String serviceMapFile = root+path;
			
			Properties response = new Properties ();
			
			try
			{
				response.load(new FileInputStream(serviceMapFile));
				this.logger.debug("Service map loaded");
			}
			catch (Exception e)
			{
				this.logger.debug("No service map found");
			}
			
			this.logger.debug("Operation completed");
			
			return response;
		}
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private long getLongProperty (String key, long defaultValue)
		{
			long result = -1;
			
			try {
			
				String stringValue = this.genericProperties.getProperty(key,this.defaultProperties.getProperty(key));
				
				if (stringValue == null) result = defaultValue;
				else result = Long.parseLong(stringValue);
			
			} catch (Exception e)
			{
				logger.error("Invalid value, using default");
				result = defaultValue;
			}
			
			return result;

		}
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private String getStringProperty (String key, String defaultValue)
		{
			
			return this.genericProperties.getProperty(key, this.defaultProperties.getProperty(key, defaultValue));
		}
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private boolean getBooleanProperty (String key, boolean defaultValue)
		{
			boolean result = false;
			
			try {
			
				String stringValue = this.genericProperties.getProperty(key,this.defaultProperties.getProperty(key));
				
				if (stringValue == null) result = defaultValue;
				else result = Boolean.parseBoolean(stringValue);
			
			} catch (Exception e)
			{
				logger.error("Invalid value, using default");
				result = defaultValue;
			}
			
			return result;
		}
		
}
