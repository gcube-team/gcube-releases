package org.gcube.security.soa3.connector;

import java.util.Map;


/**
 * 
 * This interface will replace {@link GCUBESecurityController}, {@link GCUBEServiceAuthenticationController} and {@link GCUBEServiceAuthorizationController}
 * to manage the authentication and authorization control. Currently in the security library there is an implementation of {@link GCUBESecurityController} 
 * that will supporth both the paradigms for a certain period: in the next release the old security library will be progressively abandoned 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface GCUBESecurityController 
{
	final String 	SECURITY_ENABLED = "SECURITY_ENABLED",
					SERVER_URL	= "SERVER_URL",
					CREDENTIAL_PROPAGATION_POLICY = "CREDENTIAL_PROPAGATION_POLICY";
	
	public static final String 	SERVICE_NAME = "SERVICE_NAME",
								PEER_SUBJECT = "PEER_SUBJECT",
								SERVICE_STRING = "SERVICE_STRING",
								SERVICE_INSTANCE = "SERVICE_INSTANCE";
	
	/**
	 * 
	 * Inits the controller
	 * 
	 * @param serviceContext
	 */
	public void init (Map<String, String> parameters);
	
	
	/**
	 * Checks the access rights (authentication and authorization) 
	 * @param parameters all the needed parameters
	 * @return true if the access is granted, false otherwise
	 */
	public boolean checkAccess (Map<String, Object> parameters);
	
	/**
	 * 
	 * @return 
	 */
	public boolean isSecurityEnabled ();
	

	/**
	 * 
	 * @param securityEnabled
	 */
	public void setSecurityEnabled (boolean securityEnabled);

}
