package it.eng.rdlab.soa3.connector.service.jaxrs;


import it.eng.rdlab.soa3.connector.beans.SessionBean;
import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;
import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.core.AuthenticationInternalService;
import it.eng.rdlab.soa3.connector.service.core.AuthenticationServiceFactory;
import it.eng.rdlab.soa3.connector.service.core.AuthorizationInternalService;
import it.eng.rdlab.soa3.connector.service.core.AuthorizationServiceFactory;
import it.eng.rdlab.soa3.connector.service.core.RolesLoaderFactory;
import it.eng.rdlab.soa3.connector.service.core.TicketControlManager;
import it.eng.rdlab.soa3.connector.service.core.impl.authentication.SessionAuthenticationService;
import it.eng.rdlab.soa3.connector.service.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gcube.soa3.connector.RolesLoader;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.core.HttpContext;

/**
 * REST service for the complete access control flow of SOA3 
 * @author Ciro Formisano
 * 
 */

@Path("/access")
public class AccessService 
{
	private Log logger;
	private final String 	AUTHORIZATION_HEADER = "Authorization",
							SERVICE_STRING_HEADER = "Servicestring",
							SERVICE_INSTANCE_HEADER = "Serviceinstance";
	
	public AccessService()
	{
		this.logger = LogFactory.getLog(this.getClass());
	}

	private String [] parseHeader (String authHeader)
	{
		String [] encodedHeader = authHeader.split(" ");
		
		if (encodedHeader.length <2) 
		{
			logger.error("invalid authorization header "+authHeader);
			throw new WebApplicationException (Response.status(Status.BAD_REQUEST).entity("Invalid authorization header "+authHeader).build());
		}
		else return encodedHeader;
		
	}
	
	
	/**
	 * 
	 * Access control method
	 * 
	 * @param context
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAccessControl(@Context HttpContext context) 
	{
		logger.debug("Checking access privileges");
		String authHeader = context.getRequest().getHeaderValue(AUTHORIZATION_HEADER);
		String serviceString = context.getRequest().getHeaderValue(SERVICE_STRING_HEADER);
		String serviceInstance = context.getRequest().getHeaderValue(SERVICE_INSTANCE_HEADER);
		logger.debug("Authentication header "+authHeader);
		logger.debug("Service string header "+serviceString);
		logger.debug("Service instance header "+serviceInstance);
		String ticket = performAuthentication(authHeader);
		boolean authorizationEnabled = Configuration.getInstance().isAuthorizationEnabled();
		logger.debug("Authorization enabled "+authorizationEnabled);
		
		if (authorizationEnabled)
		{
			boolean authorized = performAuthorization(ticket,serviceString,serviceInstance);
			logger.debug("Authorized "+authorized);
			if (authorized) return ticket;
			else throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Authorization failed for "+serviceString+ " "+serviceInstance).build());

		}
		else 
		{
			logger.debug("Authorization disabled");
			return ticket;
		}
		
	}

	
	/**
	 * 
	 * Access control method
	 * 
	 * @param context
	 * @return information on the duration of the session
	 */
	@GET
	@Path("/session")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSession(@Context HttpContext context) 
	{
		logger.debug("Checking access privileges");
		String authHeader = context.getRequest().getHeaderValue(AUTHORIZATION_HEADER);
		
		if (authHeader != null)
		{
			logger.debug("Parsing authN header");
			String [] encodedHeader = parseHeader(authHeader);
			logger.debug("Parsing authN header");
			logger.debug("Authentication type "+encodedHeader [0]);
			
			if (encodedHeader [0].equals(AuthenticationServiceFactory.SES))
			{
				logger.debug("Authentication data "+encodedHeader [1]);
				SessionAuthenticationService authenticationService = new SessionAuthenticationService (true);
				logger.debug("Calling the authentication service");
				SessionBean sessionBean = authenticationService.getSessionBean(encodedHeader [1]);
				
				if (sessionBean != null)
				{
					logger.debug("Authentication OK, session "+sessionBean);
					return generateResponse(sessionBean);
				}
				else
				{
					logger.error("authentication unsuccessful");
					throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Wrong credentials, check username and password ").build());
				}
			}
			else
			{
				logger.error("Invalid authentication header, only Session based authentication allowed");
				throw new WebApplicationException (Response.status(Status.BAD_REQUEST).entity("Invalid authentication header, only Session based authentication allowed").build());
			}
		}
		else
		{
			logger.error("Invalid authentication header");
			throw new WebApplicationException (Response.status(Status.BAD_REQUEST).entity("No authorization header found ").build());
			
		}
	}
	
