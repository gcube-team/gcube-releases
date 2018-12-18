/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.server.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portlets.admin.authportletmanager.server.AuthServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class AuthManagerPortlet extends GenericPortlet {
	private static final Log logger = LogFactoryUtil.getLog(AuthServiceImpl.class);
	/**
	 * JSP folder name
	 */
	public static final String JSP_FOLDER = "/WEB-INF/jsp/";

	/**
	 * 
	 */
	public static final String VIEW_JSP = JSP_FOLDER + "AuthPortletManager_view.jsp";

	/**
	 * @param request .
	 * @param response .
	 * @throws IOException .
	 * @throws PortletException .
	 */
	
	public void doView(RenderRequest request, RenderResponse response)    throws PortletException, IOException {
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(VIEW_JSP);
	    dispatcher.include(request, response);
	}
}
