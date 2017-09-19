
package org.gcube.portlets.admin.resourcemanagement.server.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * ResourceManagementPortlet Portlet Class
 * @author massi
 */
public class ResourceManagementPortlet extends GenericPortlet {
	// private static final String LOG_PREFIX = "[ResourceManagementPortlet]";

	public final void doView(final RenderRequest request, final RenderResponse response)
	throws PortletException, IOException {

		response.setContentType("text/html");
		try {
			ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		}
		catch (Exception e) {
			System.out.println("Could not initialize portlet context");
		}
		PortletRequestDispatcher dispatcher =
			getPortletContext().getRequestDispatcher("/WEB-INF/jsp/ResourceManagementPortlet_view.jsp");
		dispatcher.include(request, response);

	}


	public void processAction(final ActionRequest request, final ActionResponse response)
	throws PortletException, IOException {
		// nop - done by massi
	}

}
