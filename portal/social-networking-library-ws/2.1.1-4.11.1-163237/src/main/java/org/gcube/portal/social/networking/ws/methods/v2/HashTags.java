package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.swagger.config.Bootstrap;
import org.gcube.portal.social.networking.swagger.config.SwaggerConstants;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (hash tags).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/hashtags")
@Api(value=SwaggerConstants.HASHTAGS, authorizations={@Authorization(value = Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF), @Authorization(value = Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF)})
public class HashTags {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HashTags.class);

	@GET
	@Path("get-hashtags-and-occurrences/")
	@Produces({MediaType.APPLICATION_JSON})
	@ApiOperation(value = "Retrieve hashtags", nickname="get-hashtags-and-occurrences",
	notes="Retrieve hashtags in the context bound to the gcube-token", response=ResponseBean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Hashtags and occurrences retrieved, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response getHashTagsAndOccurrences(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		logger.info("User " + username + " has requested hashtags of context " + context);

		try{
			DatabookStore datastore = CassandraConnection.getInstance().getDatabookStore();
			// TODO handle the case of VO and ROOT
			boolean isVRE = GroupManagerWSBuilder.getInstance().getGroupManager().isVRE(GroupManagerWSBuilder.getInstance().getGroupManager().getGroupIdFromInfrastructureScope(context));
			if(isVRE){
				Map<String, Integer> map = datastore.getVREHashtagsWithOccurrence(context);
				responseBean.setResult(map);
				responseBean.setSuccess(true);
			}else{
				responseBean.setMessage("Please provide a VRE token. VO and ROOT VO cases are not yet managed.");
				responseBean.setResult(false);
			}
		}catch(Exception e){
			logger.error("Failed to retrieve hashtags", e);
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity(responseBean).build();
	}

}
