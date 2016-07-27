package org.gcube.portal;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.utils.CassandraConnection;
import org.gcube.utils.ErrorMessages;
import org.gcube.utils.Utils;
import org.slf4j.LoggerFactory;

/**
 * REST interface for the social networking library (notifications).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("/notifications")
@Singleton // please note that this is a singleton class!
public class SocialNetworkingLibraryServiceNotifications {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(SocialNetworkingLibraryServiceNotifications.class);

//	@GET
//	@Path("readNotification/")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response readNotification(
//			@QueryParam("notificationid") String notificationid, 
//			@QueryParam("token") String token) {
//
//		if(notificationid == null || token == null){
//
//			_log.error("Missing request parameters");
//			return Response.status(Status.BAD_REQUEST).build();
//			
//		}
//
//		// token validation
//		if(!Utils.validateToken(token)){
//
//			_log.error("Token is not valid");
//			return Response.status(Status.FORBIDDEN).build();
//
//		}
//
//		Notification notification = null;
//		try{
//			
//			_log.info("Retrieving notification with id " + notificationid);
//			notification = Utils.getDatabookStore().readNotification(notificationid);
//			
//
//		}catch(Exception e){
//		
//			_log.error("Unable to read such notification.", e);
//			return Response.status(Status.NOT_FOUND).build();
//			
//		}
//
//		_log.info("Notification retrieved");
//		return Response.status(Status.OK).entity(notification).build();
//	}
//
//	@GET
//	@Path("getAllNotificationByUser/")
//	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
//	/**
//	 * Retrieves all notifications belonging to token's owner.
//	 * @param quantity
//	 * @param token
//	 * @return
//	 */
//	public Response getAllNotificationByUser(
//			@QueryParam("quantity") int quantity,
//			@QueryParam("token") String token) {
//
//		if(token == null || token.isEmpty() || quantity < 0){
//
//			_log.error("Missing/wrong request parameters");
//			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
//		}
//
//		// token validation
//		AuthorizationEntry ae = Utils.validateToken(token);
//		if(ae == null){
//
//			_log.error("Token is not valid");
//			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();
//
//		}
//
//		_log.info("Retrieving last " + quantity + " notifications of user = " + ae.getUserName());
//
//		List<Notification> notifications = null;
//
//		try{
//
//			notifications = CassandraConnection.getDatabookStore().getAllNotificationByUser(ae.getUserName(), quantity);
//
//		}catch(Exception e){
//			
//			_log.error("Unable to retrieve such notifications.", e);
//			return Response.status(Status.NOT_FOUND).build();
//			
//		}
//
//		_log.info("List of notifications retrieved");
//		return Response.status(Status.OK).entity(notifications).build();
//	}
//
//	@GET
//	@Path("getUnreadNotificationsByUser/")
//	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
//	/**
//	 * Retrieves all unread notifications related to token's owner.
//	 * @param token
//	 * @return
//	 */
//	public Response getUnreadNotificationsByUser(
//			@QueryParam("token") String token) {
//
//		if(token == null || token.isEmpty()){
//
//			_log.error("Missing/wrong request parameters");
//			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
//		}
//
//		// token validation
//		AuthorizationEntry ae = Utils.validateToken(token);
//		if(ae == null){
//
//			_log.error("Token is not valid");
//			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();
//
//		}
//
//		_log.info("Retrieving last unread notifications of user = " + ae.getUserName());
//
//		List<Notification> notifications = null;
//
//		try{
//
//			notifications = CassandraConnection.getDatabookStore().getUnreadNotificationsByUser(ae.getUserName());
//
//		}catch(Exception e){
//			
//			_log.error("Unable to retrieve such notifications.", e);
//			return Response.status(Status.NOT_FOUND).build();
//			
//		}
//
//		_log.info("List of notifications retrieved");
//		return Response.status(Status.OK).entity(notifications).build();
//	}

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
			@QueryParam("quantity") int quantity,
			@QueryParam("token") String token) {

		if(token == null || token.isEmpty() || from <= 0 || quantity < 0){

			_log.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();
		}

		// token validation
		AuthorizationEntry ae = Utils.validateToken(token);
		if(ae == null){

			_log.error("Token is not valid");
			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();

		}

		_log.info("Retrieving " +  quantity + " notifications of user = " + ae.getUserName() + " from " + from);

		List<Notification> notifications = null;

		try{

			notifications = CassandraConnection.getDatabookStore().getRangeNotificationsByUser(ae.getUserName(), from, quantity);

		}catch(Exception e){
			
			_log.error("Unable to retrieve such notifications.", e);
			return Response.status(Status.NOT_FOUND).build();
			
		}

		_log.info("List of notifications retrieved");
		return Response.status(Status.OK).entity(notifications).build();
	}

//	@GET
//	@Path("getUnreadNotificationMessagesByUser/")
//	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
//	/**
//	 * Retrieves all unread notification related to token's owner.
//	 * @param token
//	 * @return
//	 */
//	public Response getUnreadNotificationMessagesByUser(
//			@QueryParam("token") String token) {
//
//		if(token == null || token.isEmpty()){
//
//			_log.error("Missing request parameters");
//			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingToken).build();
//		}
//
//		// token validation
//		AuthorizationEntry ae = Utils.validateToken(token);
//		if(ae == null){
//
//			_log.error("Token is not valid");
//			return Response.status(Status.FORBIDDEN).entity(ErrorMessages.invalidToken).build();
//
//		}
//
//		_log.info("Retrieving unread notification messages by user " + ae.getUserName());
//
//		List<Notification> notifications = null;
//
//		try{
//
//			notifications = CassandraConnection.getDatabookStore().getUnreadNotificationMessagesByUser(ae.getUserName());
//
//		}catch(Exception e){
//			
//			_log.error("Unable to retrieve such notifications.", e);
//			return Response.status(Status.NOT_FOUND).build();
//			
//		}
//
//		_log.info("List of notifications retrieved");
//		return Response.status(Status.OK).entity(notifications).build();
//	}
}
