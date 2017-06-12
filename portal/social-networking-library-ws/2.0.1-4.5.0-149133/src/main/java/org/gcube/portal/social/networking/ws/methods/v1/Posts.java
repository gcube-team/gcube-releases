package org.gcube.portal.social.networking.ws.methods.v1;


import java.util.Iterator;
import java.util.List;

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

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (feeds).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/posts")
public class Posts {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Posts.class);

	@GET
	@Path("getRecentPostsByUserAndDate/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves the most recent posts of the token's owner (starting from timeInMillis)
	 * @param timeInMillis
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getRecentPostsByUserAndDate(
			@QueryParam("time") long timeInMillis) {

		if(timeInMillis < 0){
			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.MISSING_PARAMETERS).build();
		}

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		List<Feed> feeds = null;

		try{
			logger.info("Retrieving feeds for user id " + username + " and reference time " + timeInMillis);
			feeds = CassandraConnection.getInstance().getDatabookStore().getRecentFeedsByUserAndDate(username, timeInMillis);
			Utils.filterFeedsPerContext(feeds, context);
		}catch(Exception e){
			logger.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		logger.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@GET
	@Path("getAllPostsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all posts belong to token's owner
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getAllPostsByUser() {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		List<Feed> feeds = null;
		try{
			logger.info("Retrieving feeds for user with id " + username);
			feeds = CassandraConnection.getInstance().getDatabookStore().getAllFeedsByUser(username);
			Utils.filterFeedsPerContext(feeds, context);
		}catch(Exception e){
			logger.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		logger.info("List retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}

	@GET
	@Path("getRecentPostsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves the last quantity posts belonging to token's owner
	 * @param quantity
	 * @param token
	 * @return Response (OK, BAD REQUEST, ...)
	 */
	public Response getRecentPostsByUser(
			@DefaultValue("10") @QueryParam("quantity") int quantity) {

		if(quantity < 0){
			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.MISSING_PARAMETERS).build();
		}

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		List<Feed> feeds = null;

		// if quantity is zero, just return an empty list
		if(quantity == 0){
			return Response.status(Status.OK).entity(feeds).build();
		}

		try{
			logger.info("Retrieving last " + quantity + " feeds made by user " + username);
			feeds = CassandraConnection.getInstance().getDatabookStore().getRecentFeedsByUser(username, -1);
			Utils.filterFeedsPerContext(feeds, context);
			feeds = feeds.subList(0, quantity);
		}catch(Exception e){
			logger.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		logger.info("List retrieved");
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
			@DefaultValue("false") @FormParam("enablenotification") String enableNotification
			){

		logger.info("Request of writing a feed coming from user");

		// at least the feedText is necessary to write a feed (the token is needed anyway)
		if(feedText == null || feedText.isEmpty()){

			logger.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.MISSING_PARAMETERS).build();

		}

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		try{
			// check it is a VRE
			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			long groupId = groupManager.getGroupIdFromInfrastructureScope(context);
			boolean isVRE = groupManager.isVRE(groupId);
			if(!isVRE){
				logger.error("A post cannot be written into a context that is not a VRE");
				return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.POST_OUTSIDE_VRE).build();
			}

			// convert enablenotification parameter
			boolean notifyGroup = enableNotification.equals("true");
			if(notifyGroup)
				logger.info("Enable notification for this user post.");
			else
				logger.info("Disable notification for this user post.");


			// try to share
			logger.debug("Trying to share user feed...");
			Feed res = Utils.shareUserUpdate(
					username,
					feedText, 
					ScopeProvider.instance.get(), 
					previewTitle,
					previewDescription,
					previewHost,
					previewUrl,
					httpImageUrl, 
					notifyGroup
					);

			if(res != null){
				logger.info("Feed correctly written by user " + username);
				return Response.status(Status.CREATED).build();
			}

		}catch(Exception e){
			logger.error("Feed not written by user " + username, e);
		}
		logger.info("Feed not written by user " + username);
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();

	}

	@GET
	@Path("getAllPostsByApp/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves all application's posts.
	 * @param token
	 * @return
	 */
	public Response getAllPostsByApp() {

		Caller caller = AuthorizationProvider.instance.get();
		String appId = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		// check if the token actually matches an application
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, ScopeProvider.instance.get());
		if(appProfile == null){
			logger.error("The given token is not belonging to an application!!!");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.NOT_APP_TOKEN).build();
		}
		List<Feed> feeds = null;
		try{
			logger.info("Retrieving feeds for app with id " + appId);
			feeds = CassandraConnection.getInstance().getDatabookStore().getAllFeedsByApp(appId);
			Utils.filterFeedsPerContext(feeds, context);
		}catch(Exception e){
			logger.error("Unable to retrieve such feeds.", e);
			return Response.status(Status.NOT_FOUND).build();
		}

		logger.info("List retrieved");
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
			@DefaultValue("false") @FormParam("enablenotification") String enableNotification
			){

		logger.info("Request of writing a feed coming from an application.");

		// at least the feedText is necessary to write a feed (the token is needed anyway)
		if(feedText == null || feedText.isEmpty()){
			logger.error("Missing request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.MISSING_PARAMETERS).build();
		}

		Caller caller = AuthorizationProvider.instance.get();
		String appId = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		try{
			// check it is a VRE
			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			long groupId = groupManager.getGroupIdFromInfrastructureScope(context);
			boolean isVRE = groupManager.isVRE(groupId);
			if(!isVRE){
				logger.error("A post cannot be written into a context that is not a VRE");
				return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.POST_OUTSIDE_VRE).build();
			}

			// check if the token actually matches an application profile
			ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, ScopeProvider.instance.get());
			if(appProfile == null){
				logger.error("The given token doesn't belong to an application!!!");
				return Response.status(Status.FORBIDDEN).entity(ErrorMessages.NOT_APP_TOKEN).build();
			}

			// convert enablenotification parameter
			boolean notifyGroup = enableNotification.equals("true");
			if(notifyGroup)
				logger.info("Enable notification for this application post.");
			else
				logger.info("Disable notification for this application post.");

			// write feed + notification if it is the case
			Feed written = Utils.shareApplicationUpdate(
					feedText, 
					uriParams, 
					previewTitle, 
					previewDescription, 
					httpImageUrl, 
					appProfile, 
					caller,
					notifyGroup
					);
			if(written != null){
				return Response.status(Status.CREATED).build();
			}

		}catch(Exception e){
			logger.error("Error while writing a post", e);
		}

		logger.info("Feed not written by application " + appId);
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	@GET
	@Path("getAllPostsByVRE/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieve all posts for this vre
	 * @param token a user token associated with a given vre(scope)
	 * @return
	 */
	public Response getAllPostsByVRE() {

		String scope = ScopeProvider.instance.get();

		logger.info("Retrieving all posts coming from vre = " + scope);

		List<Feed> feeds = null;
		try{
			feeds = CassandraConnection.getInstance().getDatabookStore().getAllFeedsByVRE(scope);
			Iterator<Feed> it = feeds.iterator();

			// remove disabled feeds
			while (it.hasNext()) {
				Feed f = it.next();

				if(f.getType() == FeedType.DISABLED)
					it.remove();

			}
		}catch(Exception e){
			logger.error("Unable to retrieve feeds for vre = " + scope, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		logger.info("List of feeds of vre = " + scope + " retrieved");
		return Response.status(Status.OK).entity(feeds).build();
	}
	
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

		logger.info("Retrieving all liked feeds IDS for user with id " + username);

		List<String> retrievedLikedFeeds = null;
		try{
			retrievedLikedFeeds = CassandraConnection.getInstance().getDatabookStore().getAllLikedFeedIdsByUser(username);
		}catch(Exception e){
			logger.error("Unable to read such ids of liked feeds.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
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

		logger.info("Retrieving " + limit + " liked feeds for user with id " + username);
		
		List<Feed> retrievedLikedFeeds = null;
		try{
			retrievedLikedFeeds = CassandraConnection.getInstance().getDatabookStore().getAllLikedFeedsByUser(username, limit);
		}catch(Exception e){
			logger.error("Unable to read such liked feeds.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();	
		}

		logger.info("Liked feeds by " + username + " retrieved");
		return Response.status(Status.OK).entity(retrievedLikedFeeds).build();
	}
}
