package it.eng.rdlab.soa3.um.rest.jaxrs;

import it.eng.rdlab.soa3.um.rest.bean.OrganizationModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.exceptions.UMJSONParserException;
import it.eng.rdlab.soa3.um.rest.impl.GroupManagerImpl;
import it.eng.rdlab.soa3.um.rest.impl.OrganizationManagerImpl;
import it.eng.rdlab.soa3.um.rest.impl.RoleManagerImpl;
import it.eng.rdlab.soa3.um.rest.impl.UserManagerImpl;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.OrganizationJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.OrganizationsJaxbBean;
import it.eng.rdlab.soa3.um.rest.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.sun.jersey.api.core.HttpContext;


/**
 * 
 * This class is a RESTful WS that manages operations on organizations
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@Path("/organizationmanager")
public class OrganizationManager 
{
	private OrganizationManagerImpl organizationManager;
	private ConfigurationManager cm;
	private Log logger;
	private UserManagerImpl userManager;
	private GroupManagerImpl groupManager;
	private RoleManagerImpl roleManager;

	public OrganizationManager() throws Exception
	{
		logger  = LogFactory.getLog(OrganizationManager.class);
		cm = ConfigurationManager.getInstance();
		this.organizationManager = new OrganizationManagerImpl(cm.getLdapUrl());
		this.groupManager = new GroupManagerImpl(cm.getLdapUrl());
		this.userManager = new UserManagerImpl(cm.getLdapUrl());
		this.roleManager = new RoleManagerImpl(cm.getLdapUrl());
	}

	/**
	  * Creates organization by organizationName
	  *  
	  * @param organizationName String
	  * @return organization creation process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{organizationName}")
	public Response createOrganization(@PathParam("organizationName")String organizationName, @Context HttpContext context )
	{

		logger.debug("Creating organization " + organizationName);
		String organization = this.organizationManager.createOrganization(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

		if(organization == null)
		{
			logger.error("Organization was not created" +  organizationName);
			return Response.status(Status.BAD_REQUEST)
					.entity("Organization:  "
							+  organizationName + " was not created, check if the organization already exists")
							.build();
		}else
		{
			logger.debug("Organization has been created successfully "  + organization);
			return Response.status(Status.OK).entity("Organization has been created successfully ").build();	
		}
	}

	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrganization(@Context HttpContext context )
	{
		return createOrganization(null, context);
	}

	/**
	  * Gets organization by organizationName
	  *  
	  * @param organizationName String
	  * @return the organization json object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{organizationName}")
	public String getOrganization(@PathParam("organizationName")String organizationName, @Context HttpContext context )
	{
		logger.debug("getting organization " + organizationName);
		OrganizationModel organization = this.organizationManager.getOrganizationByName(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

		if(organization == null)
		{
			logger.error("Organization "+ organizationName +" is not present ");
			return "Organization "+ organizationName +" is not present";
		}else
		{
			logger.debug("Organization "+organizationName+ " is present" );
			try {
				String response = Utils.getMapper().writeValueAsString(new OrganizationJaxbBean(organization));

				return response;
			} catch (JsonGenerationException e) {

				logger.error("get organization unsuccessful due to json parse error  ");
				throw new UMJSONParserException("Unable to generate JSON ", e);
			} catch (JsonMappingException e) {

				logger.error("get organization unsuccessful due to json parse error  ");
				throw new UMJSONParserException("Unable to map JSON ", e);
			} catch (IOException e) {

				logger.error("get organization unsuccessful due to json parse error  ");
				throw new UMJSONParserException(
						"IO Exception while parsing JSON ", e);
			}
		}
	}
	
	/**
	  * Lists organizations
	  *  
	  * @return organizations String object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/organizations")
	public String listOrganizations(@Context HttpContext context )
	{
		logger.debug("Listing all the organizations...");
		List<OrganizationModel> organizations = this.organizationManager.listOrganizations(cm.getLdapUserDN(), cm.getLdapPwd());
		Iterator<OrganizationModel> organizationsIter = organizations.iterator();
		List<OrganizationJaxbBean> organizationsBean= new ArrayList<OrganizationJaxbBean>();

		while(organizationsIter.hasNext())
		{
			OrganizationModel current = organizationsIter.next();
	        OrganizationJaxbBean currentBean = new OrganizationJaxbBean(current.getOrganizationName(),current.getDescription());
			organizationsBean.add(currentBean);
		}

		try 
		{
			String response = Utils.getMapper().writeValueAsString(new OrganizationsJaxbBean(organizationsBean));
			return response;

		} catch (JsonGenerationException e) 
		{
			logger.error("get organizations unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) 
		{
			logger.error("get organizations unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) 
		{
			logger.error("get organizations unsuccessful due to json parse error  ");
			throw new UMJSONParserException("IO Exception while parsing JSON ", e);
		}

	}

	/**
	  * Deletes organization by organizationName
	  *  
	  * @param organizationName String 
	  * @return organization deletion process response
	  * 
	  */
	@DELETE
	@Path("/{organizationName}")
	public Response deleteOrganization( @PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{
		logger.debug("Deleting organization "+organizationName);
		boolean isDeleted = this.organizationManager.deleteOrganization(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

		if(isDeleted)
		{
			logger.debug("Delete organization successful: " + isDeleted);

			return Response.status(Status.OK)
					.entity("organization "+ organizationName+ " deleted successfully ").build();
		}else{
			logger.error("Delete organization unsuccessful ");

			return Response.status(Status.BAD_REQUEST)
					.entity("organization "+ organizationName + " cannot be deleted  ").build();
		}
	}

	/**
	  * Deletes all organizations
	  *  
	  * @return organizations deletion process response
	  * 
	  */
	@DELETE
	@Path("/organizations")
	public Response deleteOrganizations(@Context HttpContext context )
	{
	logger.debug("Deleting all the organizations...");
		int isDeleted = this.organizationManager.deleteOrganizations(cm.getLdapUserDN(), cm.getLdapPwd());

		switch (isDeleted)
		{
		case 0:
			logger.debug("Delete organization successful: " + isDeleted);
			return Response.status(Status.OK).entity("organizations deleted successfully ").build();

		case 1:
			logger.debug("Delete organization successful: " + isDeleted);
			return Response.status(Status.OK).entity("WARN: some organizations have not been deleted ").build();
		default:
			logger.error("Delete organization unsuccessful ");
			return Response.status(Status.NOT_MODIFIED).entity("organizations cannot be deleted  ").build();
		}

	}



}
