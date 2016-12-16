package it.eng.rdlab.soa3.connector.service.core;

import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.configuration.SecurityConfiguration;
import it.eng.rdlab.soa3.connector.service.core.impl.authentication.DNAuthenticationService;
import it.eng.rdlab.soa3.connector.service.core.impl.authentication.FederatedAuthenticationService;
import it.eng.rdlab.soa3.connector.service.core.impl.authentication.SessionAuthenticationService;
import it.eng.rdlab.soa3.connector.service.core.impl.authentication.UsernamePasswordAuthenticationService;

import java.util.Map;

/**
 * 
 * Generator of the authentication service, basing on the information in the header
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class AuthenticationServiceFactory 
{
	public static String REQUEST_HEADER = "REQUEST_HEADER";
							
	
	public static final String 		BASIC = "BASIC",
									FED = "FED",
									SES = "SES",
									DN = "DN";

	/**
	 * 
	 * Generates the authentication service basing on the input parameters
	 * 
	 * @param parameters the input parameters
	 * @return the Authentication Service
	 */
	public static AuthenticationInternalService generateService (Map<String, String> parameters)
	{
		SecurityConfiguration.initSecurity();
		String requestHeader = parameters.get(REQUEST_HEADER);
		AuthenticationInternalService response = null;
		
		if (requestHeader != null && requestHeader.equalsIgnoreCase(BASIC)) response = new UsernamePasswordAuthenticationService (Configuration.getInstance().getSoa3Endpoint(),Configuration.getInstance().getDefaultOrganization());
		else if (requestHeader != null && requestHeader.equalsIgnoreCase(FED)) response = new FederatedAuthenticationService(Configuration.getInstance().getSoa3Endpoint());
		else if (requestHeader != null && requestHeader.equalsIgnoreCase(SES)) response = new SessionAuthenticationService ();
		else if (requestHeader != null && requestHeader.equalsIgnoreCase(DN)) response = new DNAuthenticationService(Configuration.getInstance().getSoa3Endpoint(),Configuration.getInstance().getDefaultOrganization(), Configuration.getInstance().getGCubeScope());
		
		return response;
		
	}

}
