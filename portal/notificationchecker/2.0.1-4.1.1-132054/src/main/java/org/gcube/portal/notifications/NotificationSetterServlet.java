package org.gcube.portal.notifications;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.notifications.database.connections.CassandraClusterConnection;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * Servlet implementation class NotificationSetterServlet
 */
@SuppressWarnings("serial")
public class NotificationSetterServlet  extends HttpServlet {

	private static final Log logger = LogFactoryUtil.getLog(NotificationSetterServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		DatabookStore store = CassandraClusterConnection.getConnection();

		if(store == null){
			logger.error("Not reachable infrastructure sorry.");
			return;
		}

		// retrieve the key of the notifications to set to read 
		String notificationKey = request.getParameter("notificationKey");

		// or get the username, if it is not null, set all his/her notifications to read
		String username = request.getParameter("username");

		if(notificationKey != null && !notificationKey.isEmpty()){
			try {

				logger.debug("Trying to set to read notification with key=" + notificationKey);
				store.setNotificationRead(notificationKey);
				logger.debug("Ok, set to read");

			} catch (Exception e){
				logger.error("Unable to delete notification with key = " + notificationKey, e);
			}
		}
		else if(username != null && !username.isEmpty()){
			try {

				logger.debug("Trying to set to read notifications for user=" + username);
				store.setAllNotificationReadByUser(username);
				logger.debug("Ok, set to read");

			} catch (Exception e){
				logger.error("Unable to delete notification with key = " + notificationKey, e);
			}
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
