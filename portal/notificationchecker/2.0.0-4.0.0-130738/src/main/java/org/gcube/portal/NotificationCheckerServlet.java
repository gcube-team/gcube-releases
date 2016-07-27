package org.gcube.portal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;

/**
 * Servlet implementation class NotificationCheckerServlet
 */

@SuppressWarnings("serial")
public class NotificationCheckerServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(NotificationCheckerServlet.class);
	private static final String PORTLET_ID = "gcubenotificationsaction_WAR_GCubeCustomNotifications-portlet";
	private static final String FILE_WRONG_NOTIFICATION_EVENT_NAME = "wrong_notification_events.txt";
	private static final String PATH_FILE = "/home/life/";

	// date formatter
	private SimpleDateFormat ft = 
			new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

	// usermanager
	LiferayUserManager userManager;

	@Override
	public void init() {
		userManager = new LiferayUserManager();
		createFile();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		DatabookStore store = CassandraClusterConnection.getConnection();

		if(store == null){
			logger.error("Not reachable infrastructure sorry.");
			return;
		}

		// retrieve the user's fullname
		String username = request.getParameter("userid");

		// retrieve actual location
		String location = request.getParameter("location");

		// in the case in which the call comes from the notifications page we avoid to push again the notifications.
		// The notifications portlet will set them all to read.
		if(isNotificationPageRequest(location))
			return;

		if (username == null || username.isEmpty()) {

			logger.debug("userid param not specified, do nothing");

		} else {
			try{

				logger.debug("Asking notifications for " + username + " to Cassandra");

				GCubeUser userToNotify = userManager.getUserByUsername(username);

				if(userToNotify == null)
					return;

				long longUserId = userToNotify.getUserId();
				logger.debug("Found user with screename " + username + ", his id is " + longUserId);

				// get all notifications and delete them
				List<UserNotificationEvent> toDelete = UserNotificationEventLocalServiceUtil.getUserNotificationEvents(longUserId);

				for (UserNotificationEvent userNotificationEvent : toDelete) {
					UserNotificationEvent res = null;
					try{
						res = UserNotificationEventLocalServiceUtil.deleteUserNotificationEvent(userNotificationEvent);
					}catch(Exception e){
						logger.error("Unable to delete notification " + res + " for user " + username);
					}
				}

				// do the delete query into the postgres db in order to avoid no UserEvent exception
				Connection connection = null;
				try{
					connection = ConnectionDBLiferay.getConnection();
					String delete = "DELETE FROM \"notifications_usernotificationevent\" where \"userid\" = ?;";
					PreparedStatement statement = connection.prepareStatement(delete);
					statement.setLong(1, longUserId);
					int res = statement.executeUpdate();
					logger.debug("Delete " + res + " rows after execution of query " + statement.toString());

					if(res > 0){

						writeErrorEvent("[ERROR]: " + 
								"there were " + res + " user notifications events that didn't match with existing notifications for user " + username);

					}
				}catch(Exception e){
					logger.error("Exception while deleting notifications events from liferay",e);
				}

				// get the context
				ServiceContext serviceContext = ServiceContextFactory
						.getInstance(request);

				// retrieve the newest notifications
				List<Notification> notificationsToPush = store.getUnreadNotificationsByUser(username);

				logger.debug("There are " + notificationsToPush.size() + " new notifications for " + username);

				// push notifications
				for (Notification notification : notificationsToPush) {

					try{

						// if it is an application notification we avoid to push it
						if(notification.getType().equals(NotificationType.POST_ALERT)){

							Feed sourceFeed = store.readFeed(notification.getSubjectid());
							if(sourceFeed.isApplicationFeed())
								continue;

						}

						// remove html tags from the description and construct the body of the notification
						String notificationToShow = notification.getDescription();
						Source htmlSource = new Source(notificationToShow);
						Segment htmlSeg = new Segment(htmlSource, 0, htmlSource.length());
						Renderer htmlRend = new Renderer(htmlSeg);

						String profileUrl = "";

						if(location != null && !location.isEmpty())
							profileUrl = PortalContext.getConfiguration().getSiteLandingPagePath(request) + GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;

						String notificationBody = "<a class=\"link\" href=\""+
								profileUrl+"?"+
								Base64.encode(GCubeSocialNetworking.USER_PROFILE_OID.getBytes())+"="+
								Base64.encode(notification.getSenderid().getBytes())+"\">"+
								notification.getSenderFullName()+"</a> " + htmlRend.toString();

						//shorten the notification text if greather  than 200 chars
						notificationBody = notificationBody.length() > 250 ? notificationBody.substring(0, 250) + " ..." : notificationBody;	

						// get sender
						long senderUserId = userManager.getUserId(notification.getSenderid());

						// construct json object to send as notification
						JSONObject payloadJSON = JSONFactoryUtil.createJSONObject();
						payloadJSON.put("userId", senderUserId); // NOTE: the sender must be sent in this way!
						payloadJSON.put("notificationUrl", notification.getUri()); // could be used to redirect the user
						payloadJSON.put("notificationBody", notificationBody); // the actual notification to show
						payloadJSON.put("notificationId", notification.getKey()); // the notification key

						// add the notification to the user's notification table
						UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
								longUserId,
								PORTLET_ID,
								notification.getTime().getTime(), 
								longUserId,
								payloadJSON.toString(), 
								false, 
								serviceContext);

					}catch(Exception e){
						logger.error("Error while pushing notification to user (this notification will be skipped) " + username, e);
					}
				}

			}catch(Exception e){
				logger.error("Error while pushing notifications to user " + username, e);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Check if the caller page is the notifications one.
	 * @param pageUrl
	 * return true on success, false otherwise
	 */
	private static boolean isNotificationPageRequest(String pageUrl) {
		logger.debug("Url location is " + pageUrl);
		return pageUrl.endsWith(GCubePortalConstants.USER_NOTIFICATION_FRIENDLY_URL);
	}

	/**
	 * Check if the file exists and if it is not, create it
	 * @param fileWrongNotificationEventName
	 */
	private void createFile() {

		File file = new File(PATH_FILE + FILE_WRONG_NOTIFICATION_EVENT_NAME);

		// if the file doesn't exists create it
		try{
			if (!file.exists()) {
				logger.info("Creating file with name " + file.getPath());
				file.createNewFile();
			}else
				logger.info("File " + FILE_WRONG_NOTIFICATION_EVENT_NAME + " already exists");
		}catch(Exception e){
			logger.error("Unable to create file " + FILE_WRONG_NOTIFICATION_EVENT_NAME, e);
		}

	}

	/**
	 * Write event to file
	 * @param lineToWrite
	 */
	private synchronized void writeErrorEvent(String lineToWrite){

		try{
			File file = new File(PATH_FILE + FILE_WRONG_NOTIFICATION_EVENT_NAME);
			if(file.exists()){
				Date dNow = new Date( );
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				fw.write(lineToWrite + "[" + ft.format(dNow) + "]\n");
				fw.close();
			}
		}catch(Exception e){
			logger.error("Unable to write line " + lineToWrite, e);
		}
	}
}