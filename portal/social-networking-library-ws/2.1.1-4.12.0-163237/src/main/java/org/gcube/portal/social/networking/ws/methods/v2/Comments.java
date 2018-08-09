package org.gcube.portal.social.networking.ws.methods.v2;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.List;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.social.networking.swagger.config.Bootstrap;
import org.gcube.portal.social.networking.swagger.config.SwaggerConstants;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Filters;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (comments).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/comments")
@Api(value=SwaggerConstants.COMMENTS, authorizations={@Authorization(value = Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF), @Authorization(value = Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF)})
/**
 * Resource endpoint for Comment (version 2)
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class Comments {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Comments.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get-comments-user")
	@ApiOperation(value = "Retrieve user's comments", response=ResponseBean.class, nickname="get-comments-user", notes="Retrieve the list of comments belonging to the owner of the token in the related context.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The list of comments is put into the 'result' field", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getCommentsUser() {

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		List<Comment> comments = null;

		try{
			logger.info("Retrieving comments for user id " + username);
			comments = CassandraConnection.getInstance().getDatabookStore().getRecentCommentsByUserAndDate(username, 0);
			Filters.filterCommentsPerContext(comments, context);
			responseBean.setResult(comments);
			responseBean.setSuccess(true);
		}catch(Exception e){
			logger.error("Unable to retrieve such comments.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get-comments-user-by-time")
	@ApiOperation(value = "Retrieve user's comments and filter by date", notes="Retrieve comments of the gcube-token's owner in the context bound to the token itself and filter them by date", 
	response=ResponseBean.class, nickname="get-comments-user-by-time")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The list of comments is put into the 'result' field", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getCommentsUserByTime(
			@QueryParam("time") 
			@Min(value = 0, message="time cannot be negative") 
			@ApiParam(
					allowableValues="range[0, infinity]", 
					required=true, 
					name="time", 
					value="the base time for filtering operation") 
			long timeInMillis
			) throws ValidationException{

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		List<Comment> comments = null;

		try{
			logger.info("Retrieving comments for user id " + username);
			comments = CassandraConnection.getInstance().getDatabookStore().getRecentCommentsByUserAndDate(username, timeInMillis);
			Filters.filterCommentsPerContext(comments, context);
			responseBean.setResult(comments);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
		}catch(Exception e){
			logger.error("Unable to retrieve such comments.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}
}
