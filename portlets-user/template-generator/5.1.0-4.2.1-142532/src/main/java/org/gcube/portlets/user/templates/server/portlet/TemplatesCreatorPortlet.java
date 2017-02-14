
package org.gcube.portlets.user.templates.server.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * TemplatesCreatorPortlet Portlet Class
 * @author massi
 */
public class TemplatesCreatorPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {

		ScopeHelper.setContext(request);

		String queryString = (String) request.getAttribute("javax.servlet.forward.query_string");
		String id = "";
		if (queryString != null ) {
			if (! queryString.equals("")) {
				//id=12345678&name=pino
				String[] params = queryString.split("&");
				if (params.length < 2) {
					id = params[0].split("=")[1];
					if (! id.equals("") )
						request.getPortletSession().setAttribute("templatedid", id, PortletSession.APPLICATION_SCOPE);
				}
			}
		}

		System.out.println("PORTLET LOG: templateid: " + id);	

		
		
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/TemplatesCreatorPortlet_view.jsp");
	    dispatcher.include(request, response);
		
	}


	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
