package it.eng.rdlab.soa3.um.rest.jaxrs;

import it.eng.rdlab.soa3.um.rest.bean.UserModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.exceptions.UMJSONParserException;
import it.eng.rdlab.soa3.um.rest.impl.UserManagerImpl;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.CredentialsJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.GroupJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.RoleJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.UserJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.UsersJaxbBean;
import it.eng.rdlab.soa3.um.rest.utils.Utils;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.sun.jersey.core.util.Base64;

/**
 * 
 * This class is a RESTful WS that manages operations on users
 * 
 * @author Ermanno Travaglino, Ciro Formisano
 * @version 1.0
 * 
 */
@Path("/usermanager")
public class UserManager 
{
	private  UserManagerImpl userManager;
	private ConfigurationManager cm;
	private Log logger ;


	public UserManager() throws Exception
	{
		this.logger = LogFactory.getLog(this.getClass());
		cm = ConfigurationManager.getInstance();
		this.userManager = new UserManagerImpl(cm.getLdapUrl());
	}

	/**
	  * Creates user by UserJaxbBean
	  *  
	  * @param userJaxbBean UserJaxbBean
	  * @return user creation process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(UserJaxbBean userJaxbBean, @Context HttpContext context )
	{
		logger.debug("Creating new user...");
		UserManagerImpl userManager = new UserManagerImpl(cm.getLdapUrl());
		String username = userJaxbBean.getUsername();
		UserModel userModel = new it.eng.rdlab.soa3.um.rest.bean.UserModel(username, userJaxbBean.getFirstname(), userJaxbBean.getLastname(), userJaxbBean.getPassword());
		
		if (userJaxbBean.getCertificateDN() != null) userModel.setCertDN(userJaxbBean.getCertificateDN());
		
		String organizationName = userJaxbBean.getOrganizationName();

		if (organizationName == null || organizationName.length() == 0)
		{
			logger.debug("Organization Name is null. Set organization to "+cm.getLdapDummyRoot());
			organizationName = cm.getLdapDummyRoot();
		}
		
		String userName = userManager.createUser(userModel,organizationName,cm.getLdapUserDN(), cm.getLdapPwd());
		
		if(userName == null)
		{
			logger.error("User was not created" +  username);


			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST)
					.entity("User  "
							+  username + " was not created")
							.build());
		}else
		{
			logger.debug("User has been created successfully "  + username);

			return Response.status(Status.OK).entity("User has been created successfully ").build();	
		}
	}

	
	
	/**
	  * Updates user's credentials
	  *  
	  * @param userName String
	  * @param credsBean CredentialsJaxbBean
	  * @param organizationName String (optional)
	  * @return user credentials updating process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Path("/{username}/{organizationName:[^;/]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(@PathParam("username")String userName,CredentialsJaxbBean credsBean, @PathParam("organizationName")String organizationName, @Context HttpContext context )
	{
		logger.debug("Changing password..");
		int responseCode = this.userManager.changePassword(userName,credsBean.getOldpassword(), credsBean.getNewpassword(), organizationName, cm.getLdapUserDN(), cm.getLdapPwd());

		
		switch (responseCode)
		{
		case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_OK:
			logger.debug("User change password successful "  + userName);
			return Response.status(Status.OK).entity("User change password successful  ").build();	
		
		case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_WRONG_USER_PASSWORD:
			logger.error("User password cannot be changed for user " +  userName + " because the current password provided is not correct");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
					.entity("user  "+  userName + " password cannot be changed  because the current password provided is not correct ").build());
			
		case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_EQUAL_PASSWORDS:
			logger.error("User password cannot be changed for user " +  userName + " because the current password is equal to the new password");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("For user  "+  userName + " password cannot be changed  because the current password  is equal to the new password ")
							.build());
	
		case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_INVALID_NEW_PASSWORD:
			logger.error("User password cannot be changed for user " +  userName + " because the new password value is invalid");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("For user  "+  userName + " password cannot be changed   because the  new password value is invalid")
							.build());
			
		case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_USER_NOT_FOUND:
			logger.error("User password cannot be changed for user " +  userName + " because the user is not present");

			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("For user  "+  userName + " password cannot be changed  because the user is not present ")
							.build());
		default:
		//case it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager.CHANGE_PASSWORD_GENERIC_ERROR
		logger.error("User password cannot be changed for user " +  userName + " due to internal server error");
		throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
				.entity("For user  "
						+  userName + " due to internal server error ")
						.build());
		}
		
	}
	
	/**
	 * 
	 * Updates the user with a certain userid and organization name
	 * 
	 * @param userJaxbBean the bean of the user to be updated with the old userid and organization name and the new values
	 * @param context http context
	 * @return ok if the user has been updated
	 */
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser (UserJaxbBean userJaxbBean, @Context HttpContext context )
	{
		logger.debug("Updating user...");
		logger.debug("Trying to update all the fields but password");
		
		if (userJaxbBean.getPassword()!= null) logger.warn("For updating password use the specific REST call");
		
		UserManagerImpl userManager = new UserManagerImpl(cm.getLdapUrl());
		String username = userJaxbBean.getUsername();
		UserModel userModel = new it.eng.rdlab.soa3.um.rest.bean.UserModel(username, userJaxbBean.getFirstname(), userJaxbBean.getLastname(), null);
		
		if (userJaxbBean.getCertificateDN() != null) userModel.setCertDN(userJaxbBean.getCertificateDN());


		if (userJaxbBean.getOrganizationName() == null || userJaxbBean.getOrganizationName().length() == 0)
		{
			logger.debug("Organization Name is null. Set organization to "+cm.getLdapDummyRoot());
			userJaxbBean.setOrganizationName(cm.getLdapDummyRoot());
		}
		

		if(!userManager.updateUser(userModel,userJaxbBean.getOrganizationName(),cm.getLdapUserDN(), cm.getLdapPwd()))
		{
			logger.error("User was not updated" +  username);


			throw new WebApplicationException(
					Response.status(Status.NOT_MODIFIED)
					.entity("User  "
							+  username + " was not updated")
							.build());
		}else
		{
			logger.debug("User has been updated successfully "  + username);

			return Response.status(Status.OK).entity("User has been updated successfully ").build();	
		}

	}

