package org.gcube.portal.social.networking.ws.methods.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
import org.gcube.portal.social.networking.caches.UsersCache;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.RoleManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.swagger.config.Bootstrap;
import org.gcube.portal.social.networking.swagger.config.SwaggerConstants;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.TokensUtils;
import org.gcube.portal.social.networking.ws.utils.UserProfileExtendedWithVerifiedEmail;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * REST interface for the social networking library (users).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/users")
@Api(value=SwaggerConstants.USERS, authorizations={@Authorization(value = Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF), @Authorization(value = Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF)})
public class Users {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Users.class);
	private static final String NOT_USER_TOKEN_CONTEXT_USED = "User's information can only be retrieved through a user token (not qualified)";
	private static final List<String> GLOBAL_ROLES_ALLOWED_BY_LOCAL_CALL_METHOD = Arrays.asList("DataMiner-Manager","Quota-Manager");

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
			@ApiParam(name="attribute", required=true, allowMultiple=false, value="The key of the attribute to be read")
			String attributeKey
			) throws ValidationException {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		if(!TokensUtils.isUserTokenDefault(caller)){
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

		if(!TokensUtils.isUserTokenDefault(caller)){
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
		if(!TokensUtils.isUserTokenDefault(caller)){
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

		if(!TokensUtils.isUserTokenDefault(caller)){
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
	
	private static final Function<GCubeUser, UserProfileExtendedWithVerifiedEmail> GCUBE_TO_EXTENDED_PROFILE_MAP_WITH_VERIFIED_EMAIL
	= new Function<GCubeUser, UserProfileExtendedWithVerifiedEmail>() {

		@Override
		public UserProfileExtendedWithVerifiedEmail apply(GCubeUser t) {

			if(t == null)
				return null;

			UserProfileExtendedWithVerifiedEmail profile = new UserProfileExtendedWithVerifiedEmail(t.getUsername(), null, t.getUserAvatarURL(), t.getFullname());
			profile.setEmail(t.getEmail());
			profile.setFirstName(t.getFirstName());
			profile.setJobTitle(t.getJobTitle());
			profile.setLastName(t.getLastName());
			profile.setLocationIndustry(t.getLocation_industry());
			profile.setMale(t.isMale());
			profile.setMiddleName(t.getMiddleName());
			profile.setVerifiedEmail(true);
			return profile;
		}
	};
	
	@GET
	@Path("get-oauth-profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserOAuthProfile(){
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String scope = ScopeProvider.instance.get();
		GCubeUser user = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		UserProfileExtendedWithVerifiedEmail userWithEmailVerified = null;
		if(! (TokensUtils.isUserTokenDefault(caller) || TokensUtils.isUserTokenQualified(caller))){
			status = Status.FORBIDDEN;
			responseBean.setMessage(NOT_USER_TOKEN_CONTEXT_USED);
			logger.warn("Trying to access users method via a token different than USER is not allowed");
		}else{
			try{
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				RoleManager roleManager = RoleManagerWSBuilder.getInstance().getRoleManager();
				GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
				user = userManager.getUserByUsername(username);
				userWithEmailVerified = GCUBE_TO_EXTENDED_PROFILE_MAP_WITH_VERIFIED_EMAIL.apply(user);
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(user.getUserId(), groupManager.getGroupIdFromInfrastructureScope(scope));
				List<String> rolesNames = new ArrayList<String>();
				for (GCubeRole gCubeRole : roles) {
					rolesNames.add(gCubeRole.getRoleName());
				}
				userWithEmailVerified.setRoles(rolesNames);

				//responseBean.setResult(userWithEmailVerified);
				responseBean.setSuccess(true);
			}catch(Exception e){
				logger.error("Unable to retrieve user's profile", e);
				responseBean.setMessage(e.getMessage());
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("returning: "+userWithEmailVerified.toString());
		return Response.status(status).entity(userWithEmailVerified).build();
	}


	@GET
	@Path("get-all-usernames")
	@ApiOperation(value = "Get the list of usernames belonging to a given context", notes="Retrieve the list of usernames for the user belonging to the context linked to the provided token.", 
	response=ResponseBean.class, nickname="get-all-usernames")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The list of usernames is put into the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
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
			UsersCache cache = UsersCache.getSingleton();

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
	@ApiOperation(value = "Get the map of couples username/fullname of the users belonging to a given context", notes="Get the map of couples username/fullname of the users belonging to the context linked to the provided token.", 
	response=ResponseBean.class, nickname="get-all-fullnames-and-usernames")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The map is put into the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
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
			UsersCache cache = UsersCache.getSingleton();

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
	@ApiOperation(value = "Get the list of users having a given global-role", notes="Get the list of users having a given global-role, e.g. 'Administrator'.", 
	response=ResponseBean.class, nickname="get-usernames-by-global-role")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The list is put into the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsernamesByGlobalRole(
			@ApiParam(value = "role-name: the name of the role to be checked (e.g. Administrator)", required = true) 
			@QueryParam("role-name") String roleName){

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		// this method can only be called from IS scope (except for roles in GLOBAL_ROLES_ALLOWED_BY_LOCAL)
		ScopeBean scopeInfo = new ScopeBean(ScopeProvider.instance.get());

		if(!scopeInfo.is(Type.INFRASTRUCTURE)){
			status = Status.BAD_REQUEST;
			responseBean.setMessage("This method can only be called with an infrastructure token");
		}else{

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
		}
		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-usernames-by-role")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsernamesByRole(
			@QueryParam("role-name") String roleName){

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		String context = ScopeProvider.instance.get();
		List<String> usernames = new ArrayList<String>();
		try{
			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			RoleManager roleManager = RoleManagerWSBuilder.getInstance().getRoleManager();
			long roleId = roleManager.getRoleIdByName(roleName);
			if(roleId > 0){
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				List<GCubeUser> users = null;
				long groupId = groupManager.getGroupIdFromInfrastructureScope(context);
				// first check if for any reason this is a global role, then (if result is null or exception arises) check for site role
				// Global role's users are retrieved much faster
				try{
					if(GLOBAL_ROLES_ALLOWED_BY_LOCAL_CALL_METHOD.contains(roleName)){	
						// TODO inconsistent value can be returned
						users = userManager.listUsersByGlobalRole(roleId);
					}
				}catch(Exception globalExp){
					logger.warn("Failed while checking for global role... trying with local one", globalExp);
				}

				if(users == null || users.isEmpty()){
					logger.debug("User list is still null/empty, checking for local information");
					users = userManager.listUsersByGroupAndRole(groupId, roleId);
				}

				if(users != null){
					for (GCubeUser gCubeUser : users) {
						usernames.add(gCubeUser.getUsername());
					}
				}
				responseBean.setResult(usernames);
				responseBean.setSuccess(true);
			}else{
				responseBean.setMessage("No role exists whit such a name");
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

		if(!TokensUtils.isApplicationToken(caller))
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