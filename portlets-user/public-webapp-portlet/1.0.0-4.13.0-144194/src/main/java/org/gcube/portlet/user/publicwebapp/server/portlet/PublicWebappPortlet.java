package org.gcube.portlet.user.publicwebapp.server.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.util.PortalUtil;

/**
 * PublicWebapp portlet - facilitates the integration of public web-apps and grabing of VRE security tokens
 * 
 * @author "Emmanuel Blondel" <a href="mailto:emmanuel.blondel@fao.org">emmanuel.blondel@fao.org</a>
 *
 */
@SuppressWarnings("deprecation")
public class PublicWebappPortlet extends GenericPortlet {
	
	public void doView(RenderRequest request, RenderResponse response)throws PortletException, IOException {	
		response.setContentType("text/html");
		ScopeHelper.setContext(request);
		
		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();
		String sessionID = httpSession.getId();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();

		ASLSession aslSession = SessionManager.getInstance().getASLSession(sessionID, username);
		String token = aslSession.getSecurityToken();
		
		if (token != null){
			request.setAttribute("securityToken", token);
		}
		
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/html/view.jsp");
	    dispatcher.include(request, response);		
	}

}