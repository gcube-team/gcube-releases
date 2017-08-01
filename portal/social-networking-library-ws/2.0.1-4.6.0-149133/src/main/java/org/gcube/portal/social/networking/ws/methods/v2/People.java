package org.gcube.portal.social.networking.ws.methods.v2;

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
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.Utils;
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
public class People {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(People.class);

	@GET
	@Path("profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		GCubeUser user = null;
		String scope = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		if(!Utils.isUserToken(caller)){
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
