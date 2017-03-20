package org.gcube.portal.notifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.gcube.portal.notifications.database.connections.ConnectionDBLiferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;

/**
 * Notification cleaner thread.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CleanerUserNotificationsThread extends Thread {

	private String username;
	private long longUserId;
	private NotificationCheckerServlet launcher;
	private static final int CLEANER_THREAD_SLEEP_TIME = 2000;
	private static final Log logger = LogFactoryUtil.getLog(CleanerUserNotificationsThread.class);

	CleanerUserNotificationsThread(String key, long longUserId, NotificationCheckerServlet launcher){
		this.username = key;
		this.longUserId = longUserId;
		this.launcher = launcher;
	}

	public void run(){

		logger.debug("Cleaner thread starts running to remove notifications of user " + username);

		try {

			// delete by apy
			deleteNotifications();

			// delete sql
			deleteBySql();

			// Sleep
			Thread.sleep(CLEANER_THREAD_SLEEP_TIME);
			
		}catch (Exception e) {
			logger.error("Error in cleaner thread ", e);
		}

		// cleaner finished, set status
		launcher.cleanerFinished(username);
		logger.debug("Cleaner thread ends for user " + username);

	}

	/**
	 * Delete notifications
	 */
	private void deleteNotifications(){

		try{
			// get all notifications and delete them
			List<UserNotificationEvent> toDelete = UserNotificationEventLocalServiceUtil.getUserNotificationEvents(longUserId);

			for (UserNotificationEvent userNotificationEvent : toDelete) {
				try{
					UserNotificationEventLocalServiceUtil.deleteUserNotificationEvent(userNotificationEvent);
				}catch(Exception e){
					logger.error("Unable to delete notification with key" + userNotificationEvent.getPrimaryKey() + " for user " + username);
				}
			}
		}catch(Exception e){
			logger.error("Unable to retrieve notifications events for user  " + username, e);
		}

	}

	/**
	 * Delete some notifications that liferay wasn't able to remove
	 */
	private void deleteBySql(){

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

				launcher.writeErrorEvent("[ERROR]: " + 
						"there were " + res + " user notifications events that didn't match with existing notifications for user " + username);

			}
		}catch(Exception e){
			logger.error("Exception while deleting notifications events from liferay",e);
		}
	}
}
