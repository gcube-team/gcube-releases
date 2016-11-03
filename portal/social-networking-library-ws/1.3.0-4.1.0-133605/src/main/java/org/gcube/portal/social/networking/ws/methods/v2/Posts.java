package org.gcube.portal.social.networking.ws.methods.v2;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.social.networking.ws.filters.RequestsAuthFilter;
import org.gcube.portal.social.networking.ws.inputs.PostInputBean;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (feeds).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/posts")
@Api(tags={"/2/posts"}, description="Endpoint for posts resources", protocols="https", authorizations={@Authorization(value=RequestsAuthFilter.AUTH_TOKEN)})
public class Posts {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Posts.class);

	@GET
	@Path("get-posts-user-since/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve user's posts", notes="Retrieve posts of the gcube-token's owner, and allow to filter them by time", 
	response=ResponseBean.class, nickname="get-posts-user-since")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of posts, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getRecentPostsByUserAndDate(
			@QueryParam("time") @Min(value = 0, message="time cannot be negative") 
			@ApiParam(allowableValues="range[0, infinity]", name="time",
			required=true, allowMultiple=false, value="The reference time since when retrieving posts")
			long timeInMillis
			) throws ValidationException{

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		List<Feed> feeds = null;

		try{

			logger.info("Retrieving feeds for user id " + username + " and reference time " + timeInMillis);
			feeds = CassandraConnection.getDatabookStore().getRecentFeedsByUserAndDate(username, timeInMillis);
			Utils.filterFeedsPerContext(feeds, context);
			responseBean.setResult(feeds);
			responseBean.setMessage("");
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Unable to retrieve such feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-posts-user/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve all user's posts", notes="Retrieve all posts of the gcube-token's owner", 
	response=ResponseBean.class, nickname="get-posts-user")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of posts, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getAllPostsByUser() {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		List<Feed> feeds = null;
		try{

			logger.debug("Retrieving feeds for user with id " + username);
			feeds = CassandraConnection.getDatabookStore().getAllFeedsByUser(username);
			Utils.filterFeedsPerContext(feeds, context);
			responseBean.setResult(feeds);
			responseBean.setMessage("");
			responseBean.setSuccess(true);

		}catch(Exception e){


			logger.error("Unable to retrieve such feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-posts-user-quantity/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve a given quantity of latest user's posts", notes="Retrieve a given quantity of latest posts of the gcube-token's owner", 
	response=ResponseBean.class, nickname="get-posts-user-quantity")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of posts, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getQuantityPostsByUser(
			@DefaultValue("10") 
			@QueryParam("quantity") 
			@Min(value=0, message="quantity cannot be negative") 
			@ApiParam(allowableValues="range[0, infinity]", name="quantity",
			required=false, allowMultiple=false, value="How many posts are to be retrieved at most")
			int quantity) throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		List<Feed> feeds = new ArrayList<Feed>();

		// if quantity is zero, just return an empty list
		if(quantity == 0){

			responseBean.setSuccess(true);
			return Response.status(status).entity(responseBean).build();

		}

		try{

			logger.debug("Retrieving last " + quantity + " feeds made by user " + username);
			feeds = CassandraConnection.getDatabookStore().getRecentFeedsByUser(username, quantity);
			Utils.filterFeedsPerContext(feeds, context);
			responseBean.setResult(feeds);
			responseBean.setMessage("");
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Unable to retrieve such feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@POST
	@Path("write-post-user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create a new post", notes="Create a new post having as owner the gcube-token's owner", 
	response=ResponseBean.class, nickname="write-post-user")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfull created, the new post is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response writePostUser(
			@Context HttpServletRequest httpServletRequest,
			@NotNull(message="Post to write is missing") 
			@Valid 
			@ApiParam(name="input",
			required=true, allowMultiple=false, value="The post to be written")
			PostInputBean input) throws ValidationException{

		logger.debug("Request of writing a feed coming from user " + input);
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.CREATED;

		// parse
		String feedText = input.getText();
		String previewTitle = input.getPreviewtitle();
		String previewDescription = input.getPreviewdescription();
		String previewHost = input.getPreviewhost();
		String previewUrl = input.getPreviewurl();
		String httpImageUrl = input.getHttpimageurl();
		boolean enableNotification = input.isEnablenotification();

		// convert enablenotification parameter
		if(enableNotification)
			logger.info("Enable notification for this user post.");
		else
			logger.info("Disable notification for this user post.");


		// try to share
		logger.debug("Trying to share user feed...");
		Feed res = Utils.shareUserUpdate(
				username,
				feedText, 
				context, 
				previewTitle,
				previewDescription,
				previewHost,
				previewUrl,
				httpImageUrl, 
				enableNotification,
				httpServletRequest
				);

		if(res != null){

			logger.debug("Feed correctly written by user " + username);
			responseBean.setResult(res);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
			return Response.status(status).entity(responseBean).build();

		}

		logger.error("Unable to write post.");
		responseBean.setMessage("Unable to write post");
		responseBean.setSuccess(false);
		status = Status.INTERNAL_SERVER_ERROR;
		return Response.status(status).entity(responseBean).build();

	}

	@GET
	@Path("get-posts-app/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve the application's posts", notes="Retrieve the application's posts belonging to the gcube-token's owner (i.e., application)", 
	response=ResponseBean.class, nickname="get-posts-app")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfull retrieved posts, they are reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 403, message = "There is no application profile with such token", response=ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getAllPostsByApp() {

		Caller caller = AuthorizationProvider.instance.get();
		String appId = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		// check if the token actually matches an application
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, context);

		if(appProfile == null){

			logger.error("The given token is not belonging to an application!!!");
			status = Status.FORBIDDEN;
			responseBean.setSuccess(false);
			responseBean.setMessage(ErrorMessages.tokenNotApp);
			return Response.status(status).entity(responseBean).build();

		}

		try{

			logger.debug("Retrieving feeds for app with id " + appId);
			List<Feed> feeds = CassandraConnection.getDatabookStore().getAllFeedsByApp(appId);
			//			Utils.filterFeedsPerContext(feeds, context);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
			responseBean.setResult(feeds);

		}catch(Exception e){

			logger.error("Unable to retrieve such feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@POST
	@Path("write-post-app")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create a new application post", notes="Create a new application post having as owner-application the gcube-token's owner", 
	response=ResponseBean.class, nickname="write-post-app")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfull created, the new post is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 403, message = "There is no application profile with such token", response=ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response writePostApp(
			@Context HttpServletRequest httpServletRequest,
			@NotNull(message="Post to write is null") 
			@Valid 
			@ApiParam(name="input",
			required=true, allowMultiple=false, value="The post to be written")
			PostInputBean input){

		Caller caller = AuthorizationProvider.instance.get();
		String appId = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.CREATED;

		logger.debug("Request of writing a feed coming from an application.");

		// check if the token actually matches an application profile
		ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, context);

		if(appProfile == null){

			logger.error("The given token doesn't belong to an application!!!");
			responseBean.setSuccess(false);
			responseBean.setMessage(ErrorMessages.tokenNotApp);
			status = Status.FORBIDDEN;
			return Response.status(status).entity(responseBean).build();

		}

		// parse
		String feedText = input.getText();
		String previewTitle = input.getPreviewtitle();
		String previewDescription = input.getPreviewdescription();
		String httpImageUrl = input.getHttpimageurl();
		boolean enableNotification = input.isEnablenotification();
		String params = input.getParams();

		// convert enablenotification parameter
		if(enableNotification)
			logger.debug("Enable notification for this application post.");
		else
			logger.debug("Disable notification for this application post.");

		// write feed + notification if it is the case
		Feed written = Utils.shareApplicationUpdate(
				feedText, 
				params, 
				previewTitle, 
				previewDescription, 
				httpImageUrl, 
				appProfile, 
				caller,
				enableNotification,
				httpServletRequest
				);

		if(written != null){

			responseBean.setResult(written);
			responseBean.setSuccess(true);
			return Response.status(status).entity(responseBean).build();
		}

		logger.error("Unable to write post.");
		responseBean.setMessage("Unable to write post");
		responseBean.setSuccess(false);
		status = Status.INTERNAL_SERVER_ERROR;
		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-posts-vre/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve vre's posts", notes="Retrieve all the posts in the context bound to the gcube-token", 
	response=ResponseBean.class, nickname="get-posts-vre")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfull retrieved posts, they are reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getAllPostsByVRE() {

		String scope = ScopeProvider.instance.get();
		logger.debug("Retrieving all posts coming from vre = " + scope);
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		try{

			List<Feed> feeds = CassandraConnection.getDatabookStore().getAllFeedsByVRE(scope);
			responseBean.setResult(feeds);
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Unable to retrieve feeds for vre = " + scope, e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setMessage(e.toString());
			responseBean.setSuccess(false);
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-posts-by-hashtag/")
	@Produces({MediaType.APPLICATION_JSON})
	@ApiOperation(value = "Retrieve posts containing the hashtag", notes="Retrieve posts containing the hashtag in the context bound to the gcube-token", 
	response=ResponseBean.class, nickname="get-posts-by-hashtag")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfull retrieved posts, they are reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getPostsByHashTags(
			@QueryParam("hashtag") 
			@NotNull(message="hashtag cannot be missing") 
			@ApiParam(name="hashtag",
			required=true, allowMultiple=false, value="The hashtag to be searched")
			String hashtag) throws ValidationException {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		logger.info("User " + username + " has requested posts containing hashtag " + hashtag + " in context " + context);

		try{

			DatabookStore datastore = CassandraConnection.getDatabookStore();
			List<Feed> res = datastore.getVREFeedsByHashtag(context, hashtag);
			responseBean.setResult(res);
			responseBean.setSuccess(true);

		}catch(Exception e){
			logger.error("Failed to retrieve hashtags", e);
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-id-liked-posts/")
	@Produces({MediaType.APPLICATION_JSON})
	@ApiOperation(value = "Retrieve ids (UUID) of the liked by the user", notes="Retrieve ids (UUID) of the liked by the user in the context bound to the gcube-token", 
	response=ResponseBean.class, nickname="get-id-liked-posts")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfull retrieved ids, they are reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getAllLikedPostIdsByUser() {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		logger.debug("Retrieving all liked feeds for user with id " + username + " in context " + context);

		List<String> retrievedLikedFeedsIds = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		try{

			DatabookStore datastore = CassandraConnection.getDatabookStore();
			retrievedLikedFeedsIds = datastore.getAllLikedFeedIdsByUser(username);
			Utils.filterFeedsPerContextById(retrievedLikedFeedsIds, context);
			responseBean.setResult(retrievedLikedFeedsIds);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
			logger.debug("Ids of liked feeds by " + username + " retrieved");

		}catch(Exception e){

			logger.error("Unable to read such ids of liked feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-liked-posts/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve posts liked by the user", notes="Retrieve posts liked by the user (up to a given quantity) in the context bound to the gcube-token", 
	response=ResponseBean.class, nickname="get-liked-posts")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfull retrieved posts, they are reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getAllLikedPostsByUser(
			@DefaultValue("10") 
			@QueryParam("limit") 
			@Min(message="limit cannot be negative", value = 0) 
			@ApiParam(name="limit",
			required=false, allowMultiple=false, value="The maximum number of posts to retrieve")
			int limit) throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();


		List<Feed> retrievedLikedFeeds = null;
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		try{

			logger.debug("Retrieving " + limit + " liked feeds for user with id " + username + " in context " + context);
			retrievedLikedFeeds = CassandraConnection.getDatabookStore().getAllLikedFeedsByUser(username, limit);
			Utils.filterFeedsPerContext(retrievedLikedFeeds, context);
			responseBean.setResult(retrievedLikedFeeds);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
			logger.debug("Liked feeds by " + username + " retrieved");

		}catch(Exception e){

			logger.error("Unable to read such liked feeds.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}
}
