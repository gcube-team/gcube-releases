package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.RolesBeanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.RolesLoader;
import org.gcube.soa3.connector.rest.RestManager;

public class RolesLoaderImpl implements RolesLoader {



	private final String 	ROLEMANAGER_PATH = "userService/rolemanager/users";
	private String 			soa3Endpoint;
	private Log 			logger;
				
	public  RolesLoaderImpl (String soa3Endpoint)
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
	public List<String> loadRoles(String user, String organization) 
	{
		this.logger.debug("Loading REST manager for "+soa3Endpoint);
		RestManager manager = RestManager.getInstance(this.soa3Endpoint);
		this.logger.debug("REST manager loaded");
		StringBuilder pathBuilder = new StringBuilder(ROLEMANAGER_PATH).append("/").append(user).append("/");
		
		if (organization != null && organization.trim().length()>0) pathBuilder.append(organization.trim());
		
		String path = pathBuilder.toString();
		
		RolesBeanResponse response = manager.sendMessage(path, new HashMap<String, String>(), null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, RolesBeanResponse.class);
		int responseCode = response.getResponseCode();
		logger.debug("Response code "+responseCode);
		List<String> responseList = null;
		
		if (responseCode<300 && responseCode>=200 && response.getResponse() != null)
		{
			try
			{
				responseList = response.getResponse().getRoles();
				
				if (responseList == null) responseList = new ArrayList<String>();
				
				logger.debug("Roles found "+responseList.size());

			} 
			catch (Exception e)
			{
				logger.debug("Problems in loading roles",e);
			}
		}
		else logger.debug("Problems in loading roles ");
		
		return responseList;

	}
	
}
