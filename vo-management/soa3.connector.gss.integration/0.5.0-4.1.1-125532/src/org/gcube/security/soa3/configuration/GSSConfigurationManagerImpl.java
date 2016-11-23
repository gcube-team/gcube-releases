package org.gcube.security.soa3.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.security.soa3.connector.GCUBESecurityController;
import org.globus.wsrf.config.ContainerConfig;

/**
 * 
 * Singleton instance managing the configuration of the module
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class GSSConfigurationManagerImpl implements GSSConfigurationManager
{
	
	
	private final String	SOA3_DEFAULT_URL = "https://localhost",
							CREDENTIAL_PROPAGATION_DEFAULT_POLICY = "true";
	
	/** Default Security Configuration option name. */
	private final String DEFAULT_SECURITY_CONFIGURATION = "defaultSecurityConfiguration";
	private GCUBELog log;
	private Properties properties;
	
	private HashMap<String, Properties> serviceSecurityConfiguration;

	


	GSSConfigurationManagerImpl (boolean defaultConfiguration)
	{
		this.serviceSecurityConfiguration = new HashMap<String, Properties>();
		this.log = new GCUBELog(this);
		this.properties = new Properties();
		
		if (defaultConfiguration) loadDefaultProperties();
		
	}
	
	
	private void loadDefaultProperties ()
	{
		String pathToDefaultSecurityConfiguration = ContainerConfig.getConfig().getOption(DEFAULT_SECURITY_CONFIGURATION);
		log.debug("Security configuration path "+pathToDefaultSecurityConfiguration);
		pathToDefaultSecurityConfiguration = GHNContext.getContext().getLocation()+File.separatorChar+pathToDefaultSecurityConfiguration;
		log.debug("Absolute path = "+pathToDefaultSecurityConfiguration);
		try 
		{
			this.properties.load(new FileInputStream(pathToDefaultSecurityConfiguration));
		} catch (Exception e) 
		{
			this.log.warn("Unable to load default properties",e);
			
		} 
	}
	
	/**
	 * 
	 * 
	 * @param serviceName 
	 * @return true if the security is enabled
	 */
	public boolean isSecurityEnabled (String serviceName)
	{
		String stringResponse = getProperty(serviceName, GCUBESecurityController.SECURITY_ENABLED,null);
		log.debug("String response "+stringResponse);
		boolean finalResponse = stringResponse == null ? GHNContext.getContext().isSecurityEnabled() : getBooleanValue(stringResponse);
		log.debug("Security enabled "+finalResponse);
		return finalResponse;
		
	}
	
	/**
	 * 
	 * 
	 * @param serviceName
	 * @return the server url for that service
	 */
	public String getServerUrl (String serviceName)
	{
		String response = getProperty(serviceName, GCUBESecurityController.SERVER_URL, SOA3_DEFAULT_URL);
		log.debug("SOA3 Url = "+response);
		return response;
	}
	
	/**
	 * 
	 * @param serviceName
	 * @return true if the credentials must be propagated for the considered service, false otherwise
	 */
	public boolean getCredentialPropagationPolicy (String serviceName)
	{
		String stringResponse = getProperty(serviceName, GCUBESecurityController.CREDENTIAL_PROPAGATION_POLICY, CREDENTIAL_PROPAGATION_DEFAULT_POLICY);
		log.debug("Credential propagation "+stringResponse);
		return getBooleanValue(stringResponse);
	}

	private String getProperty (String serviceName, String propertyName, String defaultValue)
	{
		String stringResponse = null;
		log.debug("Service Name = "+serviceName);
		
		if (serviceName != null)
		{
			Properties serviceProperties = this.serviceSecurityConfiguration.get(serviceName);
			log.debug("Service Properties = "+serviceProperties);
			
			if (serviceProperties != null) stringResponse = serviceProperties.getProperty(propertyName, properties.getProperty(propertyName, defaultValue));
			else stringResponse = properties.getProperty(propertyName, defaultValue);
		}
		else stringResponse = properties.getProperty(propertyName, defaultValue);
		
		log.debug("String response = "+ stringResponse);
		
		return stringResponse;
	}


	private boolean getBooleanValue (String stringValue)
	{
		return (stringValue != null && stringValue.equalsIgnoreCase("true")); 
	}

	
	/**
	 * 
	 * 
	 * @param serviceName
	 * @param serviceProperties sets the configuration properties for the service
	 */
	@Override
	public void setServiceProperties (String serviceName, Properties serviceProperties)
	{
		this.serviceSecurityConfiguration.put(serviceName, serviceProperties);
	}
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	public boolean servicePropertiesSet (String serviceName)
	{
		return this.serviceSecurityConfiguration.containsKey(serviceName);
	}
}
