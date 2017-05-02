package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ElasticSearchConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (post and its comments).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/full-text-search")
@Api(tags={"full-text-search"}, protocols="https", authorizations={@Authorization(value="gcube-token")})
public class FullTextSearch {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FullTextSearch.class);

	@GET
	@Path("search-by-query")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve posts/comments that match the given query", 
	notes="The posts/comments returned belong to the context bound to the gcube-token", 
	response=ResponseBean.class, nickname="search-by-query")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of posts/comments that match the query, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.ERROR_IN_API_RESULT, response=ResponseBean.class)})
	public Response searchByQuery(
			@Context HttpServletRequest httpServletRequest,
			@QueryParam("query") @NotNull(message="query cannot be null") @Size(min=1, message="query cannot be empty") 
			@ApiParam(required=true, defaultValue="none", name="query", value="A string to search for") 
			String query,

			@DefaultValue("0") @QueryParam("from") @Min(value=0, message="from cannot be negative") 
			@ApiParam(allowableValues="[0, infinity)", 
			required=false, name="from", value="the index of the base result to be returned") 
			int from, 

			@DefaultValue("10") @QueryParam("quantity") @Min(value=0, message="quantity cannot be negative") 
			@ApiParam(allowableValues="[1, infinity)", 
			required=false, name="quantity", value="defines how many results are most are to be returned") 
			int quantity
			) throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;
		
		GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
		UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();

		try{
			// Retrieve user's vres in which we must search
			Set<String> vres = new HashSet<String>();
			
			// get the group id from the current context
			long currentGroupId = groupManager.getGroupIdFromInfrastructureScope(context);
			GCubeUser currUser = userManager.getUserByUsername(username);
			List<GCubeGroup> userContexts = groupManager.listGroupsByUser(currUser.getUserId());

			if (groupManager.isRootVO(currentGroupId)) {
				for (GCubeGroup group : groupManager.listGroupsByUser(currUser.getUserId())) {		
					if (groupManager.isVRE(group.getGroupId()) && userContexts.contains(group)) {
						vres.add(groupManager.getInfrastructureScope(group.getGroupId()));
					}
				}				
			}
			else if(groupManager.isVO(currentGroupId)){
				for (GCubeGroup group : groupManager.listGroupsByUser(currUser.getUserId())) {		
					if (groupManager.isVRE(group.getGroupId()) && group.getParentGroupId() == currentGroupId && userContexts.contains(group)) {
						vres.add(groupManager.getInfrastructureScope(group.getGroupId()));
					}
				}
			}
			else {
				vres.add(context);
			}			

			// query elastic search
			List<EnhancedFeed> enhancedFeeds = ElasticSearchConnection.getSingleton().getElasticSearchClient().search(query, vres, from, quantity);
			DatabookStore store = CassandraConnection.getInstance().getDatabookStore();

			// retrieve the ids of liked feeds by the user
			List<String> likedFeeds = store.getAllLikedFeedIdsByUser(username);

			// update fields "liked" and "isuser"
			for (EnhancedFeed enhancedFeed : enhancedFeeds) {
				if(isUsers(enhancedFeed.getFeed(), username))
					enhancedFeed.setUsers(true);
				if(likedFeeds.contains(enhancedFeed.getFeed().getKey()))
					enhancedFeed.setLiked(true);
			}
			responseBean.setResult((ArrayList<EnhancedFeed>) enhancedFeeds);
			responseBean.setSuccess(true);
		}catch(Exception e){
			logger.error("Something went wrong while searching", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}
		return Response.status(status).entity(responseBean).build();
	}

	/**
	 * tell if a feed belongs to the current user or not
	 * @param tocheck
	 * @param username
	 * @return true if this feed is of the current user
	 */
	private static final boolean isUsers(Feed tocheck, String username) {
		return (tocheck.getEntityId().equals(username));
	}

}
