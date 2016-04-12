package it.eng.rdlab.soa3.connector.service.core;

import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.configuration.SecurityConfiguration;
import it.eng.rdlab.soa3.connector.service.core.impl.authorization.PolicyBasedAuthorization;

/**
 * 
 * Generator of the authentication service, basing on the information in the header
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class AuthorizationServiceFactory 
{


	/**
	 * 
	 * Generates the authentication service basing on the input parameters
	 * 
	 * @param parameters the input parameters
	 * @return the Authentication Service
	 */
	public static AuthorizationInternalService generateService ()
	{
		SecurityConfiguration.initSecurity();		
		return new PolicyBasedAuthorization(Configuration.getInstance().getSoa3Endpoint());
		
	}

}
