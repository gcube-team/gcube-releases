package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

import java.util.List;

import javax.validation.ValidationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.RoleManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.swagger.config.Bootstrap;
import org.gcube.portal.social.networking.swagger.config.SwaggerConstants;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.TokensUtils;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;


/**
 * REST interface for the social networking library (vres).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/vres")
@Api(value=SwaggerConstants.VRES, authorizations={@Authorization(value = Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF), @Authorization(value = Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF)})
public class VREs {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Users.class);

	@SuppressWarnings("unchecked")
	@GET
	@Path("get-my-vres/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMyVres(
			@DefaultValue("false") @QueryParam("getManagers") boolean getManagers
			) throws ValidationException {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		if(!TokensUtils.isUserToken(caller)){
			status = Status.FORBIDDEN;
			responseBean.setMessage("This method can only be invoked with a user token!");
			logger.warn("Trying to access get-my-vres method via a token different than USER is not allowed");
		}else{
			GroupManager gmManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
			RoleManager roleManager = RoleManagerWSBuilder.getInstance().getRoleManager();
			try{

				GCubeUser user = userManager.getUserByUsername(username);
				List<GCubeGroup> vres = gmManager.listVresByUser(user.getUserId());
				JSONArray toReturn = new JSONArray();

				for (GCubeGroup group : vres) {

					// # ticket 9333
					JSONObject obj = new JSONObject();
					obj.put("name", group.getGroupName());
					obj.put("context", gmManager.getInfrastructureScope(group.getGroupId()));
					obj.put("description", group.getDescription());
					//obj.put("thumbnail_url", ...); // TODO
					JSONArray managers = new JSONArray();

					if(getManagers){
						List<GCubeUser> vreManagers = userManager.listUsersByGroupAndRole(group.getGroupId(), 
								roleManager.getRoleIdByName(GatewayRolesNames.VRE_MANAGER.getRoleName()));

						for (GCubeUser vreManager : vreManagers) {
							JSONObject manager = new JSONObject();
							manager.put("username", vreManager.getUsername());
							manager.put("fullname", vreManager.getFullname());
							managers.add(manager);
						}

						obj.put("managers", managers);
					}
					toReturn.add(obj);
				}

				responseBean.setSuccess(true);
				responseBean.setResult(toReturn);

			}catch(Exception e){

				logger.error("Unable to retrieve vres for user.", e);
				responseBean.setMessage(e.toString());
				responseBean.setSuccess(false);
				status = Status.INTERNAL_SERVER_ERROR;

			}
		}
		return Response.status(status).entity(responseBean).build();
	}	
}
