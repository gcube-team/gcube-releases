
package org.gcube.portlets.admin.rolesmanagementportlet.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * RolesManagementPortlet Portlet Class
 * 
 * @author Panagiota Koltsida, NKUA
 */
public class RolesManagementPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
        PortletContext context = getPortletContext();
        PortletRequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/jsp/RolesManagementPortlet_view.jsp");
        dispatcher.include(request, response);  
	}


	public void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/RolesManagementPortlet_edit.jsp");
        dispatcher.include(request, response);
		
	}

	public void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		response.setContentType("text/html");
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/RolesManagementPortlet_help.jsp");
        dispatcher.include(request, response);
		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
