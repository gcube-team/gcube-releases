/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.portal.PortalContext;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2019
 */
public class PerformFishAnalyticsPortlet extends GenericPortlet{


	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     *
     */
    public static final String VIEW_JSP = JSP_FOLDER + "PerformFishAnalyticsPortlet_view.jsp";

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
