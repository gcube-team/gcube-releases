
package org.gcube.portlets.admin.wfroleseditor.server.portlet;

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
 * WfRolesPortlet Portlet Class
 * @author Massimiliano Assante - ISTI-CNR
 */
public class WfRolesPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");
	    ScopeHelper.setContext(request);
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/WfRolesPortlet_view.jsp");
	    dispatcher.include(request, response);			
	}


	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
