/**
 * 
 */
package org.gcube.portlets.user.joinvre.server.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class JoinVREConfigurationActionImpl implements ConfigurationAction {

	public static final String PORTLET_RESOURCE = "portletResource";
	
	public static final String PROPERTIES = "properties";
	
	/** {@inheritDoc} */
	@Override
	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {

        PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, PORTLET_RESOURCE);
        
        String properties = ParamUtil.getString(actionRequest, PROPERTIES);
        preferences.setValue(PROPERTIES, properties);
 
        preferences.store();
        
        SessionMessages.add(actionRequest, portletConfig.getPortletName() + ".doConfigure");
	}

	/** {@inheritDoc} */
	@Override
	public String render(PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
			throws Exception {
		return "/WEB-INF/jsp/JoinVRE_config.jsp";
	}

}
