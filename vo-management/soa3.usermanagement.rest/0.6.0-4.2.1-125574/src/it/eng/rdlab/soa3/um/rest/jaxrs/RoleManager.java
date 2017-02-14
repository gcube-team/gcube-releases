package it.eng.rdlab.soa3.um.rest.jaxrs;

import it.eng.rdlab.soa3.um.rest.bean.RoleModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.exceptions.UMJSONParserException;
import it.eng.rdlab.soa3.um.rest.impl.RoleManagerImpl;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.RoleJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.RolesJaxbBean;
import it.eng.rdlab.soa3.um.rest.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
 * This class is a RESTful WS that manages operations on roles
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@Path("/rolemanager")
public class RoleManager 
{
	private RoleManagerImpl roleManager;
	private ConfigurationManager cm;
	private String AdminDN = "";
	private String AdminPass = "";
	/** Logger **/
	private  Log logger;

	public RoleManager() throws Exception 
	{
		logger  = LogFactory.getLog(this.getClass());
		cm = ConfigurationManager.getInstance();
		AdminDN = cm.getLdapUserDN();
		AdminPass = cm.getLdapPwd();
		this.roleManager = new RoleManagerImpl(cm.getLdapUrl());
	}


	/**
	  * Creates role by roleName and organizationName
	  *  
	  * @param roleBean RoleJaxbBean
	  * @param organizationName String
	  * @return role creation process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{organizationName}")
	public Response createRole(@PathParam("organizationName")String organizationName, RoleJaxbBean roleBean, @Context HttpContext context )
	{

		String roleName = roleBean.getRoleName();
		logger.debug(roleName);
		String roleId =  this.roleManager.createRole(roleName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());


		if(roleId == null)
		{

			logger.error("Role was not created" +  roleName);
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST)
					.entity("Role  "
							+  roleName + " was not created, check if the role already exists")
							.build());
		}else
		{


			logger.debug("Role has been created successfully "  + roleName);
			return Response.status(Status.OK).entity("Role has been created successfully ").build();	
		}
	}

	/**
	  * Creates role by roleName
	  *  
	  * @param roleBean RoleJaxbBean
	  * @return role creation process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRole(RoleJaxbBean rolebean, @Context HttpContext context )
	{
		return createRole(null, rolebean, context);
	}





	/**
	  * Gets all roles by organizationName
	  *  
	  * @param organizationName String
	  * @return the roles string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/roles/{organizationName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRoles(@PathParam("organizationName")String organizationName, @Context HttpContext context )
	{



		List<RoleModel> roles = this.roleManager.listRolesByOrganization(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());


		Iterator<RoleModel> rolesIter = roles.iterator();

		List<String> roleNames= new ArrayList<String>();

		while(rolesIter.hasNext())
		{
			RoleModel current = rolesIter.next();
			roleNames.add(current.getRoleName());
		}

		try {
			String response = Utils.getMapper().writeValueAsString(new RolesJaxbBean(roleNames));

			return response;
		} catch (JsonGenerationException e) {

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}
		
	}

	/**
	  * Gets all roles
	  *  
	  * @return the roles string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/roles/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRoles(@Context HttpContext context )
	{
		return getRoles(null,context);
	}

//	/**
//	  * Updates roles by roleName and organizationName
//	  *  
//	  * @param rolesBean RolesJaxbBean
//	  * @param organizationName String 
//	  * @return roles updating process response
//	  * 
//	  */
//	@POST
//	@Path("/{organizationName}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateRoles(@PathParam("organizationName")String organizationName, RolesJaxbBean rolesBean,  @Context HttpContext context )
//	{
//		List<String> roles = rolesBean.getRoles();
//
//		boolean isadded = this.roleManager.updateRolesOfOrganization(roles, organizationName,cm.getLdapUserDN(), cm.getLdapPwd());
//
//
//		if(!isadded)
//		{
//
//			return Response.status(Status.NOT_MODIFIED).entity("failed to update roles for organization "+ organizationName).build();
//		}else{
//
//			return Response.status(Status.OK).entity("Roles updated successfully for organization "+ organizationName).build();
//		}
////	}
//
//	/**
//	  * Updates roles by roleName
//	  *  
//	  * @param rolesBean RolesJaxbBean
//	  * @return roles updating process response
//	  * 
//	  */
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateRoles(RolesJaxbBean rolesBean,  @Context HttpContext context )
//	{
//		return updateRoles(null, rolesBean, context);
//	}

	/**
	  * Gets roles of user by userName and organizationName
	  *  
	  * @param userName String 
	  * @param organizationName String
	  * @return the roles string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/users/{username}/{organizationName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRolesOfUser(@PathParam("username") String username, @PathParam("organizationName")String organizationName, @Context HttpContext context )
	{

		List<RoleModel> roles = this.roleManager.listRolesByUser(username, organizationName, AdminDN, AdminPass);


		Iterator<RoleModel> usersIter = roles.iterator();
		List<String> roleNames= new ArrayList<String>();

		while(usersIter.hasNext())
		{
			roleNames.add(usersIter.next().getRoleName());
		}
		//	if(roleNames.isEmpty()){
		//		logger.error(" Tenant " + organizationName + " does not contain any configured roles ");
		//		return Response.status(Status.NO_CONTENT).entity("No roles found for user "+ username +" under tenant "+ organizationName).toString();
		//	}

		try {
			String response =  Utils.getMapper().writeValueAsString(new RolesJaxbBean(roleNames));

			return response;
		} catch (JsonGenerationException e) 
		{

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) 
		{

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) 
		{

			logger.error("get roles unsuccessful due to json parse error  ");
			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}
	
	}

	/**
	  * Gets roles of user by userName
	  *  
	  * @param userName String 
	  * @return the roles string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/users/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRolesOfUser(@PathParam("username") String username, @Context HttpContext context )
	{
		return getRolesOfUser(username, null,context);
	}




}
