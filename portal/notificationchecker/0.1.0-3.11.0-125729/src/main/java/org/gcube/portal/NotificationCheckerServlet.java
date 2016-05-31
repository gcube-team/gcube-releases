package org.gcube.portal;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationIDNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationTypeNotFoundException;

/**
 * Servlet implementation class NotificationCheckerServlet
 */

@SuppressWarnings("serial")
public class NotificationCheckerServlet extends HttpServlet {
	DatabookStore store;
	public void init() {
		store = new DBCassandraAstyanaxImpl();
	}

	public NotificationCheckerServlet() {

	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("NotificationCheckerServlet");
		String toReturn = "";
		String userid = request.getParameter("userid");
		String unlockread = request.getParameter("unlockread");
		//System.out.println("unlockread param:  " + unlockread);

		if (userid == null) {
			toReturn = "userid param not specified";
		} else {
			if (unlockread.equals("true")) {
				setStopNotificationOnLoadInSession(request, false); //unlock the read
				setStopMessagesOnLoadInSession(request, false); 
			}
			try {
				if (isStopNotificationOnLoadInSession(request) )
					toReturn = "false"; //return no notifications because user has just clicked on notification icon
				else {
					 //System.out.println("NOTIF START" + new java.util.Date());
					if (store.checkUnreadNotifications(request.getParameter("userid")))
						toReturn = "true";
					else 
						toReturn = "false";
					 //System.out.println("NOTIF END" + new java.util.Date());
				}
				toReturn +=",";
				if (isStopMessagesOnLoadInSession(request) )
					toReturn += "false"; //return no messages because user has just clicked on messages icon
				else {
					//System.out.println("MESSAGE START" + new java.util.Date());
					if (store.checkUnreadMessagesNotifications(request.getParameter("userid")))
						toReturn += "true";
					else 
						toReturn += "false";
					//System.out.println("MESSAGE END" + new java.util.Date());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//System.out.println("-> " + userid + " has Notification/Messages? " + toReturn);

		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(toReturn); 
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	//return whether the user has just clicked on notification icon
	private Boolean isStopNotificationOnLoadInSession(HttpServletRequest request) {
		if (request.getSession().getAttribute(NotificationSetterServlet.STOP_GETNOTIF_ONLOAD) == null)
			return false;
		boolean toReturn = (Boolean) request.getSession().getAttribute(NotificationSetterServlet.STOP_GETNOTIF_ONLOAD);
		//System.out.println("isStopNotificationOnLoadInSession?" + toReturn);
		return toReturn;
	}

	//return whether the user has just clicked on messages dockbar icon
	private Boolean isStopMessagesOnLoadInSession(HttpServletRequest request) {
		if (request.getSession().getAttribute(NotificationSetterServlet.STOP_GETMESSAGES_ONLOAD) == null)
			return false;
		boolean toReturn = (Boolean) request.getSession().getAttribute(NotificationSetterServlet.STOP_GETMESSAGES_ONLOAD);
		//System.out.println("isStopMessagesOnLoadInSession?" + toReturn);
		return toReturn;
	}

	private void setStopNotificationOnLoadInSession(HttpServletRequest request, boolean enabled) {
		request.getSession().setAttribute(NotificationSetterServlet.STOP_GETNOTIF_ONLOAD, enabled);
	}

	private void setStopMessagesOnLoadInSession(HttpServletRequest request, boolean enabled) {
		request.getSession().setAttribute(NotificationSetterServlet.STOP_GETMESSAGES_ONLOAD, enabled);
	}
}