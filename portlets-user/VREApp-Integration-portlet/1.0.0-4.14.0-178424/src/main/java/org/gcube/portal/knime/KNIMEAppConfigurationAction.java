package org.gcube.portal.knime;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

public class KNIMEAppConfigurationAction extends DefaultConfigurationAction {
	private static Log _log = LogFactoryUtil.getLog(KNIMEAppConfigurationAction.class);
    @Override
    public void processAction(
        PortletConfig portletConfig, ActionRequest actionRequest,
        ActionResponse actionResponse) throws Exception {  

        super.processAction(portletConfig, actionRequest, actionResponse);

        PortletPreferences prefs = actionRequest.getPreferences();
        String appURL = prefs.getValue("KNIMEAppURL", "true");
        String appURLTokenParam = prefs.getValue("KNIMEAppURLTokenParam", "true");
    
        _log.debug("KNIMEAppURL = " + appURL + " in PublicWebappConfigurationAction.processAction().");
        _log.debug("KNIMEAppURLTokenParam = " + appURLTokenParam + " in PublicWebappConfigurationAction.processAction().");
    }
    
    @Override
    public String render(PortletConfig portletConfig,
            RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
 
        return "/html/knimeappintegration/config.jsp";
    }
 
}