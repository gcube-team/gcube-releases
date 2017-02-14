package org.gcube.portlets.widgets.workspaceuploader.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


/**
 * The Class WorkspaceUploaderPortlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 6, 2015
 */
public class WorkspaceUploaderPortlet extends GenericPortlet{
	
	/**
	 * JSP folder name
	 */
    public static final String JSP_FOLDER = "/WEB-INF/jsp/";

    /**
     * 
     */
    public static final String VIEW_JSP = JSP_FOLDER + "WorkspaceUploader_view.jsp";
    


    /**
     * Do view.
     *
     * @param request .
     * @param response .
     * @throws PortletException .
     * @throws IOException .
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
  	
//    	request.setAttribute("fromportlet","true");

    	// Invoke the JSP to render
    	PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
    	rd.include(request,response);
    }
}
