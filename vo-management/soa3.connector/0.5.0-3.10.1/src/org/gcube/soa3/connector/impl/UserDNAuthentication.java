package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.StringResponse;
import it.eng.rdlab.soa3.connector.beans.UserBean;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.Authenticate;
import org.gcube.soa3.connector.rest.RestManager;

/**
 * 
 * Authentication service client for federated users
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class UserDNAuthentication  implements Authenticate 
{
	
	private final String 	USERMANAGER_PATH = "userService/usermanager/certificate";
	private String 			soa3Endpoint;
	private Log 			logger;
				
	public  UserDNAuthentication (String soa3Endpoint)
	{
		this.logger = LogFactory.getLog(this.getClass());
		this.soa3Endpoint = soa3Endpoint;
	}


	@Override
	public void setSoa3Endpoint(String soa3Endpoint) 
	{
		this.soa3Endpoint = soa3Endpoint;
	}

	@Override
	public UserBean authenticate(String parameter, String organization) 
	{
		this.logger.debug("Loading REST manager for "+soa3Endpoint);
		RestManager manager = RestManager.getInstance(this.soa3Endpoint);
		this.logger.debug("REST manager loaded");
		StringBuilder pathBuilder = new StringBuilder(USERMANAGER_PATH).append("/").append(parameter).append("/");
		
		if (organization != null && organization.trim().length()>0) pathBuilder.append(organization.trim());
		
		String path = pathBuilder.toString();
		
		StringResponse response = manager.sendMessage(path, new HashMap<String, String>(), null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, StringResponse.class);
		int responseCode = response.getResponseCode();
		logger.debug("Response code "+responseCode);
		UserBean responseBean = null;
		
		if (responseCode<300 && responseCode>=200 && response.getResponse() != null)
		{
			try
			{
				String user = response.getResponse();
				logger.debug("User found "+user);
				responseBean = new UserBean();
				responseBean.setUserName(user);
				logger.debug("Authentication OK ");
			} 
			catch (Exception e)
			{
				logger.debug("User not found",e);
			}
		}
		else logger.debug("Authentication failed ");
		
		return responseBean;
	}
	

}
