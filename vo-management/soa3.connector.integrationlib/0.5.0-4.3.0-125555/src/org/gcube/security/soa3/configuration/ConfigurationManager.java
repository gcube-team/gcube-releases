package org.gcube.security.soa3.configuration;


/**
 * 
 * Singleton instance managing the configuration of the module
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface ConfigurationManager 
{
	
	/**
	 * 
	 * 
	 * @param serviceName 
	 * @return true if the security is enabled
	 */
	public boolean isSecurityEnabled (String serviceName);
	
	/**
	 * 
	 * 
	 * @param serviceName
	 * @return the server url for that service
	 */
	public String getServerUrl (String serviceName);
	
	/**
	 * 
	 * @param serviceName
	 * @return true if the credentials must be propagated for the considered service, false otherwise
	 */
	public boolean getCredentialPropagationPolicy (String serviceName);

//
//	/**
//	 * 
//	 * 
//	 * @param serviceName
//	 * @param serviceProperties sets the configuration properties for the service
//	 */
//	public void setServiceProperties (String serviceName, Properties serviceProperties);
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	public boolean servicePropertiesSet (String serviceName);
}
