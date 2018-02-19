package org.gcube.portlets.user.topics.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class TopicsPortlet  extends GenericPortlet {
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/Topics_view.jsp");
	    dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}
}
