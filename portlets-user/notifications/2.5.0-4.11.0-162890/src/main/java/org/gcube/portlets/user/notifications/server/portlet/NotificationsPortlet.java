package org.gcube.portlets.user.notifications.server.portlet;




import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * NotificationsPortlet Portlet Class
 * @author Massimiliano Assante - ISTI CNR
 * @version 1.0 Jan 2013
 */
public class NotificationsPortlet extends GenericPortlet {
	
	public void init() throws PortletException {
	}

	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/Notifications_view.jsp");
	    dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

}
