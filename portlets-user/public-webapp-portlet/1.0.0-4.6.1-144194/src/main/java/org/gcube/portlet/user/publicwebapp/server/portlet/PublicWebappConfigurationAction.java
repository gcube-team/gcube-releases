package org.gcube.portlet.user.publicwebapp.server.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

/**
 * PublicWebapp portlet Configuration Action.
 * Allows Liferay VRE manager to configure a public web-app within a portlet container
 * 
 * @author "Emmanuel Blondel" <a href="mailto:emmanuel.blondel@fao.org">emmanuel.blondel@fao.org</a>
 *
 */
public class PublicWebappConfigurationAction extends DefaultConfigurationAction {

    @Override
    public void processAction(
        PortletConfig portletConfig, ActionRequest actionRequest,
        ActionResponse actionResponse) throws Exception {  

        super.processAction(portletConfig, actionRequest, actionResponse);

        PortletPreferences prefs = actionRequest.getPreferences();
        String appURL = prefs.getValue("appURL", "true");
        String appURLTokenParam = prefs.getValue("appURLTokenParam", "true");
    
        System.out.println("appURL = " + appURL + " in PublicWebappConfigurationAction.processAction().");
        System.out.println("appURLTokenParam = " + appURLTokenParam + " in PublicWebappConfigurationAction.processAction().");
    }
    
    @Override
    public String render(PortletConfig portletConfig,
            RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
 
        return "/html/config.jsp";
    }
 
}