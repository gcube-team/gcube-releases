
package org.gcube.portlets.admin.createusers.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
 public class CreateUsersPortlet extends GenericPortlet {
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/CreateUsers_view.jsp");
	    dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}
}
