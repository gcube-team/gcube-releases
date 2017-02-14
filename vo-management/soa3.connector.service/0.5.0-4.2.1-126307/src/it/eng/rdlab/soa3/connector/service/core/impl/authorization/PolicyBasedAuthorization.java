package it.eng.rdlab.soa3.connector.service.core.impl.authorization;

import it.eng.rdlab.soa3.connector.beans.PolicyRequestBean;
import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;
import it.eng.rdlab.soa3.connector.service.core.AuthorizationInternalService;
import it.eng.rdlab.soa3.connector.service.core.TicketControlManager;
import it.eng.rdlab.soa3.connector.service.utils.Utils;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gcube.soa3.connector.Authorize;
import org.gcube.soa3.connector.impl.PolicyManagerBasedAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyBasedAuthorization implements AuthorizationInternalService {

	private String soa3Endpoint;
	private Logger logger;
	
	public PolicyBasedAuthorization(String soa3Endpoint) 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.soa3Endpoint = soa3Endpoint;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean authorize(String ticket, String action, String resource) 
	{
		logger.debug("Checking the local cache for permitted or denied operations...");
		logger.debug("Action = "+action);
		logger.debug("Resource = "+resource);
		boolean response = false;
		AccessControlBean accessBean = TicketControlManager.getInstance().getAccessGrantEntry(ticket);

		try
		{
			
			response = performAuthorization(accessBean, action, resource);
			
		} catch (RuntimeException e)
		{
			logger.error("Unable to find the entry",e);
			response = false;
		}

		logger.debug("Response = "+response);
		return response;


	}
	
	
	/**
	 * 
	 * @param accessBean
	 * @param action
	 * @param resource
	 * @return
	 */
	private boolean performAuthorization (AccessControlBean accessBean, String action, String resource)
	{
		logger.debug("Performing authorization...");
		boolean response = false;
		
		if (accessBean.isPermitted(action, resource)) 
		{
			logger.debug("Operation permitted");
			response = true;
		}
		else if (accessBean.isDenied(action, resource))
		{
			logger.debug("Operation denied");
			response = false;
		}
		else
		{
			logger.debug("Operation not present: asking soa3");
			List<String> roles = accessBean.getRoles();
			Map<String, List<String>> attributes = new HashMap<String, List<String>> ();
			attributes.put(PolicyDecisionEngine.ROLE_DEFAULT_ATTRIBUTE, roles);
			Authorize policyManager = new PolicyManagerBasedAuthorization(this.soa3Endpoint);
			response = policyManager.authorize(attributes, action, resource);
			logger.debug("Response = "+response);
			
			if (response)
			{
				logger.debug("Adding the action and operation to the cache in the permitted section");
				accessBean.addPermittedOperation(action, resource);
			}
			else
			{
				logger.debug("Adding the action and operation to the cache in the denied section");
				accessBean.addDeniedOperation(action, resource);
			}
			return response;
		}
		
		logger.debug("Response = "+resource);
		return response;
	}


	


}
