package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.social.networking.caches.UsersInInfrastructureCache;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.RoleManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (users).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/users")
@Api(tags={"users"}, protocols="https", authorizations={@Authorization(value="gcube-token")})
public class Users {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Users.class);
	private static final String NOT_USER_TOKEN_CONTEXT_USED = "User's information can only be retrieved through a user token (not qualified)";

	@GET
	@Path("get-custom-attribute/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read a user's custom attribute", notes="Read a user's custom attribute. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-custom-attribute")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful read of the attribute, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 404, message = "Such an attribute doesn't exist", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response readCustomAttr(
			@QueryParam("attribute") 
			@NotNull(message="attribute name is missing") 
			@ApiParam(name="attribute", required=true, allowMultiple=false, value="The key of the attribute to read")
			String attributeKey
			) throws ValidationException {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		if(!Utils.isUserTokenDefault(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage(NOT_USER_TOKEN_CONTEXT_USED);
			logger.warn("Trying to access users method via a token different than USER is not allowed");
		}else{
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			try{

				GCubeUser user = userManager.getUserByUsername(username);
				String toReturn = (String)userManager.readCustomAttr(user.getUserId(), attributeKey);
				responseBean.setSuccess(true);
				responseBean.setResult(toReturn);

			}catch(Exception e){

				logger.error("Unable to retrieve attribute for user.", e);
				responseBean.setMessage(e.toString());
				responseBean.setSuccess(false);
				status = Status.NOT_FOUND;

			}
		}
		return Response.status(status).entity(responseBean).build();
	}

	//	@PUT
	//	@Path("update-custom-attribute")
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@ApiOperation(value = "Update a user's custom attribute", notes="Update a user's custom attribute. The user is the one owning the token", 
	//	response=ResponseBean.class, nickname="update-custom-attribute")
	//	@ApiResponses(value = {
	//			@ApiResponse(code = 200, message = "Successful update of the attribute, the new value is reported in the 'result' field of the returned object", response = ResponseBean.class),
	//			@ApiResponse(code = 400, message = "Key or value for the new attribute missing", response=ResponseBean.class),
	//			@ApiResponse(code = 500, message = ErrorMessages.errorMessageApiResult, response=ResponseBean.class),
	//			@ApiResponse(code = 304, message = "Attribute not modified", response=ResponseBean.class)})
	//	public Response updateCustomAttr(
	//			@NotNull(message="input is missing") 
	//			@ApiParam(name="input", required=true, allowMultiple=false, value="The object having an attribute key and a value key for the new value")
	//			String inputJsonObj 
	//			) throws ValidationException{
	//
	//		Caller caller = AuthorizationProvider.instance.get();
	//		String username = caller.getClient().getId();
	//		ResponseBean responseBean = new ResponseBean();
	//		Status status = Status.OK;
	//
	//		try{
	//
	//			// Jackson parser
	//			ObjectMapper mapper = new ObjectMapper();
	//			JsonNode actualObj = mapper.readTree(inputJsonObj);
	//			String attributeKey = actualObj.get("attribute").asText();
	//			String newValue = actualObj.get("value").asText(); 
	//
	//			if(attributeKey == null || attributeKey.isEmpty() || newValue == null){
	//
	//				logger.error("Missing/wrong request parameters");
	//				status = Status.BAD_REQUEST;
	//				responseBean.setMessage(ErrorMessages.missingParameters);
	//				return Response.status(status).entity(responseBean).build();
	//
	//			}
	//
	//			GCubeUser user = userManager.getUserByUsername(username);
	//			userManager.saveCustomAttr(user.getUserId(), attributeKey, newValue);
	//			responseBean.setSuccess(true);
	//			responseBean.setResult(newValue);
	//
	//		}catch(Exception e){
	//
	//			logger.error("Unable to set attribute for user.", e);
	//			status = Status.NOT_MODIFIED;
	//			responseBean.setMessage(e.toString());
	//
	//		}
	//
	//		return Response.status(status).entity(responseBean).build();
	//	}

	@GET
	@Path("get-fullname")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read the user's fullname", notes="Read the user's fullname. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-fullname")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The user's fullname is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getUserFullname(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String fullName = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		if(!Utils.isUserTokenDefault(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage(NOT_USER_TOKEN_CONTEXT_USED);
			logger.warn("Trying to access users method via a token different than USER is not allowed");
		}else{
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			try{

				GCubeUser user = userManager.getUserByUsername(username);
				fullName = user.getFullname();
				logger.info("Found fullname " + fullName + " for user " +  username);
				responseBean.setResult(fullName);
				responseBean.setSuccess(true);

			}catch(Exception e){

				logger.error("Unable to retrieve attribute for user.", e);
				status = Status.INTERNAL_SERVER_ERROR;

			}
		}
		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-email")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read the user's email address", notes="Read the user's email address. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-email")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The user's email value is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getUserEmail(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String email = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		if(!Utils.isUserTokenDefault(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage(NOT_USER_TOKEN_CONTEXT_USED);
			logger.warn("Trying to access users method via a token different than USER is not allowed");
		}else{
			try{
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				GCubeUser user = userManager.getUserByUsername(username);
				email = user.getEmail();
				logger.info("Found email " + email + " for user " +  username);
				responseBean.setResult(email);
				responseBean.setSuccess(true);

			}catch(Exception e){

				logger.error("Unable to retrieve attribute for user.", e);
				status = Status.INTERNAL_SERVER_ERROR;

			}
		}
		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-profile")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read the user's profile", notes="Read the user's profile. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-profile")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The user's profile is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getUserProfile(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		GCubeUser user = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		if(!Utils.isUserTokenDefault(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage(NOT_USER_TOKEN_CONTEXT_USED);
			logger.warn("Trying to access users method via a token different than USER is not allowed");
		}else{
			try{
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				user = userManager.getUserByUsername(username);
				responseBean.setResult(user);
				responseBean.setSuccess(true);
			}catch(Exception e){
				logger.error("Unable to retrieve user's profile", e);
				responseBean.setMessage(e.getMessage());
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-all-usernames")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserNames(){

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		List<String> usernames = new ArrayList<String>();
		try{

			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			long groupId = groupManager.getGroupIdFromInfrastructureScope(ScopeProvider.instance.get());

			// first retrieve ids
			List<Long> userIds = userManager.getUserIdsByGroup(groupId);

			// check info in cache when available
			UsersInInfrastructureCache cache = UsersInInfrastructureCache.getSingleton();

			for (Long userId : userIds) {
				if(cache.getUser(userId) == null){
					GCubeUser user = userManager.getUserById(userId);
					if(user != null){
						usernames.add(user.getUsername());
						cache.pushEntry(userId, user);
					}
				}else
					usernames.add(cache.getUser(userId).getUsername());
			}

			responseBean.setResult(usernames);
			responseBean.setSuccess(true);
		}catch(Exception e){
			logger.error("Unable to retrieve user's usernames", e);
			responseBean.setMessage(e.getMessage()); 
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}
	
	@GET
	@Path("get-all-fullnames-and-usernames")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFullnamesAndUsernames(){

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
	
		Map<String, String> usernamesAndFullnames = new HashMap<String, String>();
		try{

			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			long groupId = groupManager.getGroupIdFromInfrastructureScope(ScopeProvider.instance.get());

			// first retrieve ids
			List<Long> userIds = userManager.getUserIdsByGroup(groupId);

			// check info in cache when available
			UsersInInfrastructureCache cache = UsersInInfrastructureCache.getSingleton();

			for (Long userId : userIds) {
				if(cache.getUser(userId) == null){
					GCubeUser user = userManager.getUserById(userId);
					if(user != null){
						usernamesAndFullnames.put(user.getUsername(), user.getFullname());
						cache.pushEntry(userId, user);
					}
				}else
					usernamesAndFullnames.put(cache.getUser(userId).getUsername(), cache.getUser(userId).getFullname());
			}

			responseBean.setResult(usernamesAndFullnames);
			responseBean.setSuccess(true);
		}catch(Exception e){
			logger.error("Unable to retrieve user's usernames", e);
			responseBean.setMessage(e.getMessage()); 
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-usernames-by-global-role")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsernamesByGlobalRole(@QueryParam("role-name") String roleName){

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		List<String> usernames = new ArrayList<String>();
		try{

			RoleManager roleManager = RoleManagerWSBuilder.getInstance().getRoleManager();
			long globalRoleId = roleManager.getRoleIdByName(roleName);
			if(globalRoleId > 0){
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				List<GCubeUser> users = userManager.listUsersByGlobalRole(globalRoleId);
				if(users != null){
					for (GCubeUser gCubeUser : users) {
						usernames.add(gCubeUser.getUsername());
					}
				}
				responseBean.setResult(usernames);
				responseBean.setSuccess(true);
			}else{
				responseBean.setMessage("No global role exists whit such a name");
				status = Status.BAD_REQUEST;
			}
		}catch(Exception e){
			logger.error("Unable to retrieve user's usernames", e);
			responseBean.setMessage(e.getMessage()); 
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}
	
	@GET
	@Path("user-exists")
	@Produces(MediaType.APPLICATION_JSON)
	public Response existUser(@QueryParam("username") String username){

		ResponseBean responseBean = new ResponseBean();
		String messageOnError = "This method can be invoked only by using an application token bound to the root context";
		Status status = Status.BAD_REQUEST;
		responseBean.setMessage(messageOnError);
		responseBean.setSuccess(false);
		Caller caller = AuthorizationProvider.instance.get();
		
		if(!Utils.isApplicationToken(caller))
			return Response.status(status).entity(responseBean).build();
		
		ScopeBean scopeInfo = new ScopeBean(ScopeProvider.instance.get());
		
		if(!scopeInfo.is(Type.INFRASTRUCTURE))
			return Response.status(status).entity(responseBean).build();
		
		try{
			
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			GCubeUser user = userManager.getUserByUsername(username);
			responseBean.setSuccess(true);
			responseBean.setMessage(null);
			responseBean.setResult(user != null);
			status = Status.OK;
			
		}catch(Exception e){
			logger.error("Unable to retrieve such information", e);
			responseBean.setMessage(e.getMessage()); 
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}

}
