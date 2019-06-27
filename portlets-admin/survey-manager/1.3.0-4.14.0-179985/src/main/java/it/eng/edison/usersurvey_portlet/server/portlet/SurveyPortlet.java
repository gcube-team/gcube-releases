package it.eng.edison.usersurvey_portlet.server.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;

import it.eng.edison.usersurvey_portlet.server.GreetingServiceImpl;

public class SurveyPortlet extends GenericPortlet {
	private static final Logger _log = LoggerFactory.getLogger(SurveyPortlet.class);
	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
		
	    PortletPreferences portletPreferences=request.getPreferences();
		String displayName = GetterUtil.getString(portletPreferences.getValue("displayName", StringPool.BLANK));
		_log.debug("Questionnaire portlet config name read and stored in session: " + displayName);
		request.getPortletSession().setAttribute(GreetingServiceImpl.PORTLET_SCOPE_NAME_ATTR, displayName, PortletSession.APPLICATION_SCOPE);
		
		String remoteUser = request.getRemoteUser();
		if (remoteUser != null)
		    ScopeHelper.setContext(request);
		_log.debug("Survey context set");
	    PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/Survey_view.jsp");
	    dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}
}
