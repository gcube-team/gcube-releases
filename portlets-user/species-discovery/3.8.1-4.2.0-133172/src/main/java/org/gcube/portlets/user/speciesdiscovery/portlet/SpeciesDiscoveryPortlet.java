/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class SpeciesDiscoveryPortlet extends GenericPortlet {
	
	protected Logger logger = Logger.getLogger(SpeciesDiscoveryPortlet.class);


	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "SpeciesDiscovery_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		//gr.uoa.di.madgik.commons.utils.FileUtils
		//gr.uoa.di.madgik.grs.record.GenericRecordDefinition
		
		logger.trace("SpeciesDiscovery loading from JSP: "+VIEW_JSP);

		logger.trace("setting context using ScopeHelper");
		ScopeHelper.setContext(request);
		
		logger.trace("passing to the render");
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request,response);
	}
}
