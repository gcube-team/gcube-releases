package org.gcube.portal.notifications;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.notifications.cache.NotificationsActionCache;
import org.gcube.portal.notifications.database.connections.CassandraClusterConnection;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;

/**
 * Servlet implementation class NotificationCheckerServlet
 */
@SuppressWarnings("serial")
public class NotificationCheckerServlet extends HttpServlet {

	private static final Log logger = LogFactoryUtil.getLog(NotificationCheckerServlet.class);
	private static final String PORTLET_ID = "gcubenotificationsaction_WAR_GCubeCustomNotifications-portlet";
	private static final String FILE_WRONG_NOTIFICATION_EVENT_NAME = "wrong_notification_events.txt";
	private static final String PATH_FILE = "/home/life/";
	public static final int ALERT_MAX_NEW_NOTIFICATIONS = 20;

	// date formatter
	private SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

	// usermanager
	LiferayUserManager userManager;

	// concurrent hashmap
	private Map<String, Boolean> statusCleanNotifications =  new ConcurrentHashMap<String, Boolean>();

	// notifications action cache
	private NotificationsActionCache notificationActions = NotificationsActionCache.getCacheInstance();

	// cassandra library instance
	private DatabookStore store = CassandraClusterConnection.getConnection();

	@Override
	public void init() {
		userManager = new LiferayUserManager();
		createFile();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(store == null){
			logger.error("Not reachable infrastructure sorry.");
			return;
		}

		// retrieve the user's fullname
		String username = request.getParameter("userid");

		// retrieve actual location
		String location = request.getParameter("location");

		if(username == null || username.isEmpty()){
			logger.debug("userid param not specified, do nothing");
		}else if(location == null || location.isEmpty()){
			logger.debug("location param not specified, do nothing");
		}else{
			try{

				/*
				 * In the case in which the call comes from the notifications/messages page we avoid to push again the notifications.
				 * The notifications/messages portlets will set them all to read. 
				 * However, if the notificationActions cache contains the user's username entry as key, we remove it because
				 * the notifications count must be forced.
				 */
				if(isNotificationMessagesPageRequest(location)){
					notificationActions.removeKey(username);
				}

				// first check if the username is in the NotificationsAction cache 
				if(notificationActions.get(username) != null){
					logger.debug("User's username " + username + " is still in the cache, his notifications won't be refreshed yet");
					return;
				}else
					notificationActions.insert(username, new Date().getTime());

				// checking if the cleaner thread finished
				if(statusCleanNotifications.containsKey(username)){
					if(statusCleanNotifications.get(username) == false){
						logger.debug("There is still a cleaner thread that is removing the notifications of user, returning " + username);
						return;
					}
				}

				logger.debug("Asking notifications for " + username + " to Cassandra");

				GCubeUser userToNotify = userManager.getUserByUsername(username);

				if(userToNotify == null)
					return;

				long longUserId = userToNotify.getUserId();
				logger.debug("The user with screename " + username + " has id " + longUserId);

				// start the thread that cleans the user notifications and wait
				statusCleanNotifications.put(username, false);
				CleanerUserNotificationsThread cleaner = new CleanerUserNotificationsThread(username, longUserId, this);
				cleaner.start();
				cleaner.join();

				/* 
				 * If the current page was the notifications/messages one, just return (because we are sure the notifications
				 * have been put to read)
				 */
				if(isNotificationMessagesPageRequest(location))
					return;

				// get the context
				ServiceContext serviceContext = ServiceContextFactory.getInstance(request);

				// retrieve the newest notifications
				List<Notification> notificationsToPush = store.getUnreadNotificationsByUser(username);

				logger.debug("There are " + notificationsToPush.size() + " new notifications for " + username);
				int stopIndex = notificationsToPush.size() > ALERT_MAX_NEW_NOTIFICATIONS ? ALERT_MAX_NEW_NOTIFICATIONS : notificationsToPush.size();
				logger.debug("Going to push latest " + stopIndex + " notifications to user " + username);

				// push notifications
				for (int i = 0; i < stopIndex; i++) {
					Notification notification = notificationsToPush.get(i);

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

						//						String profileUrl = "";
						//
						//						if(location != null && !location.isEmpty())
						//							profileUrl = PortalContext.getConfiguration().getSiteLandingPagePath(request) + GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
						//
						//						 "<a class=\"link\" href=\""+
						//								profileUrl+"?"+
						//								Base64.encode(GCubeSocialNetworking.USER_PROFILE_OID.getBytes())+"="+
						//								Base64.encode(notification.getSenderid().getBytes())+"\">"+
						//								notification.getSenderFullName()+"</a> " + 

						String notificationBody = notification.getSenderFullName() + " " + htmlRend.toString();

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
						logger.error("Error while pushing notification with id " + notification.getKey() + " to user (this notification will be skipped) " + username);
					}
				}

			}catch(Exception e){
				logger.error("Error while pushing notifications to user " + username);
			}finally{

				// cleaner finished
				cleanerFinished(username);
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
	private static boolean isNotificationMessagesPageRequest(String pageUrl) {
		logger.debug("Url location is " + pageUrl);
		return pageUrl.endsWith(GCubePortalConstants.USER_NOTIFICATION_FRIENDLY_URL) | 
				pageUrl.endsWith(GCubePortalConstants.USER_MESSAGES_FRIENDLY_URL);
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
				logger.debug("Creating file with name " + file.getPath());
				file.createNewFile();
			}else
				logger.debug("File " + FILE_WRONG_NOTIFICATION_EVENT_NAME + " already exists");
		}catch(Exception e){
			logger.error("Unable to create file " + FILE_WRONG_NOTIFICATION_EVENT_NAME, e);
		}

	}

	/**
	 * Write event to file
	 * @param lineToWrite
	 */
	synchronized void writeErrorEvent(String lineToWrite){
		try{
			File file = new File(PATH_FILE + FILE_WRONG_NOTIFICATION_EVENT_NAME);
			if(file.exists()){
				Date dNow = new Date();
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				fw.write(lineToWrite + "[" + ft.format(dNow) + "]\n");
				fw.close();
			}
		}catch(Exception e){
			logger.error("Unable to write line " + lineToWrite, e);
		}
	}

	/**
	 * change the status
	 * @param key
	 */
	public void cleanerFinished(String key){
		if(statusCleanNotifications.containsKey(key)){
			logger.debug("Setting cleaner thread to finished for user " + key);
			statusCleanNotifications.put(key, true);
		}
	}
}