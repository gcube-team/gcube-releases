package org.gcube.soa3.connector.impl;

/**
 * 
 * Authentication service client for federated users
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class FederatedAuthentication extends HeaderBasedAuthentication 
{
	private static final String METHOD = "FED",
								ENDPOINT_BASE_PATH = "authService";
	
	public FederatedAuthentication(String soa3Endpoint) 
	{
		super (METHOD,soa3Endpoint+ENDPOINT_BASE_PATH);
	}
}
