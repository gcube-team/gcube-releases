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

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
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
	public static final String VIEW_JSP = JSP_FOLDER + "TabularDataPortlet_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		logger.trace("TabularDataPortlet loading from JSP: "+VIEW_JSP);

		logger.trace("setting context using ScopeHelper");
		ScopeHelper.setContext(request);
		
		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		logger.trace("Call: "+VIEW_JSP);
		rd.include(request,response);
	}
}
