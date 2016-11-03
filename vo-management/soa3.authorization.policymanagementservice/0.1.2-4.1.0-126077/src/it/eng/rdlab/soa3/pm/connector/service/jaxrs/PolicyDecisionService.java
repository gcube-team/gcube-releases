package it.eng.rdlab.soa3.pm.connector.service.jaxrs;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.AuthZRequestBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;
import it.eng.rdlab.soa3.pm.connector.service.beans.PolicyRequestBean;
import it.eng.rdlab.soa3.pm.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.pm.connector.service.factory.PolicyDecisionEngineFactory;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.HttpContext;

@Path("/policyquery")
public class PolicyDecisionService 
{
	private Logger logger;
	private PolicyDecisionEngine engine;
	
	public PolicyDecisionService() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		Configuration.init();
		this.engine = PolicyDecisionEngineFactory.getPolicyDecisionEngine();
		
		if (this.engine == null) logger.error("Unable to create a policy decision engine");
		
	}

	/**
	 * 
	 * Simple query for a default role
	 * 
	 * @param role
	 * @param action
	 * @param resource
	 * @param context
	 * @return
	 */
	@GET
	@Path("/rolequery/{role}/{action}/{resource}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getDecision (@PathParam("role") String role, @PathParam("action")  String action,@PathParam("resource")  String resource, @Context HttpContext context)
	{
		logger.debug("Get decision");
	
		if (this.engine != null)
		{
			logger.debug("Asking Policy Decision Engine");
			AuthZRequestBean bean = new AuthZRequestBean();
			bean.getAttributes().add(new Attribute(PolicyDecisionEngine.ROLE_DEFAULT_ATTRIBUTE,role));
			bean.setAction(action);
			bean.setResource(resource);
			return executeQuery(bean);

		}
		else
		{
			logger.error("Internal server error");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity("Service not available").build();
		}
		
	}
	
	/**
	 * 
	 * POST based query with complex datasent
	 * 
	 * @param ruleBean
	 * @return
	 */
	@POST
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getDecision (PolicyRequestBean requestBean)
	{
		logger.debug("Get decision");
		
		if (this.engine != null)
		{
			logger.debug("Asking Policy Decision Engine");
			
			
			return executeQuery(getRequestBean(requestBean));

		}
		else
		{
			logger.error("Internal server error");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity("Service not available").build();
		}
	}
	
	
	
	private Response executeQuery (AuthZRequestBean authorizationBean)
	{
		if (this.engine.getDecision(authorizationBean))
		{
			logger.debug("Authorization grant!");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity("Authorized").build();
		}
		else
		{
			logger.debug("Not authorized");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.UNAUTHORIZED).entity("Not Authorized").build();
		}
	}
	
	private AuthZRequestBean getRequestBean (PolicyRequestBean policyRequestBean)
	{
		logger.debug("Converting policy request bean...");
		List<String> attributes = policyRequestBean.getAttributes();
		AuthZRequestBean response = new AuthZRequestBean();
		
		for (String attribute : attributes)
		{
			try
			{
				String [] idValue = attribute.split("=");
				logger.debug("Id = "+idValue[0]+ " value "+idValue[1]);
				response.getAttributes().add(new Attribute(idValue[0],idValue[1]));
			} catch (RuntimeException e)
			{
				logger.warn("Invalid attribute");
			}
		
		}
		
		response.setAction(policyRequestBean.getAction());
		response.setResource(policyRequestBean.getResource());
		return response;
	}
}
