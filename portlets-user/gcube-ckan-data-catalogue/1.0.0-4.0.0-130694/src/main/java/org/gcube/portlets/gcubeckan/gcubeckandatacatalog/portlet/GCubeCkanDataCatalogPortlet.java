package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;


/**
 * The Class GCubeCkanDataCatalogPortlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 12, 2016
 */
public class GCubeCkanDataCatalogPortlet extends GenericPortlet{

	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     *
     */
    public static final String VIEW_JSP = JSP_FOLDER + "GCubeCkanDataCatalogPortlet_view.jsp";

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
		try {
			ScopeHelper.setContext(request, ScopeHelper.USERNAME_ATTRIBUTE); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		}
		catch (Exception e) {
			System.out.println("Could not initialize portlet context");
		}
    	PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
    	rd.include(request,response);
    }
}
