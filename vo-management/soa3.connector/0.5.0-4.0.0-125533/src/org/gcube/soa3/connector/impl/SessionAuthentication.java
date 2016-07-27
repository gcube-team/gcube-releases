package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.SessionBean;
import it.eng.rdlab.soa3.connector.beans.SessionBeanResponse;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.rest.RestManager;

/**
 * 
 * Authentication service client for session identified users
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SessionAuthentication 
{
	private static final String METHOD = "SES",
								ENDPOINT_BASE_PATH = "soa3Service/access/session",
								HEADER = "Authorization";
	private Log 			logger;
	private String 			soa3Endpoint;
	
	public SessionAuthentication(String soa3Endpoint) 
	{
		this.logger = LogFactory.getLog(this.getClass());
		this.soa3Endpoint = soa3Endpoint;

	}
	

	/**
	 * 
	 * Gets the session end for the ticket (if exists)
	 * 
	 * @param ticket
	 * @return the time when the session expires if the ticket exists, null otherwise
	 */
	public SessionBean getRemoteBean(String ticket) 
	{
		this.logger.debug("Loading REST manager for "+soa3Endpoint);
		RestManager manager = RestManager.getInstance(this.soa3Endpoint);
		this.logger.debug("REST manager loaded");
		Map<String, String> headers = new HashMap<String, String> ();
		headers.put(HEADER, METHOD+" "+ticket);
		SessionBeanResponse sessionResponse = manager.sendMessage(ENDPOINT_BASE_PATH, headers, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, SessionBeanResponse.class);
		int responseCode = sessionResponse.getResponseCode();
		logger.debug("Response code "+responseCode);
		SessionBean response = null;
		if (responseCode<300 && responseCode>=200 && sessionResponse.getResponse() != null)
		{
			response = sessionResponse.getResponse();
			logger.debug("Session end = "+response);
		}
		else logger.debug("No session");
		
		return response;
	}




	/**
	 * 
	 * @param soa3Endpoint
	 */
	public void setSoa3Endpoint(String soa3Endpoint) 
	{
		this.soa3Endpoint = soa3Endpoint;
		
	}
}
