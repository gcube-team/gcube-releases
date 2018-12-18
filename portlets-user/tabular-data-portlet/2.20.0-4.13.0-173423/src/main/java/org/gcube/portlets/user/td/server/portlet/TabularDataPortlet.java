/**
 * 
 */
package org.gcube.portlets.user.td.server.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.common.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabularDataPortlet extends GenericPortlet {

	protected Logger logger = LoggerFactory.getLogger(TabularDataPortlet.class);

	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER
			+ "TabularDataPortlet_view.jsp";


	/**
	 * 
	 */
	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		logger.trace("Loading from JSP: " + VIEW_JSP);
		logger.trace("Setting user in session using PortalContext");
		PortalContext.setUserInSession(request);

		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(
				VIEW_JSP);
		logger.trace("Call: " + VIEW_JSP);
		rd.include(request, response);

	}
}
