package org.gcube.portlets.user.workspace.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
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
     * @param request .
     * @param response .
     * @throws IOException .
     * @throws PortletException .
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    	
    	System.out.println("LOADING ********* FROM "+VIEW_JSP);
    	
    	ScopeHelper.setContext(request, ScopeHelper.USERNAME_ATTRIBUTE);
    	
//    	request.setAttribute("fromportlet","true");

    	// Invoke the JSP to render
    	PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
    	rd.include(request,response);
    }
}
