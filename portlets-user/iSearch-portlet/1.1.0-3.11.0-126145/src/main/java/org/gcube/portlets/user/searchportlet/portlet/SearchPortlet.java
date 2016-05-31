
package org.gcube.portlets.user.searchportlet.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * SearchPortlet Portlet Class
 * 
 * @author Panagiota Koltsida, NKUA
 */
public class SearchPortlet extends GenericPortlet {
	
	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp";

	/**
	 * JSP file name to be rendered on the view mode
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "/SearchPortlet_view.jsp";

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		try
		{
			response.setContentType("text/html;charset=UTF-8");
			// the regular search form is displayed.
			getPortletContext().getRequestDispatcher(VIEW_JSP).include(request, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
