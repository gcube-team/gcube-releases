package org.gcube.portlets.user.gcubelogin.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
public class LoginPortlet extends GenericPortlet {
	public static final String GATEWAY_NAME = "GATEWAY_NAME";
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {

		response.setContentType("text/html");
		System.out.println("LoginPortlet ...");
		ScopeHelper.setContext(request);

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/LoginPortlet_view.jsp");
		dispatcher.include(request, response);

	}


	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}