package org.gcube.portal.social.networking.ws.methods.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (users).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/users")
public class Users {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Users.class);

	private static final String NOT_USER_TOKEN_CONTEXT_USED = "User's information can only be retrieved through a user token (not qualified)";

	@GET
	@Path("readCustomAttr/")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * A wrapper for the user management library 's readCustomAttr method
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response readCustomAttr(
			@QueryParam("attribute") String attributeKey
			) {

		if(attributeKey == null || attributeKey.isEmpty()){
			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.MISSING_PARAMETERS).build();
		}

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String toReturn;
		if(!Utils.isUserTokenDefault(caller)){
			logger.warn(NOT_USER_TOKEN_CONTEXT_USED);
			return Response.status(Status.FORBIDDEN).entity("User's information can only be retrieved through a user token").build();
		}else{
			try{
				GCubeUser user = UserManagerWSBuilder.getInstance().getUserManager().getUserByUsername(username);
				toReturn = (String)UserManagerWSBuilder.getInstance().getUserManager().readCustomAttr(user.getUserId(), attributeKey);
			}catch(Exception e){
				logger.error("Unable to retrieve attribute for user.", e);
				return Response.status(Status.NOT_FOUND).build();
			}
		}
		logger.info("Attribute " + attributeKey + " retrieved for user " +  username);
		return Response.status(Status.OK).entity(toReturn).build();
	}

	//	@PUT
	//	@Path("updateCustomAttr")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	/**
	//	 * A wrapper for the user management library 's saveCustomAttr method
	//	 * @return
	//	 */
	//	public Response updateCustomAttr(
	//			@FormParam("attribute") String attributeKey,
	//			@FormParam("value") String newValue
	//			){
	//
	//		if(attributeKey == null || attributeKey.isEmpty() || newValue == null){
	//
	//			logger.error("Missing/wrong request parameters");
	//			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
	//
	//		}
	//
	//		Caller caller = AuthorizationProvider.instance.get();
	//		String username = caller.getClient().getId();
	//
	//		try{
	//
	//			GCubeUser user = userManager.getUserByUsername(username);
	//			userManager.saveCustomAttr(user.getUserId(), attributeKey, newValue);
	//
	//		}catch(Exception e){
	//
	//			logger.error("Unable to set attribute for user.", e);
	//			return Response.status(Status.NOT_MODIFIED).build();
	//
	//		}
	//
	//		return Response.status(Status.OK).build();
	//
	//	}

	@GET
	@Path("getUserFullname")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Retrieve user's fullname
	 * @return
	 */
	public Response getUserUsername(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String toReturn = null;
		if(!Utils.isUserTokenDefault(caller)){
			logger.warn(NOT_USER_TOKEN_CONTEXT_USED);
			return Response.status(Status.FORBIDDEN).entity("User's information can only be retrieved through a user token").build();
		}else{
			try{
				GCubeUser user = UserManagerWSBuilder.getInstance().getUserManager().getUserByUsername(username);
				toReturn = user.getFullname();
				logger.info("Found fullname " + toReturn + " for user " +  username);
			}catch(Exception e){
				logger.error("Unable to retrieve attribute for user.", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
			}
		}
		return Response.status(Status.OK).entity(toReturn).build();
	}

	@GET
	@Path("getUserEmail")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Retrieve user's email 
	 * @return
	 */
	public Response getUserEmail(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String toReturn = null;
		if(!Utils.isUserTokenDefault(caller)){
			logger.warn(NOT_USER_TOKEN_CONTEXT_USED);
			return Response.status(Status.FORBIDDEN).entity("User's information can only be retrieved through a user token").build();
		}else{
			try{

				GCubeUser user = UserManagerWSBuilder.getInstance().getUserManager().getUserByUsername(username);
				toReturn = user.getEmail();
				logger.info("Found email " + toReturn + " for user " +  username);

			}catch(Exception e){

				logger.error("Unable to retrieve attribute for user.", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
			}
		}
		return Response.status(Status.OK).entity(toReturn).build();
	}
}
