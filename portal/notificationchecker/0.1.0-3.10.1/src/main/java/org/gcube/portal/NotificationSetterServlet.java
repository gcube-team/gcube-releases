package org.gcube.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Notification;

@SuppressWarnings("serial")
public class NotificationSetterServlet extends HttpServlet {
	protected static final String STOP_GETNOTIF_ONLOAD = "STOP_GETNOTIF_ONLOAD";
	
	protected static final String STOP_GETMESSAGES_ONLOAD = "STOP_GETMESSAGES_ONLOAD";


	public NotificationSetterServlet() {

	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Setter Setter");
		String toReturn = "";
		String userid = request.getParameter("userid");
//		boolean setNotification = (request.getParameter("notifications") != null);
		//boolean setMessages = (request.getParameter("messages") != null);
//		if (userid == null) {
//			toReturn = "userid param not specified";
//		} else {
//			try {
//				if (setNotification) {
//					System.out.println("setting Notifications Read");
//					for (Notification notification :store.getUnreadNotificationsByUser(userid)) 
//						store.setNotificationRead(notification.getKey());
//				}
//				if (setMessages) {
//					System.out.println("setting Messages Read");
//					for (Notification notification :store.getUnreadNotificationMessagesByUser(userid)) {
//						store.setNotificationRead(notification.getKey());
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		boolean setMessages = (request.getParameter("messages") == null);
		if (setMessages)  //i clicked notifications
			setStopNotificationOnLoadInSession(request, true);
		else
			setStopMessagesOnLoadInSession(request, true);
		toReturn = "Set Done";
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(toReturn); 
	}
	
	private void setStopNotificationOnLoadInSession(HttpServletRequest request, boolean enabled) {
		request.getSession().setAttribute(STOP_GETNOTIF_ONLOAD, enabled);
	}
	
	private void setStopMessagesOnLoadInSession(HttpServletRequest request, boolean enabled) {
		request.getSession().setAttribute(STOP_GETMESSAGES_ONLOAD, enabled);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}


}
