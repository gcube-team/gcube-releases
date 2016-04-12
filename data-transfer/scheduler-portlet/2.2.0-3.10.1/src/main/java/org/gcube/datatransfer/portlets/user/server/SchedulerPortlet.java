package org.gcube.datatransfer.portlets.user.server;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;


public class SchedulerPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically

		PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SchedulerPortlet_view.jsp");
	    dispatcher.include(request, response);
		
	}


	public void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		
        PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SchedulerPortlet_edit.jsp");
        dispatcher.include(request, response);
		
	}

	public void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SchedulerPortlet_help.jsp");
        dispatcher.include(request, response);
		
        
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}


}
