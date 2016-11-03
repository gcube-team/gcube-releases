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
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.social.networking.ws.filters.RequestsAuthFilter;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (notifications).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/notifications")
@Api(tags={"/2/notifications"}, description="Endpoint for notifications resources", protocols="https", authorizations={@Authorization(value=RequestsAuthFilter.AUTH_TOKEN)})
public class Notifications {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Notifications.class);

	@GET
	@Path("get-range-notifications/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve user's notifications", notes="Retrieve notifications of the gcube-token's owner", 
	response=ResponseBean.class, nickname="get-range-notifications")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Notifications retrieved and reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.errorMessageApiResult, response=ResponseBean.class)})
	public Response getRangeNotifications(
			@DefaultValue("1") @QueryParam("from") @Min(value=1, message="from must be greater or equal to 1") 
			@ApiParam(allowableValues="range[0, infinity]", 
			required=false, allowMultiple=false, value="The base index notification")
			int from,

			@DefaultValue("10") @QueryParam("quantity") @Min(value=0, message="quantity must be greater or equal to 0") 
			@ApiParam(allowableValues="range[1, infinity]", 
			required=false, allowMultiple=false, value="Retrieve notifications up to this quantity")
			int quantity
			) throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();

		logger.debug("Retrieving " +  quantity + " notifications of user = " + username + " from " + from);

		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		List<Notification> notifications = null;
		try{

			notifications = CassandraConnection.getDatabookStore().getRangeNotificationsByUser(username, from, quantity);
			responseBean.setResult(notifications);
			responseBean.setMessage("");
			responseBean.setSuccess(true);
			logger.debug("List of notifications retrieved");

		}catch(Exception e){

			logger.error("Unable to retrieve such notifications.", e);
			responseBean.setMessage(e.getMessage());
			responseBean.setSuccess(false);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

}
