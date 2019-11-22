
package org.gcube.portlets.user.gcubeloggedin.server.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * LoggedinPortlet Portlet Class
 * @author massi
 */
public class LoggedinPortlet extends GenericPortlet {
	
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		
		response.setContentType("text/html");
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/LoggedinPortlet_view.jsp");
		dispatcher.include(request, response);

	}
}
