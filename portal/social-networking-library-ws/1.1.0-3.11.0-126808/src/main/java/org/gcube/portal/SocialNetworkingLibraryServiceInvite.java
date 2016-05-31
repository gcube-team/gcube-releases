package org.gcube.portal;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.utils.ErrorMessages;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (invites).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/invites")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceInvite {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceInvite.class);

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response NotImplemented() {

		return ErrorMessages.serviceNotYetImplemented(_log);

	}

	// Logger
	/*@SuppressWarnings("unused")
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceInvite.class);

	@GET
	@Path("readInvite/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readInvite(
			@QueryParam("inviteid") String inviteid, 
			@QueryParam("token") String token) {

		if(inviteid == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		Invite invite = null;
		try{

			_log.info("Retrieving invite with id = " + inviteid);
			invite = Utils.getDatabookStore().readInvite(inviteid);

		}catch(Exception e){

			_log.error("Unable to read such invite.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("Invite retrieved");
		return Response.status(Status.OK).entity(invite).build();
	}*/

	/*@GET
	@Path("isExistingInvite/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isExistingInvite(
			@QueryParam("vreid") String vreid, 
			@QueryParam("email") String email, 
			@QueryParam("token") String token) {

		if(vreid == null || email == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		String exists = null;
		try{

			_log.info("Checking if invite in vre with id = " + vreid + " for email " + email + " exists");
			exists = Utils.getDatabookStore().isExistingInvite(vreid, email);

		}catch(Exception e){

			_log.error("Unable to check if such invite exists .", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("Invite information retrieved correctly");
		return Response.status(Status.OK).entity(exists).build();
	}

	@GET
	@Path("getInvitedEmailsByVRE/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInvitedEmailsByVRE(
			@QueryParam("vreid") String vreid,
			@QueryParam("invitestatus") List<InviteStatus> status,
			@QueryParam("token") String token) {

		if(vreid == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();

		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		List<Invite> invites = null;
		try{

			_log.info("Getting invites from vre and status.");
			invites = Utils.getDatabookStore().getInvitedEmailsByVRE(vreid, status.toArray(new InviteStatus[status.size()]));

		}catch(Exception e){

			_log.error("Unable to retrieve such invites.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("Invites retrieved correctly");
		return Response.status(Status.OK).entity(invites).build();
	}*/

}