	/**
	  * Gets user by userName and organizationName
	  *  
	  * @param userName String 
	  * @param organizationName String (optional)
	  * @return the user string object
	  * 
	  * 
	  */
	@GET
	@Path("/{username}/{organizationName:[^;/]*}")
	public Response getUser(@PathParam("username") String username, @PathParam("organizationName") String organizationName,  @Context HttpContext context )
	{
		logger.debug("Getting user");
		
		if (organizationName == null || organizationName.isEmpty()) organizationName = cm.getLdapDummyRoot();
		
		UserModel user =  this.userManager.getUser(username, organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

		if(user == null)
		{
			logger.error("user not present "+ username);
			return Response.status(Status.NOT_FOUND).entity("User  "+ username +" is not present under organization " + organizationName).build();
		}

		try 
		{

			UserJaxbBean responseBean = new UserJaxbBean(user.getUserId(), user.getFirstname(), user.getLastname(), user.getPassword());
			responseBean.setCertificateDN(user.getCertDN());
			
			if (organizationName != null && organizationName.trim().length() > 0) responseBean.setOrganizationName(organizationName);
			
			String value = Utils.getMapper().writeValueAsString(responseBean);

			return Response.status(Status.OK).entity(value).build();
			
		} catch (JsonGenerationException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {

			logger.error("get user unsuccessful due to json parse error  ");
			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}

	}
	
	


	
	
	/**
	  * Gets users by organizationName
	  *  
	  * @param organizationName String
	  * @param attributes AttributeMap
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/users/{organizationName}")
	public String getUsers(@PathParam("organizationName") String organizationName,@Context HttpContext context )
	{
		logger.debug("getting users...");
		List<UserModel> users = this.userManager.listUsersByOrganizationAndAttributes(organizationName, null, cm.getLdapUserDN(), cm.getLdapPwd());
		Iterator<UserModel> usersIter = users.iterator();
		List<String> userNames= new ArrayList<String>();
		
		while(usersIter.hasNext())
		{
			userNames.add(usersIter.next().getUserId());
		}

		try {
			String response =  Utils.getMapper().writeValueAsString(new UsersJaxbBean(userNames));

			return response;

		} catch (JsonGenerationException e) {

			logger.error("get users unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {

			logger.error("get users unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {

			logger.error("get users unsuccessful due to json parse error  ");
			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}
		
	}

	/**
	  * Gets all users
	  *  
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/users")
	public String getUsers(@Context HttpContext context )
	{
		return getUsers(cm.getLdapDummyRoot(),context);
	}
	
	
	/**
	  * Gets the user with a certain DN 
	  *  
	  *  @param certificateDN the DN
	  *  @param organizationName the organization (optional)
	  * @return the user string object
	  * 
	  * 
	  */
	@GET
	@Path("/certificate/{certificateDN}/{organizationName:[^;/]*}")
	public Response getUserByDN(@PathParam("certificateDN") String certificateDN,@PathParam("organizationName") String organizationName,@Context HttpContext context )
	{
		logger.debug("getting users...");
		
		if (certificateDN == null || certificateDN.trim().isEmpty())
		{
			logger.error("DN null");
			return Response.status(Status.BAD_REQUEST).entity("DN null").build();
		}
		else
		{
			certificateDN = Base64.base64Decode(certificateDN);
			logger.debug("DN = "+certificateDN);
			
			if (organizationName == null || organizationName.trim().isEmpty()) organizationName = cm.getLdapDummyRoot();
			
			Map<String, String> attributes = new HashMap<String, String> ();
			attributes.put(LdapUserModel.CERTIFICATE, certificateDN);
			List<UserModel> users = this.userManager.listUsersByOrganizationAndAttributes(organizationName, attributes, cm.getLdapUserDN(), cm.getLdapPwd());
			
			if (users == null || users.isEmpty())
			{
				logger.error("User associated with dn "+certificateDN+" under organization " + organizationName+" not present ");
				return Response.status(Status.NOT_FOUND).entity("No user associated with dn "+certificateDN+" under organization " + organizationName+" found").build();

			}
			else
			{
				if (users.size()>1) logger.warn("More than an user is registered with DN "+certificateDN+ " using the first one");
				
				else logger.debug("User found");
				
				UserModel user = users.get(0);
//				UserJaxbBean responseBean = new UserJaxbBean(user.getUserId(), user.getFirstname(), user.getLastname(), user.getPassword());
//				responseBean.setCertificateDN(user.getCertDN());
//				
//				if (organizationName != null && organizationName.trim().length() > 0) responseBean.setOrganizationName(organizationName);

				String value = user.getUserId();
				return Response.status(Status.OK).entity(value).build();
				

			}
			
			
		}
		
	}

	/**
	  * Deletes user by userName (and optionally by organizationName)
	  *  
	  * @param userName String
	  * @param organizationName String (optional) 
	  * @return user deletion process response
	  * 
	  */
	@DELETE
	@Path("/{username}/{organizationName:[^;/]*}")
	public Response deleteUser(@PathParam("username")String username, @PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{

		if ((this.userManager.getUser(username, organizationName,cm.getLdapUserDN(), cm.getLdapPwd()) != null))
		{
			logger.debug("User exists");

			boolean isDeleted = this.userManager.deleteUser(username, organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

			if(isDeleted)
			{
				logger.debug("User "+ username + " has been deleted");

				return Response.status(Status.OK)
						.entity("User "+ username+ " has been deleted").build();
			}else
			{

				logger.error("User "+ username + " cannot be deleted");
				return Response.status(Status.BAD_REQUEST)
						.entity("User "+ username + " cannot be deleted").build();
			}

		}
		else
		{
			logger.debug("User " + username + " doesn't exist");
			return Response.status(Status.NOT_FOUND).entity("User "+ username + " doesn't exist").build();
		}

	}

	/**
	  * Deletes all users by organizationName
	  *  
	  * @param organizationName String
	  * @return users deletion process response
	  * 
	  */
	@DELETE
	@Path("/users/{organizationName}")
	public Response deleteAllUsers(@PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{

		boolean isDeleted = this.userManager.deleteUsers(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());


		if(isDeleted)
		{
			logger.debug("Delete all users successful: " + isDeleted);

			return Response.status(Status.OK)
					.entity("all users deleted successfully ").build();
		}else{
			logger.error("Delete  all user unsuccessful ");

			return Response.status(Status.BAD_REQUEST)
					.entity(" all users cannot be deleted  ").build();
		}
	}

	/**
	  * Deletes all users
	  *  
	  * @return users deletion process response
	  * 
	  */
	@DELETE
	@Path("/users")
	public Response deleteAllUsers(@Context HttpContext context )
	{
		return deleteAllUsers(null, context);
	}
	
	
	/**
	  * Assigns/dismisses role to an user by roleName and userName (and optionally by organizationName)
	  *  
	  * @param roleBean RoleJaxbBean
	  * @param userName String
	  * @param organizationName String (optional)
	  * @return users assignment/dismissal role process response
	  * 
	  */
	@PUT
	@Path("/roles/{username}/{organizationName:[^;/]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignDismissRole(RoleJaxbBean roleBean, @PathParam("username")String userName, @PathParam("organizationName")String organizationName, @Context HttpContext context )
	{
		logger.debug("Assign/dismiss a role to an user");
		String role = roleBean.getRoleName();
		List<UserModel> users = this.userManager.listUsersByRole(role, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
		boolean isAssigned = false;
		boolean isSuccess = false;
		Iterator<UserModel> usersIter = users.iterator();
		
		while(usersIter.hasNext() && !isAssigned)
		{
			isAssigned = userName.equals(usersIter.next().getUserId());
		}

		String operation = null;
		
		if(!isAssigned)
		{
			operation = "assigned";
			logger.debug("assigning role to user ..");
			isSuccess = this.userManager.assignRoleToUser(role, userName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());

		}else
		{
			operation = "dismissed";
			logger.debug("dismissing role from user ..");
			isSuccess = this.userManager.dismissRoleToUser(role, userName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
		}
		if (!isSuccess) 
		{
			logger.error("User was not "+operation+" " +  role);
			throw new WebApplicationException(
					Response.status(Status.NOT_MODIFIED).entity("User  "
							+ userName + " was not "+operation+" " + role)
							.build());
		}else
		{
			logger.debug("User successfully "+operation+" role "  + role);
			return Response.status(Status.OK).entity("User "+operation+" role successfully ").build();	
		}
	}

	/**
	  * Assigns/dismisses group to an user by groupBean and userName
	  *  
	  * @param groupBean GroupJaxbBean
	  * @param userName String
	  * @return users assignment/dismissal group process response
	  * 
	  */
	@PUT
	@Path("/groups/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignDismissGroup(GroupJaxbBean groupBean, @PathParam("username")String userName, @Context HttpContext context )
	{
		logger.debug("Assign/dismiss user to a group");
		String groupName = groupBean.getGroupName();
		String organizationName = groupBean.getOrganizationName();
		List<UserModel> users = this.userManager.listUsersByGroup(groupName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
		boolean isPresent = false;
		boolean isSuccess = false;
		String operation = null;
		Iterator<UserModel> usersIter = users.iterator();
		
		while(usersIter.hasNext() && !isPresent)
		{
			isPresent = userName.equals(usersIter.next().getUserId());
		}

		if(!isPresent)
		{
			logger.debug("adding user to group ...");
			isSuccess = this.userManager.addUserToGroup(userName, groupName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
			operation = "assigned to";

		}else{
			logger.debug("remove user from group ..");
			isSuccess = this.userManager.removeUserFromGroup(userName, groupName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
			operation = "dismissed from";
		}
		if (!isSuccess) 
		{
			logger.error("User was not "+operation+" group" +  groupName);
			throw new WebApplicationException(
					Response.status(Status.NOT_MODIFIED)
					.entity("User  "
							+ userName + " was not "+operation+" group " + groupName)
							.build());
		}else
		{
			logger.debug("User successfully "+operation+" group "  + groupName);
			return Response.status(Status.OK).entity("User "+operation+" group successfully ").build();	
		}
	}

	
	/**
	  * Gets users with a specific role under an organization
	  *  
	  * @param roleName String
	  * @param organizationName String
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/roles/{roleName}/organization/{organizationName}")
	public String getUsersWithRole(@PathParam("roleName")String roleName, @PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{
		logger.debug("Listing users with a role...");
		List<UserModel> users = this.userManager.listUsersByRole(roleName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
		List<String> userNames = new ArrayList<String>();
		Iterator<UserModel> usersIter = users.iterator();
		
		while(usersIter.hasNext())
		{
			userNames.add(usersIter.next().getUserId());
		}

		try {
			String value =  Utils.getMapper().writeValueAsString(new UsersJaxbBean(userNames));

			return value;
		} catch (JsonGenerationException e) {
			logger.error("get organizations unsuccessful due to json parse error  ");

			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {
			logger.error("get organizations unsuccessful due to json parse error  ");

			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {
			logger.error("get organizations unsuccessful due to json parse error  ");

			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}

	}

	
	/**
	  * Gets all users with a specific role
	  *  
	  * @param roleName String
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/roles/{roleName}")
	public String getUsersWithRole(@PathParam("roleName")String roleName,  @Context HttpContext context )
	{
		return getUsersWithRole(roleName, null, context);
	}

	
	/**
	  * Gets users with a specific group under an organization
	  *  
	  * @param groupName String
	  * @param organizationName String
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/groups/{groupName}/{organizationName}")
	public String getUsersWithGroup(@PathParam("groupName")String groupName, @PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{
		List<UserModel> users = this.userManager.listUsersByGroup(groupName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());


		List<String> userNames = new ArrayList<String>();
		Iterator<UserModel> usersIter = users.iterator();
		while(usersIter.hasNext()){
			userNames.add(usersIter.next().getUserId());
		}

		try {
			String value =  Utils.getMapper().writeValueAsString(new UsersJaxbBean(userNames));

			return value;
		} catch (JsonGenerationException e) {
			logger.error("get groups unsuccessful due to json parse error  ");

			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {
			logger.error("get groups unsuccessful due to json parse error  ");

			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {
			logger.error("get groups unsuccessful due to json parse error  ");

			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}

	}

	/**
	  * Gets all users with a specific group
	  *  
	  * @param groupName String
	  * @return the users string object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/groups/{groupName}")
	public String getUsersWithGroup(@PathParam("groupName")String groupName,  @Context HttpContext context )
	{
		return getUsersWithGroup(groupName, null, context);
	}

}



