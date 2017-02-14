package it.eng.rdlab.soa3.pm.connector.service.jaxrs;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.ResponseBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyEngine;
import it.eng.rdlab.soa3.pm.connector.service.beans.RuleJaxBean;
import it.eng.rdlab.soa3.pm.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.pm.connector.service.factory.PolicyEngineFactory;
import it.eng.rdlab.soa3.pm.connector.service.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.util.Base64;

@Path("/policymanager")
public class PolicyManagerService 
{
	private Logger logger;
	private PolicyEngine engine;
	
	public PolicyManagerService() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		Configuration.init();
		this.engine = PolicyEngineFactory.getPolicyEngine();
		this.logger.debug("Policy engine generated");
		
	}
	
	/**
	 * 
	 * Policy creator
	 * 
	 * @param ruleBean
	 * @param context
	 * @return
	 */
	@POST
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createRule (RuleJaxBean ruleJaxBean,@Context HttpContext context)
	{
		this.logger.debug("Calling the policy creation engine...");
		this.logger.debug("Attributes = "+ruleJaxBean.getAttributes());
		ResponseBean response = this.engine.createRule(Utils.fromRuleJaxBean(ruleJaxBean));
		this.logger.debug("Response received "+response);
		
		switch (response.getStatus())
		{
		case ResponseBean.RULE_CREATE_UPDATE_NOT_CREATED_OR_UPDATED:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.BAD_REQUEST).entity("Rule not created ").build();	
		case ResponseBean.RULE_CREATE_UPDATE_WARNING_ON_EXTRA_PARAMETERS:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(response.getInfo()).build();	
		default:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.CREATED).entity(response.getInfo()).build();

		}
	}
	
	/**
	 * 
	 * @param ruleBean
	 * @param context
	 * @return
	 */
	@POST
	@Path("/ruleid")
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRuleId (RuleJaxBean ruleJaxBean, @Context HttpContext context)
	{
		logger.debug("Calling get ruleid method");
		String ruleID = this.engine.getRuleId(Utils.fromRuleJaxBean(ruleJaxBean));
		this.logger.debug("Response "+ruleID);
		
		if (ruleID != null) return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(ruleID).build();
		else return Response.status(com.sun.jersey.api.client.ClientResponse.Status.NOT_FOUND).entity("Policy not found").build();
	}
	
	/**
	 * 
	 * Policy reader
	 * 
	 * @param ruleId
	 * @param context
	 * @return
	 */
	@GET
	@Path("/{ruleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRule (@PathParam("ruleId") String ruleId, @Context HttpContext context)
	{
		this.logger.debug("Calling the policy reading engine...");
		this.logger.debug("Rule ID "+ruleId);
		RuleBean ruleBean = this.engine.getRule(ruleId);
		this.logger.debug("Response received");
		
		if (ruleBean != null)
		{
			String value = null;
			
			try 
			{
				value = Utils.getMapper().writeValueAsString(Utils.fromRuleBean(ruleBean));
				logger.debug("Policy response "+value);
				
				return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(value).build();
			
			} catch (JsonGenerationException e) {

				logger.error("get user unsuccessful due to json parse error  ");
				value = "json parser error";
				
			} catch (JsonMappingException e) {

				logger.error("get user unsuccessful due to json parse error  ");
				value = "json mapping error";
			} 
			catch (IOException e) 
			{

				logger.error("get user unsuccessful due to json parse error  ");
				value = "json parser error";
			}

			throw new WebApplicationException(Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity(value).build());
			
		}
		else
		{
			logger.debug("Policy not found");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.NOT_FOUND).entity("Policy not found").build();
		}
		
	}

	/**
	 * 
	 * 
	 * @param ruleId
	 * @param policyBean
	 * @param context
	 * @return
	 */
	@PUT
	@Path("/{ruleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRule (@PathParam("ruleId") String ruleId, RuleJaxBean ruleJaxBean,@Context HttpContext context )
	{
		this.logger.debug("Calling the policy updating engine...");
		this.logger.debug("Rule ID "+ruleId);
		ResponseBean response = this.engine.updateRule(ruleId, Utils.fromRuleJaxBean(ruleJaxBean));
		this.logger.debug("Response received "+response.getStatus());
		
		switch (response.getStatus())
		{
		case ResponseBean.RULE_UPDATE_RULE_NOT_FOUND:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.NOT_FOUND).entity("Policy not updated: the policy is not present ").build();	
		case ResponseBean.RULE_CREATE_UPDATE_NOT_CREATED_OR_UPDATED:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.NOT_MODIFIED).entity("Policy not updated: unable to modify the policy").build();
		case ResponseBean.RULE_CREATE_UPDATE_WARNING_ON_EXTRA_PARAMETERS:
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.CONFLICT).entity(response.getInfo()).build();	

		default:
		
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(response.getInfo()).build();	
		}
		
	}
	
	/**
	 * 
	 * @param ruleId
	 * @param context
	 * @return
	 */
	@DELETE
	@Path("/{ruleId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteRule (@PathParam("ruleId") String ruleId, @Context HttpContext context)
	{
		this.logger.debug("Calling the policy deletion engine...");
		boolean response = this.engine.deleteRule(ruleId);
		this.logger.debug("Response received "+response);
		
		if (response)
		{
			logger.debug("Policy deleted");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity("Policy has been deleted successfully ").build();	
		}
		else
		{
			logger.debug("Policy not deleted");
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.BAD_REQUEST).entity("Policy not deleted ").build();	

		}
	}
	
	/**
	 * 
	 * @param subjectid
	 * @param subjectvalue
	 * @param context
	 * @return
	 */
	@GET
	@Path("/subject/{subjectid}/{subjectvalue}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRulesBySubjects  (@PathParam("subjectid") String subjectid,@PathParam("subjectvalue")String subjectvalue, @Context HttpContext context)
	{
		this.logger.debug("Calling the rule retrieving engine...");
		this.logger.debug("Subject id "+subjectid);
		this.logger.debug("Subject value "+subjectvalue);
		List<Attribute> attributesList = new ArrayList<Attribute> ();
		attributesList.add(new Attribute(subjectid,subjectvalue));
		return internalListRulesBySubject (attributesList);	
		
	}
	
	/**
	 * 
	 * @param subjects
	 * @param context
	 * @return
	 */
	@GET
	@Path("/subjects/{subjects}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRulesBySubjects  (@PathParam("subjects") String subjects, @Context HttpContext context)
	{
		this.logger.debug("Calling the rule retrieving engine...");
		this.logger.debug("Subject attributes "+subjects);
		List<Attribute> attributesList = getAttributes(subjects);
		return internalListRulesBySubject (attributesList);			
		
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	@GET
	@Path("/rules")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRules (@Context HttpContext context)
	{
		this.logger.debug("Calling the rule retrieving engine...");
		List<RuleBean> ruleBeans = this.engine.listRules();
		this.logger.debug("Policy beans retrieved");
		String value = null;
		

		
		try 
		{
			value = Utils.getMapper().writeValueAsString(Utils.fromRuleBeanList(ruleBeans));
			logger.debug("Policy response "+value);
			
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(value).build();
		
		} catch (JsonGenerationException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
			
		} catch (JsonMappingException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json mapping error";
		} 
		catch (IOException e) 
		{

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
		}

		throw new WebApplicationException(Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity(value).build());

	}
	
	/**
	 * 
	 * @param attributesList
	 * @return
	 */
	private Response internalListRulesBySubject (List<Attribute> attributesList)
	{
		List<RuleBean> ruleBeans = this.engine.listRulesBySubjects(attributesList);
		this.logger.debug("Policy beans retrieved");
		String value = null;
		
		try 
		{
			value = Utils.getMapper().writeValueAsString(Utils.fromRuleBeanList(ruleBeans));
			logger.debug("Policy response "+value);
			
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(value).build();
		
		} catch (JsonGenerationException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
			
		} catch (JsonMappingException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json mapping error";
		} 
		catch (IOException e) 
		{

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
		}

		throw new WebApplicationException(Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity(value).build());

	}
	
	/**
	 * 
	 * @param subjects
	 * @return
	 */
	private List<Attribute> getAttributes (String subjects)
	{
		logger.debug("Generating subjects map");
		List<Attribute> response = new ArrayList<Attribute>();
		
		if (subjects != null)
		{
			String decodedSubjects = new String(Base64.decode(subjects));
			logger.debug("Decoded subjects = "+decodedSubjects);
			String [] keyValues = decodedSubjects.split(":");
			
			for (String keyvalue : keyValues)
			{
				logger.debug("KeyValue "+keyvalue);
				String [] kv = keyvalue.split("=");
				
				try
				{
					response.add(new Attribute(kv[0].trim(), kv[1].trim()));
				}
				catch (RuntimeException e)
				{
					logger.error("Unable to add a value in the attribute table",e);
				}
				
			}
			
		}
		else logger.debug("No attributes found");
		
		return response;
		
	}
	
	/**
	 * 
	 * @param action
	 * @param context
	 * @return
	 */
	@GET
	@Path("/action/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRulesByAction  (@PathParam("action") String action, @Context HttpContext context)
	{
		this.logger.debug("Calling the policy retrieving engine...");
		this.logger.debug("Action "+action);
		List<RuleBean> ruleBeans = this.engine.listRulesByAction(action);
		this.logger.debug("Policy beans retrieved");
		String value = null;
		
		try 
		{
			value = Utils.getMapper().writeValueAsString(Utils.fromRuleBeanList(ruleBeans));
			logger.debug("Policy response "+value);
			
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(value).build();
		
		} catch (JsonGenerationException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
			
		} catch (JsonMappingException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json mapping error";
		} 
		catch (IOException e) 
		{

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
		}

		throw new WebApplicationException(Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity(value).build());

	}
	
	/**
	 * 
	 * @param respurce
	 * @param context
	 * @return
	 */
	@GET
	@Path("/resource/{resource}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRulesByResource  (@PathParam("resource") String respurce, @Context HttpContext context)
	{
		this.logger.debug("Calling the policy retrieving engine...");
		this.logger.debug("Resource "+respurce);
		List<RuleBean> ruleBeans = this.engine.listRulesByResource(respurce);
		this.logger.debug("Policy beans retrieved");
		String value = null;
		
		try 
		{
			value = Utils.getMapper().writeValueAsString(Utils.fromRuleBeanList(ruleBeans));
			logger.debug("Rule response "+value);
			
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.OK).entity(value).build();
		
		} catch (JsonGenerationException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
			
		} catch (JsonMappingException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json mapping error";
		} 
		catch (IOException e) 
		{

			logger.error("get user unsuccessful due to json parse error  ");
			value = "json parser error";
		}

		throw new WebApplicationException(Response.status(com.sun.jersey.api.client.ClientResponse.Status.INTERNAL_SERVER_ERROR).entity(value).build());

	}
	
	public static void main(String[] args) throws Exception{
		RuleBean bean = new RuleBean();
		bean.setAction("ciao");
		bean.setResource(".*");
		bean.getAttributes().put("role", "ciao");
		System.out.println(Utils.getMapper().writeValueAsString(bean));
	}
	
	
}
