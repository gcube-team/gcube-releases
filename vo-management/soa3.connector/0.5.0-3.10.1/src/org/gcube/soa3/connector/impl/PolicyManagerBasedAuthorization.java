package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.PolicyRequestBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.Authorize;
import org.gcube.soa3.connector.rest.RestManager;

/**
 * 
 * Abstract class Authentication service based on the parameters inserted in an header of a REST message
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyManagerBasedAuthorization implements Authorize {

	private String soa3Endpoint;
	private Log logger;
	private final String AUTHORIZATION_PATH = "policyService/policyquery";
	
	public PolicyManagerBasedAuthorization (String soa3Endpoint)
	{
		this.logger = LogFactory.getLog(this.getClass());
		this.soa3Endpoint = soa3Endpoint;
	}
	
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean authorize(Map<String, List<String>> attributes, String action, String resource) 
	{
		this.logger.debug("Loading REST manager for "+soa3Endpoint);
		RestManager manager = RestManager.getInstance(this.soa3Endpoint);
		this.logger.debug("REST manager loaded");
		PolicyRequestBean bean = new PolicyRequestBean();
		bean.setAttributes(generateAttributeList(attributes));
		bean.setAction(action);
		bean.setResource(resource);
		int responseCode = manager.sendPostMessage(AUTHORIZATION_PATH, new HashMap<String, String>(), bean, MediaType.APPLICATION_JSON_TYPE, MediaType.TEXT_PLAIN_TYPE);
		logger.debug("Response code "+responseCode);
		return responseCode >= 200 && responseCode <300;
		
	}


	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void setSoa3Endpoint(String soa3Endpoint) 
	{
		this.soa3Endpoint = soa3Endpoint;
		
	}
	
	/**
	 * 
	 * @param attributesMap
	 * @return
	 */
	private List<String> generateAttributeList (Map<String, List<String>> attributesMap)
	{
		logger.debug("Converting attributes map");
		Iterator<String> keys = attributesMap.keySet().iterator();
		List<String> response = new ArrayList<String>();
		
		while (keys.hasNext())
		{
			String key = keys.next();
			logger.debug("Key = "+key);
			List<String> values = attributesMap.get(key);
			
			for (String value : values)
			{
				logger.debug("Value = "+value);
				response.add(key+"="+value);
			}
			
		}
		
		logger.debug("Added "+response.size()+" attributes");
		return response;
	}
	
	


}
