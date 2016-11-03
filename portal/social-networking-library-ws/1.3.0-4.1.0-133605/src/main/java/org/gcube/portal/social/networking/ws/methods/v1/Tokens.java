package org.gcube.portal.social.networking.ws.methods.v1;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.slf4j.LoggerFactory;


/**
 * REST interface for the social networking library (tokens).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/tokens")
public class Tokens {

	private final static String DEFAULT_ROLE = "OrganizationMember";

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Tokens.class);

	@POST
	@Path("generateApplicationToken/")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Allows the owner of the token token to generate a new token for the couple (appId, vre) 
	 * where vre is the same associated to the original user token.
	 * @param appId
	 * @param token
	 * @return
	 */
	public Response generateApplicationToken(@FormParam("appid") String appId){

		logger.info("Incoming request for app token generation.");
		
		//user scope
		String userScope = ScopeProvider.instance.get();
		
		// check if an application profile exists for this appId/scope (the discovery of the service will be made on the root)
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, userScope);

		if(appProfile == null){

			logger.error("There is no application profile for this application id and scope!");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.noApplicationProfileAvailable).build();

		}

		logger.info("Generating token for the application with id " + appId);

		List<String> roles = new ArrayList<>();
		roles.add(DEFAULT_ROLE);

		String appToken = null;
		try {

			// each token is related to an identifier and the context
			appToken = authorizationService().generateUserToken(new UserInfo(appId, new ArrayList<String>()), userScope);

		} catch (Exception e) {

			logger.error("Unable to generate token for app " + appId + " and scope " + userScope);
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.tokenGenerationFailed).build();

		}

		return Response.status(Status.CREATED).entity(appToken).build();
	}

}
