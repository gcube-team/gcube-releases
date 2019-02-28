package org.gcube.portlets.user.performfish;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

public class CompanyFarmRepositoryConfigurationAction extends DefaultConfigurationAction {
	private static Log _log = LogFactoryUtil.getLog(CompanyFarmRepositoryConfigurationAction.class);
	@Override
	public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {      	  
		super.processAction(portletConfig, actionRequest, actionResponse);
		PortletPreferences prefs = actionRequest.getPreferences();
		String phaseValue = prefs.getValue("phase", "");     
		_log.info("selected phase = " + phaseValue + " in ConfigurationAction.processAction() saved correctly");
	}

	@Override
	public String render(PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
					throws Exception {
		return "/html/farmrepository/config.jsp";
	}

}
