package org.gcube.portlets.admin.policydefinition.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.policydefinition.services.informationsystem.InformationSystemClient;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulClient;
import org.gcube.portlets.admin.policydefinition.vaadin.components.ServicesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * An instance of this class in created for every user accessing the application.
 *
 */
public class PolicyDefinitionPortlet extends Application implements PortletRequestListener {	
	
	private static Logger logger = LoggerFactory.getLogger(PolicyDefinitionPortlet.class);
	
	private static final long serialVersionUID = -1185622918631995767L;

    @Override
    public void init() {
    	logger.debug("Initializing portlet Polcicy Definition...");
    	setTheme("reindeer"); // liferay, reindeer
    	InformationSystemClient.getInstance();
    	RestfulClient.getInstance();
    	Window mainWindow = new Window("Policy Definition");
		ServicesComponent services = new ServicesComponent();
		mainWindow.addComponent(services);
		mainWindow.setSizeFull();
		setMainWindow(mainWindow);
        logger.debug("Initialization portlet Polcicy Definition done.");    	
    }

	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		try {
			if(request instanceof RenderRequest){	
				ScopeHelper.setContext((RenderRequest)request);
			} else {
				long userid = Long.parseLong(request.getRemoteUser());
				User user = UserLocalServiceUtil.getUser(userid);
				String username = user.getScreenName();
				String sessionID = request.getPortletSession().getId();
				ASLSession session = SessionManager.getInstance().getASLSession(sessionID, username);
				logger.debug("Setting scope "+session.getScope()+" for "+username);
				ScopeProvider.instance.set(session.getScope().toString());
			}
			logger.debug("Setting scope on context done.");
		} catch (Exception e) {
			logger.error("Error setting scope", e);
			getMainWindow().showNotification("Internal server error", Notification.TYPE_ERROR_MESSAGE);	
		}
	}

	@Override
	public void onRequestEnd(PortletRequest request, PortletResponse response) {
		//nothing
	}
}
