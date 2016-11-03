package org.gcube.portlets.admin.fhn_manager_portlet.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;


public class FhnManagerPortlet extends GenericPortlet{

	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     * 
     */
    public static final String VIEW_JSP = JSP_FOLDER + "fhnManagerPortlet_view.jsp";
    
    
	
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
			ScopeHelper.setContext(request);
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher(VIEW_JSP);
	    dispatcher.include(request, response);		
	}

	/**
	 * 
	 */
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}
}
