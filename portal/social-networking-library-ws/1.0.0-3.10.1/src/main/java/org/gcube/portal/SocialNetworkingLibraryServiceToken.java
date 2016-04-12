package org.gcube.portal;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.utils.ErrorMessages;
import org.gcube.utils.Utils;
import org.slf4j.LoggerFactory;


/**
 * REST interface for the social networking library (tokens).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/tokens")
@Singleton
public class SocialNetworkingLibraryServiceToken {

	private final static String DEFAULT_ROLE = "OrganizationMember";

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceToken.class);

	@POST
	@Path("generateApplicationToken/")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Allows the owner of the token token to generate a new token for the couple (appId, vre) 
	 * where vre is the same associated to the original user token.
	 * @param appId
	 * @param token
	 * @return
	 */
	public Response generateToken(
			@FormParam("appid") String appId,
			@FormParam("token") String token){

		_log.info("Incoming request for app token generation. Validating parameter/token of the requester... ");

		if(appId == null || token == null || token.isEmpty() || appId.isEmpty()){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();

		}

		// check user token
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}
		
		//user scope
		String userScope = ae.getScope();
		
		// check if an application profile exists for this appId/scope
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, userScope);

		if(appProfile == null){

			_log.error("There is no application profile for this application id and scope!");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.noApplicationProfileAvailable).build();

		}

		_log.info("Generating token for the application with id " + appId);

		// Set this scope on the current thread according to user's scope
		ScopeProvider.instance.set(userScope);

		List<String> roles = new ArrayList<>();
		roles.add(DEFAULT_ROLE);

		String appToken = null;
		try {

			// each token is related to an identifier and the context
			appToken = authorizationService().build().generate(appId, roles);

		} catch (Exception e) {

			_log.error("Unable to generate token for app " + appId + " and scope " + userScope);
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.tokenGenerationFailed).build();

		}

		return Response.status(Status.CREATED).entity(appToken).build();
	}

}