	/**
	 * 
	 * @param authHeader
	 * @return
	 */
	private String performAuthentication (String authHeader)
	{
		logger.debug("Perform authentication");
		
		if (authHeader != null)
		{
			logger.debug("Parsing authN header");
			String [] encodedHeader = parseHeader(authHeader);
			logger.debug("Parsing authN header");
			logger.debug("Authentication type "+encodedHeader [0]);
			logger.debug("Authentication data "+encodedHeader [1]);
			Map<String, String> authenticationServiceRequestParameters = new HashMap<String, String> ();
			authenticationServiceRequestParameters.put(AuthenticationServiceFactory.REQUEST_HEADER, encodedHeader [0]);
			AuthenticationInternalService authenticationService = AuthenticationServiceFactory.generateService(authenticationServiceRequestParameters);
			
			if (authenticationService == null)
			{
				logger.error("invalid authorization header "+encodedHeader [0]);
				throw new WebApplicationException (Response.status(Status.BAD_REQUEST).entity("Invalid authorization header "+encodedHeader [0]).build());
	
			}
			else
			{
				logger.debug("Calling the authentication service");
				String ticket = authenticationService.authenticate(encodedHeader [1]);
				
				if (ticket != null)
				{
					logger.debug("Authentication OK, ticket "+ticket);
					return ticket;
				}
				else
				{
					logger.error("authentication unsuccessful");
					throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Wrong credentials").build());
				}
			}
		}
		else
		{
			logger.error("Invalid authentication header");
			throw new WebApplicationException (Response.status(Status.BAD_REQUEST).entity("No authorization header found ").build());
			
		}
	}
	
	/**
	 * 
	 * @param ticket
	 * @return
	 */
	private boolean performAuthorization (String ticket,String action, String resource)
	{
		logger.debug("Perform authorization");
		boolean response = false;
		AccessControlBean bean = TicketControlManager.getInstance().getAccessGrantEntry(ticket);
		try
		{
			if (!bean.isRolesLoaded())
			{
				logger.debug("Loading roles");
				RolesLoader rolesLoader = RolesLoaderFactory.getRoleLoader();
				List<String> roles = rolesLoader.loadRoles(bean.getUsername(),Configuration.getInstance().getDefaultOrganization());
				bean.setRolesLoaded(true);
				
				if (roles != null && roles.size()>0) 
				{
					logger.debug("Importing roles");
					bean.getRoles().addAll(roles);
					logger.debug("Roles imported");
				}
				else logger.debug("No roles found");
			}
			
			if (bean.getRoles().size()>0) 
			{
				logger.debug("Performing authorization...");
				AuthorizationInternalService authorization = AuthorizationServiceFactory.generateService();
				response = authorization.authorize(ticket, action, resource);
				logger.debug("Authorization performed");
			}
			
		} catch (RuntimeException e)
		{
			logger.error("Inconsistent entries",e);
			response = false;
		}
		
		logger.debug("Response "+response);
		return response;
	}

	/**
	 * 
	 * @param responseObject
	 * @return
	 */
	private Response generateResponse (Object responseObject)
	{
		String value = null;
		
		try 
		{
			value = Utils.getMapper().writeValueAsString(responseObject);
			logger.debug("Response "+value);
			
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

}
