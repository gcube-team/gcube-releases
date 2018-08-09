package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.RoleManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.swagger.config.Bootstrap;
import org.gcube.portal.social.networking.swagger.config.SwaggerConstants;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.TokensUtils;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;


/**
 * REST interface for the social networking library (people). Used by OAUTH 2.0 apps/users.
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/people")
@Api(value=SwaggerConstants.PEOPLE, authorizations={@Authorization(value = Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF), @Authorization(value = Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF)})
public class People {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(People.class);

	@GET
	@Path("profile")
	@ApiOperation(value = "Retrieve user's profile", notes="Retrieve the user's profile. The user in this case is the one bound to the token which can be of any kind (qualified, default)", 
	response=ResponseBean.class, nickname="profile")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of user's profile, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		GCubeUser user = null;
		String scope = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		if(!TokensUtils.isUserToken(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage("User's information can only be retrieved through a user token");
			logger.warn("Trying to access users method via a token different than 'user-token' is not allowed");
		}else{

			try{
				UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
				RoleManager roleManager = RoleManagerWSBuilder.getInstance().getRoleManager();
				GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
				user = userManager.getUserByUsername(username);

				Map<String, Object> toReturn = new HashMap<String, Object>();
				toReturn.put("username", user.getUsername());
				toReturn.put("avatar", user.getUserAvatarURL());
				toReturn.put("fullname", user.getFullname());
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(user.getUserId(), groupManager.getGroupIdFromInfrastructureScope(scope));
				List<String> rolesNames = new ArrayList<String>();
				for (GCubeRole gCubeRole : roles) {
					rolesNames.add(gCubeRole.getRoleName());
				}
				toReturn.put("roles", rolesNames);

				responseBean.setResult(toReturn);
				responseBean.setSuccess(true);
			}catch(Exception e){
				logger.error("Unable to retrieve user's profile", e);
				responseBean.setMessage(e.getMessage());
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}

		return Response.status(status).entity(responseBean).build();
	}

}
