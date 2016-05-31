package org.gcube.soa3.connector.impl;

/**
 * 
 * Authentication service client for username/password identified users
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class UsernamePasswordAuthentication extends HeaderBasedAuthentication 
{
	private static final String METHOD = "Basic",
								ENDPOINT_BASE_PATH = "authService";
	
	public UsernamePasswordAuthentication(String soa3Endpoint) 
	{
		super (METHOD,soa3Endpoint+"/"+ENDPOINT_BASE_PATH);
	}
}
