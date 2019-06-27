/**
 * 
 */
package org.gcube.portlets.admin.dataminermanagerdeployer.portlet;

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
public class DataMinerManagerDeployer extends GenericPortlet {

	private final Logger logger = LoggerFactory.getLogger(GenericPortlet.class);

	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "DataMinerManagerDeployer_view.jsp";

	/**
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @throws IOException
	 *             IOException
	 * @throws PortletException
	 *             PortletException
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		logger.trace("DataMinerManagerDeployer loading from JSP: " + VIEW_JSP);
		logger.trace("Setting user in session using PortalContext");
		PortalContext.setUserInSession(request);

		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request, response);
	}
}
