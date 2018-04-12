/**
 * 
 */
package org.gcube.portets.user.message_conversations.server.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessagesPortlet extends GenericPortlet {
	
	

	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "Messages_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_JSP);
		rd.include(request,response);
	}
}
