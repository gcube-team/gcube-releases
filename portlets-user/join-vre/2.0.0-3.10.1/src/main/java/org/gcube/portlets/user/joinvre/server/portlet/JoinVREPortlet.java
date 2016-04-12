package org.gcube.portlets.user.joinvre.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class JoinVREPortlet extends GenericPortlet {
	
	private static Log _log = LogFactoryUtil.getLog(JoinVREPortlet.class);

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info("************************* Rendering JoinVRE Portlet");
		
		response.setContentType("text/html");
		try {
			ScopeHelper.setContext(request);
		} catch(Exception e){
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			request.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay, PortletSession.APPLICATION_SCOPE);
			_log.error("The following exception is acceptable if the user is not logged.");
		}
		
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/JoinVRE_view.jsp");
		dispatcher.include(request, response);
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}
}
