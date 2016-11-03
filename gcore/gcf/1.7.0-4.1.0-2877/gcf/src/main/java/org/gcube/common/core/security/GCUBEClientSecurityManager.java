package org.gcube.common.core.security;

import java.util.Map;

/**
 * 
 * An extension of {@link GCUBESecurityManager} that provides methods for standalone clients
 * to manages security configuration of outgoing messages
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface GCUBEClientSecurityManager extends GCUBESecurityManager
{
	public static final String IDENTITY = "IDENTITY";
	
	/**
	 * 
	 * Sets a map of identity parameters
	 * 
	 * @param parameters the identity parameters
	 * @throws Exception if something doesn't work in the configuration
	 */
	public void setIdentityParameters (Map<String, String> parameters) throws Exception;
	
	/**
	 * 
	 * Sets a single default identity parameter
	 * 
	 * @param identity the identity
	 * @throws Exception if something doesn't work in the configuration
	 */
	public void setDefaultIdentityParameter (String identity) throws Exception;
	
	/**
	 * 
	 * forces the security to run in spite of the client configuration files
	 * 
	 */
	public void forceSecurityEnabled ();
	
	/**
	 * 
	 * forces the security to not run in spite of the client configuration files
	 * 
	 */
	public void forceSecurityDisabled ();
	
	/**
	 * 
	 * the security status is established by the configuration file of the GHN in client mode
	 * 
	 */
	public void disableSecurityStatusEnforcement(); 
	
	/**
	 * 
	 * Provides the default credentials of the security manager (they could be the credentials of the GHN in client mode, but this is not mandatory)
	 * 
	 * @return the default credentials
	 */
	public SecurityCredentials getClientBaseCredentials();
	
}
