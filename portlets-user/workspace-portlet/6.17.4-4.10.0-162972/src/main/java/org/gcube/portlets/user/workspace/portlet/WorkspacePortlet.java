package org.gcube.portlets.user.workspace.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.portal.PortalContext;

/**
 * The Class WorkspacePortlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 25, 2016
 */
public class WorkspacePortlet extends GenericPortlet{

	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     *
     */
    public static final String VIEW_JSP = JSP_FOLDER + "WorkspacePortlet_view.jsp";

    /**
     * Do view.
     *
     * @param request .
     * @param response .
     * @throws PortletException .
     * @throws IOException .
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    	System.out.println("LOADING ********* FROM "+VIEW_JSP);
    	// Invoke the JSP to render
    	PortalContext.setUserInSession(request);
    	PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
    	rd.include(request,response);
    }
}
