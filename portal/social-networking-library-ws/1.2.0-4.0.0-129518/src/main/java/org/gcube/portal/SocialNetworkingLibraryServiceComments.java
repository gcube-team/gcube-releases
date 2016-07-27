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
 * REST interface for the social networking library (comments).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/comments")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceComments {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceComments.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response NotImplemented() {

		return ErrorMessages.serviceNotYetImplemented(_log);
	}

	// Logger
	/*private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceComments.class);

	@GET
	@Path("getAllCommentByFeed/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCommentByFeed(
			@QueryParam("feedid") String feedId, 
			@QueryParam("token") String token) {

		if(feedId == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		List<Comment> retrievedComments = null;
		try{

			_log.info("Retrieving comments for feed with id " + feedId);
			retrievedComments = Utils.getDatabookStore().getAllCommentByFeed(feedId);

		}catch(Exception e){

			_log.error("Unable to read such comments.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("Comments retrieved");
		return Response.status(Status.OK).entity(retrievedComments).build();
	}*/
}
