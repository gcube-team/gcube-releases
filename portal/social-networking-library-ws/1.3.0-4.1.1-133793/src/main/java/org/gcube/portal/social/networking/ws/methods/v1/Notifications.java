package org.gcube.portal.social.networking.ws.methods.v1;

import java.util.List;

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
import org.gcube.portal.social.networking.ws.utils.CassandraConnection;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (notifications).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/notifications")
public class Notifications {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Notifications.class);

	@GET
	@Path("getRangeNotificationsByUser/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Retrieves notifications from from up to a given quantity for a given user (to whom the token belongs)
	 * @param from
	 * @param quantity
	 * @param token
	 * @return
	 */
	public Response getRangeNotificationsByUser(
			@QueryParam("from") int from,
			@QueryParam("quantity") int quantity) {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();

		logger.info("Retrieving " +  quantity + " notifications of user = " + username + " from " + from);
		
		if(from <= 0 || quantity <= 0){
			
			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.badRequest).build();
			
		}

		List<Notification> notifications = null;

		try{

			notifications = CassandraConnection.getDatabookStore().getRangeNotificationsByUser(username, from, quantity);

		}catch(Exception e){

			logger.error("Unable to retrieve such notifications.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}

		logger.info("List of notifications retrieved");
		return Response.status(Status.OK).entity(notifications).build();
	}

}
