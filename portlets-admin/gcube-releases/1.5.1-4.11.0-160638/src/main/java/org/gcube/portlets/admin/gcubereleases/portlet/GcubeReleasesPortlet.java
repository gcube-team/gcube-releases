package org.gcube.portlets.admin.gcubereleases.portlet;

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
 * The Class GcubeReleasesPortlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class GcubeReleasesPortlet extends GenericPortlet{

	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     *
     */
    public static final String VIEW_JSP = JSP_FOLDER + "GcubeReleasesPortlet_view.jsp";

    private static Logger _log = LoggerFactory.getLogger(GcubeReleasesPortlet.class);


    /* (non-Javadoc)
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    	System.out.println("LOADING ********* FROM "+VIEW_JSP);

    	try{
    		ScopeHelper.setContext(request, ScopeHelper.USERNAME_ATTRIBUTE);
    	}catch(Exception e){
    		_log.error("Skipping error on set Context: "+ e.getMessage());
    		_log.error("FIX THIS ISSUE FOR LIFERAY 6.2!!!");
    	}
//    	request.setAttribute("fromportlet","true");

    	// Invoke the JSP to render
    	PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
    	rd.include(request,response);
    }
}
