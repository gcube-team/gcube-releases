package org.gcube.portlets.widgets.file_dw_import_wizard.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Filedwimportwizard extends GenericPortlet {
	
//	protected GCUBELog logger = new GCUBELog(Filedwimportwizard .class);


	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "filedwimportwizard_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
//		logger.trace("Filedwimportwizard loading from JSP: "+VIEW_JSP);
//
//		logger.trace("setting context using ScopeHelper");
		ScopeHelper.setContext(request);
		
//		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request,response);
	}
}
