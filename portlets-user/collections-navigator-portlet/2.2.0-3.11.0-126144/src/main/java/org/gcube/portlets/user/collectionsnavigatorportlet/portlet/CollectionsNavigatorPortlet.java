
package org.gcube.portlets.user.collectionsnavigatorportlet.portlet;

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
 * CollectionsNavigatorPortlet Portlet Class
 * 
 * @author Panagiota Koltsida, NKUA
 */
public class CollectionsNavigatorPortlet extends GenericPortlet {

	// 	JSP folder name
	public static final String JSP_FOLDER = "/WEB-INF/jsp";
	//	 JSP file name to be rendered on the view mode
	public static final String VIEW_JSP = JSP_FOLDER + "/CollectionsNavigatorPortlet_view.jsp";	

	protected  long timer = -1;
	protected static final int interval = 60000; // 10 minutes.
	private boolean bInitializing = true;

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		ScopeHelper.setContext(request);
		// Invokes the JSP to render
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request,response);		
	}

	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		if (bInitializing) {
			bInitializing = false;
		}
	}

}
