package org.gcube.portlets.user.accountingdashboard.portlet;



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
public class AccountingDashboardPortlet extends GenericPortlet {
	
	private final Logger logger= LoggerFactory.getLogger(GenericPortlet.class);

	/**
	 * 
	 */
	public static final String VIEW_JSP = "/WEB-INF/jsp/AccountingDashboardPortlet_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		logger.trace("AccountingDashboard loading from JSP: "+VIEW_JSP);
		logger.trace("Setting user in session using PortalContext");
		PortalContext.setUserInSession(request);
		

		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request,response);
	}
}
