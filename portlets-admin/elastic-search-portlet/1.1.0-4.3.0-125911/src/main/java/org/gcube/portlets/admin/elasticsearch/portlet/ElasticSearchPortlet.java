
package org.gcube.portlets.admin.elasticsearch.portlet;

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
 * ElasticSearchPortlet Portlet
 * @author Panagiota Koltsida, NKUA
 */
public class ElasticSearchPortlet extends GenericPortlet {

	public static final String INDEX_NODE_SERVICE_RIs = "indexnodeserviceRIsAttr";
	
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		response.setContentType("text/html");
		ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/ElasticSearchPortlet_view.jsp");
	    dispatcher.include(request, response);
		
	}

	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

	}
}
