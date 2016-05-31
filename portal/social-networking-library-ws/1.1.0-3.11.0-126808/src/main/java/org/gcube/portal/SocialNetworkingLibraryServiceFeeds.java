package org.gcube.portal;


import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.utils.CassandraConnection;
import org.gcube.utils.ErrorMessages;
import org.gcube.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (feeds).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/feeds")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceFeeds {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceFeeds.class);

	@GET
	@Path("getRecentFeedsByUserAndDate/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves the most recent feeds of the token's owner (starting from timeInMillis)
	 * @param timeInMillis
	 * @param token
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getRecentFeedsByUserAndDate(
			@QueryParam("time") long timeInMillis, 
			@QueryParam("token") String token) {

		if(token == null || token.isEmpty() || timeInMillis < 0){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		List<Feed> feeds = null;

		try{

			_log.info("Retrieving feeds for user id " + ae.getUserName() + " and reference time " + timeInMillis);
			feeds = CassandraConnection.getDatabookStore().getRecentFeedsByUserAndDate(ae.getUserName(), timeInMillis);

		}catch(Exception e){

			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@GET
	@Path("getAllFeedsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all feeds belong to token's owner
	 * @param token
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getAllFeedsByUser(
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

		List<Feed> feeds = null;
		try{

			_log.info("Retrieving feeds for user with id " + ae.getUserName());
			feeds = CassandraConnection.getDatabookStore().getAllFeedsByUser(ae.getUserName());

		}catch(Exception e){

			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@GET
	@Path("getRecentFeedsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves the last quantity feeds belonging to token's owner
	 * @param quantity
	 * @param token
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getRecentFeedsByUser(
			@DefaultValue("10") @QueryParam("quantity") int quantity,
			@QueryParam("token") String token) {

		if(token == null || token.isEmpty() || quantity < 0){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}
		
		List<Feed> feeds = null;
		
		// if quantity is zero, just return an empty list
		if(quantity == 0){
			
			return Response.status(Status.OK).entity(feeds).build();
			
		}

		try{

			_log.info("Retrieving last " + quantity + " feeds made by user " + ae.getUserName());
			feeds = CassandraConnection.getDatabookStore().getRecentFeedsByUser(ae.getUserName(), quantity);

		}catch(Exception e){

			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("writePostUser")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Allows a user to write post
	 * @param feedText
	 * @param previewTitle
	 * @param previewDescription
	 * @param previewHost
	 * @param previewUrl
	 * @param httpImageUrl
	 * @param enableNotification
	 * @param token
	 * @return
	 */
	public Response writePostUser(
			@FormParam("text") String feedText, 
			@FormParam("previewtitle")String previewTitle, 
			@FormParam("previewdescription") String previewDescription,
			@FormParam("previewhost") String previewHost,
			@FormParam("previewurl") String previewUrl,
			@FormParam("httpimageurl") String httpImageUrl,
			@DefaultValue("false") @FormParam("enablenotification") String enableNotification,
			@FormParam("token") String token
			){

		_log.info("Request of writing a feed coming from user");

		// at least the feedText is necessary to write a feed (the token is needed anyway)
		if(feedText == null || token == null || token.isEmpty() || feedText.isEmpty()){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();

		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		// convert enablenotification parameter
		boolean notifyGroup = enableNotification.equals("true");
		if(notifyGroup)
			_log.info("Enable notification for this user post.");
		else
			_log.info("Disable notification for this user post.");


		// try to share
		_log.debug("Trying to share user feed...");
		Feed res = Utils.shareUserUpdate(
				ae.getUserName(),
				feedText, 
				ae.getScope(), 
				previewTitle,
				previewDescription,
				previewHost,
				previewUrl,
				httpImageUrl, 
				notifyGroup
				);

		if(res != null){

			_log.info("Feed correctly written by user " + ae.getUserName());
			return Response.status(Status.CREATED).build();

		}

		_log.info("Feed not written by user " + ae.getUserName());
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();

	}

	@GET
	@Path("getAllFeedsByApp/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all application's feeds.
	 * @param token
	 * @return
	 */
	public Response getAllFeedsByApp(
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

		// check if the token actually matches an application
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(ae.getUserName(), ae.getScope());

		if(appProfile == null){

			_log.error("The given token is not belonging to an applcation!!!");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.tokenNotApp).build();

		}

		List<Feed> feeds = null;

		try{

			_log.info("Retrieving feeds for app with id " + ae.getUserName());
			feeds = CassandraConnection.getDatabookStore().getAllFeedsByApp(ae.getUserName());

		}catch(Exception e){

			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("writePostApplication")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Allows an application to write a post.
	 * @param feedText
	 * @param uriParams
	 * @param previewTitle
	 * @param previewDescription
	 * @param httpImageUrl
	 * @param enableNotification
	 * @param token
	 * @return
	 */
	public Response writePostApp(
			@FormParam("text") String feedText, 
			@FormParam("params") String uriParams, 
			@FormParam("previewtitle")String previewTitle, 
			@FormParam("previewdescription") String previewDescription,
			@FormParam("httpimageurl") String httpImageUrl,
			@DefaultValue("false") @FormParam("enablenotification") String enableNotification,
			@FormParam("token") String token
			){

		_log.info("Request of writing a feed coming from an application.");

		// at least the feedText is necessary to write a feed (the token is needed anyway)
		if(feedText == null || token == null || token.isEmpty() || feedText.isEmpty()){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();

		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		// check if the token actually matches an application profile
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(ae.getUserName(), ae.getScope());

		if(appProfile == null){

			_log.error("The given token doesn't belong to an application!!!");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.tokenNotApp).build();

		}

		// convert enablenotification parameter
		boolean notifyGroup = enableNotification.equals("true");
		if(notifyGroup)
			_log.info("Enable notification for this application post.");
		else
			_log.info("Disable notification for this application post.");

		// write feed + notification if it is the case
		Feed written = Utils.shareApplicationUpdate(
				feedText, 
				uriParams, 
				previewTitle, 
				previewDescription, 
				httpImageUrl, 
				appProfile, 
				ae,
				notifyGroup);

		if(written != null){

			return Response.status(Status.CREATED).build();
		}

		_log.info("Feed not written by application " + ae.getUserName());
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	@GET
	@Path("getAllFeedsByVRE/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieve all feeds for this vre
	 * @param token a user token associated with a given vre(scope)
	 * @return
	 */
	public Response getAllFeedsByVRE(
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

		_log.info("Retrieving all feeds coming from vre = " + ae.getScope());

		List<Feed> feeds = null;
		try{

			feeds = CassandraConnection.getDatabookStore().getAllFeedsByVRE(ae.getScope());
			
			Iterator<Feed> it = feeds.iterator();
			
			// remove disabled feeds
			while (it.hasNext()) {
				Feed f = it.next();
				
				if(f.getType() == FeedType.DISABLED)
					it.remove();
				
			}

		}catch(Exception e){

			_log.error("Unable to retrieve feeds for vre = " + ae.getScope(), e);
			return Response.status(Status.NOT_FOUND).build();

		}

		_log.info("List of feeds of vre = " + ae.getScope() + " retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	/*@GET
	@Path("getRecentFeedsByVRE/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecentFeedsByVRE(
			@QueryParam("vreid") String vreid,
			@QueryParam("quantity") int quantity,
			@QueryParam("token") String token) {

		if(vreid == null || token == null || quantity < 0){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving last " + quantity + " feeds coming from vre = " + vreid);

		List<Feed> feeds = null;

		try{

			feeds = Utils.getDatabookStore().getRecentFeedsByVRE(vreid, quantity);

		}catch(Exception e){
			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}*/

	/*@GET
	@Path("getRecentFeedsByVREAndRange/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecentFeedsByVREAndRange(
			@QueryParam("vreid") String vreid,
			@QueryParam("from") int from,
			@QueryParam("quantity") int quantity,
			@QueryParam("token") String token) {

		if(vreid == null || token == null || quantity < 0 || from < 1){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving last " + quantity + " feeds coming from vre = " + vreid + " from " + from);

		RangeFeeds feeds = null;

		try{

			feeds = Utils.getDatabookStore().getRecentFeedsByVREAndRange(vreid, from, quantity);

		}catch(Exception e){
			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}*/

	/*@GET
	@Path("readFeed/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readFeed(
			@QueryParam("feedid") String feedId, 
			@QueryParam("token") String token) {

		Feed retrievedFeed = null;

		if(feedId == null || token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		if(!Utils.validateToken(token)){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving feed with id " + feedId);

		try{

			retrievedFeed = Utils.getDatabookStore().readFeed(feedId);

		}catch(Exception e){
			_log.error("Unable to read such feed.", e);
			return Response.status(Status.NOT_FOUND).build();
		}


		_log.info("Feed retrieved");
		return Response.status(Status.OK).entity(retrievedFeed).build();
	}*/

	/*@GET
	@Path("getAllPortalPrivacyLevelFeeds/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPortalPrivacyLevelFeeds(
			@QueryParam("token") String token) {

		if(token == null){

			_log.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).build();

		}

		_log.info("Retrieving all portal privacy level feeds");

		List<Feed> feeds = null;

		try{

			feeds = Utils.getDatabookStore().getAllPortalPrivacyLevelFeeds();

		}catch(Exception e){
			_log.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		_log.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}*/
}
