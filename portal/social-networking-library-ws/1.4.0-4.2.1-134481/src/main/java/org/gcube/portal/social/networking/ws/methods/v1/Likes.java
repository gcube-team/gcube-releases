package org.gcube.portal.social.networking.ws.methods.v1;

import java.util.List;

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
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (likes).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/likes")
public class Likes {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Likes.class);

	@GET
	@Path("getAllLikedPostIdsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all liked posts ids relates to token's owner
	 * @param token
	 * @return
	 */
	public Response getAllLikedPostIdsByUser() {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();

		logger.info("Retrieving all liked feeds for user with id " + username);

		List<String> retrievedLikedFeeds = null;
		try{

			retrievedLikedFeeds = CassandraConnection.getDatabookStore().getAllLikedFeedIdsByUser(username);

		}catch(Exception e){

			logger.error("Unable to read such ids of liked feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		logger.info("Ids of liked feeds by " + username + " retrieved");
		return Response.status(Status.OK).entity(retrievedLikedFeeds).build();
	}

	@GET
	@Path("getAllLikedPostsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Returns all liked feeds of token's owner
	 * @param limit
	 * @param token
	 * @return
	 */
	public Response getAllLikedPostsByUser(@DefaultValue("10") @QueryParam("limit") int limit) {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();

		List<Feed> retrievedLikedFeeds = null;
		try{

			logger.info("Retrieving " + limit + " liked feeds for user with id " + username);
			retrievedLikedFeeds = CassandraConnection.getDatabookStore().getAllLikedFeedsByUser(username, limit);

		}catch(Exception e){
			logger.error("Unable to read such liked feeds.", e);
			return Response.status(Status.NOT_FOUND).build();	
		}
		
		logger.info("Liked feeds by " + username + " retrieved");
		return Response.status(Status.OK).entity(retrievedLikedFeeds).build();
	}

}
