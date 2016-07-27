package org.gcube.portal;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.utils.CassandraConnection;
import org.gcube.utils.ErrorMessages;
import org.gcube.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (likes).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/likes")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceLikes {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceLikes.class);

	@GET
	@Path("getAllLikedFeedIdsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all liked feeds ids relates to token's owner
	 * @param token
	 * @return
	 */
	public Response getAllLikedFeedIdsByUser(
			@QueryParam("token") String token) {

		if(token == null || token.isEmpty()){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingToken).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		_log.info("Retrieving all liked feeds for user with id " + ae.getUserName());

		List<String> retrievedLikedFeeds = null;
		try{

			retrievedLikedFeeds = CassandraConnection.getDatabookStore().getAllLikedFeedIdsByUser(ae.getUserName());

		}catch(Exception e){

			_log.error("Unable to read such ids of liked feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("Ids of liked feeds by " + ae.getUserName() + " retrieved");
		return Response.status(Status.OK).entity(retrievedLikedFeeds).build();
	}

	@GET
	@Path("getAllLikedFeedsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Returns all liked feeds of token's owner
	 * @param limit
	 * @param token
	 * @return
	 */
	public Response getAllLikedFeedsByUser(
			@DefaultValue("10") @QueryParam("limit") int limit, 
			@QueryParam("token") String token) {

		if(token == null || token.isEmpty() || limit < 0){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingToken).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		List<Feed> retrievedLikedFeeds = null;
		try{

			_log.info("Retrieving " + limit + " liked feeds for user with id " + ae.getUserName());
			retrievedLikedFeeds = CassandraConnection.getDatabookStore().getAllLikedFeedsByUser(ae.getUserName(), limit);

		}catch(Exception e){
			
			_log.error("Unable to read such liked feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
			
		}


		_log.info("Liked feeds by " + ae.getUserName() + " retrieved");
		return Response.status(Status.OK).entity(retrievedLikedFeeds).build();
	}

	/*@GET
	@Path("getAllLikesByFeed/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllLikesByFeed(
			@QueryParam("feedid") String feedid, 
			@QueryParam("token") String token) {

		if(feedid == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving all likes for feed with id" + feedid);

		List<Like> retrievedLikes = null;
		try{

			retrievedLikes = Utils.getDatabookStore().getAllLikesByFeed(feedid);

		}catch(Exception e){
			_log.error("Unable to read such likes.", e);
			return Response.status(Status.NOT_FOUND).build();
		}


		_log.info("Likes retrieved");
		return Response.status(Status.OK).entity(retrievedLikes).build();
	}*/

}
