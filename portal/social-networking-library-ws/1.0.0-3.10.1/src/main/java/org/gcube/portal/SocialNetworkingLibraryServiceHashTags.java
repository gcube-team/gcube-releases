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
 * REST interface for the social networking library (hash tags).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/hashtags")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceHashTags {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceHashTags.class);

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response NotImplemented() {

		return ErrorMessages.serviceNotYetImplemented(_log);

	}

	// Logger
	/*private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceHashTags.class);

	@GET
	@Path("getVREHashtagsWithOccurrence/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVREHashtagsWithOccurrence(
			@QueryParam("vreid") String vreid, 
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

		_log.info("Retrieving all hashtags for vre with id = " + vreid);

		Map<String, Integer> retrievedHashTags = null;
		try{

			retrievedHashTags = Utils.getDatabookStore().getVREHashtagsWithOccurrence(vreid);

		}catch(Exception e){
			_log.error("Unable to read such hash tags.", e);
			return Response.status(Status.NOT_FOUND).build();
		}


		_log.info("List of hashtags retrieved");
		return Response.status(Status.OK).entity(retrievedHashTags).build();
	}

	@GET
	@Path("getVREFeedsByHashtag/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVREFeedsByHashtag(
			@QueryParam("vreid") String vreid, 
			@QueryParam("hashtag") String hashtag,
			@QueryParam("token") String token) {

		if(vreid == null || hashtag == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving feeds for vre with id = " + vreid + " that contains hashtag " + hashtag);

		List<Feed> retrievedFeeds = null;
		try{

			retrievedFeeds = Utils.getDatabookStore().getVREFeedsByHashtag(vreid, hashtag);

		}catch(Exception e){
			_log.error("Unable to read such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}


		_log.info("List of feeds retrieved");
		return Response.status(Status.OK).entity(retrievedFeeds).build();
	}
	 */

}
