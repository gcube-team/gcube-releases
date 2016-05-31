package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.UserBean;
import it.eng.rdlab.soa3.connector.beans.UserBeanResponse;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.Authenticate;
import org.gcube.soa3.connector.rest.RestManager;

/**
 * 
 * Abstract class Authentication service based on the parameters inserted in an header of a REST message
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public abstract class HeaderBasedAuthentication implements Authenticate {

	private final String 	AUTHENTICATION_PATH = "authenticate",
							HEADER = "Authorization",
							ORGANIZATION_HEADER = "Organization";
	private String authenticationMethod;
	private String soa3Endpoint;
	private Log logger;
	
	public HeaderBasedAuthentication (String authenticationMethod, String soa3Endpoint)
	{
		this.logger = LogFactory.getLog(this.getClass());
		this.authenticationMethod = authenticationMethod;
		this.soa3Endpoint = soa3Endpoint;
	}
	
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public UserBean authenticate(String parameter, String organization) 
	{
		this.logger.debug("Loading REST manager for "+soa3Endpoint);
		RestManager manager = RestManager.getInstance(this.soa3Endpoint);
		this.logger.debug("REST manager loaded");
		Map<String, String> headers = new HashMap<String, String> ();
		headers.put(HEADER, authenticationMethod+" "+parameter);
		
		if (organization != null) headers.put(ORGANIZATION_HEADER, organization);
		
		UserBeanResponse response = manager.sendMessage(AUTHENTICATION_PATH, headers, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, UserBeanResponse.class);
		int responseCode = response.getResponseCode();
		logger.debug("Response code "+responseCode);
		UserBean responseBean = null;
		if (responseCode<300 && responseCode>=200 && response.getResponse() != null)
		{
			responseBean = response.getResponse();
			logger.debug("Authentication OK ");
		}
		else logger.debug("Authentication failed ");
		
		return responseBean;
	}


	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setSoa3Endpoint(String soa3Endpoint) 
	{
		this.soa3Endpoint = soa3Endpoint;
		
	}

}
