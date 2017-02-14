package org.gcube.portlets.user.gcubewidgets.server;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeService;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ScopeServiceImpl extends RemoteServiceServlet implements ScopeService {
	private static final Logger _log = LoggerFactory.getLogger(ScopeServiceImpl.class);



	@Override
	public boolean setScope(String portalURL) {
		String friendlyURL = extractOrgFriendlyURL(portalURL);
		if (friendlyURL == null) //the URL is not a portal URL, we are in devmode.
			return true;
		else {
			try {
				GroupManager gm = new LiferayGroupManager();
				List<GCubeGroup> groups = gm.listGroups();
				for (GCubeGroup g : groups) {
					_log.trace("Comparing " + g.getFriendlyURL() + " with: " +friendlyURL);
					if (g.getFriendlyURL().compareTo(friendlyURL) == 0) {
						setScope(g);
						return true;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void setScope(GCubeGroup currentGroup) throws Exception {
		GroupManager gm = new LiferayGroupManager();		
		String scopeToSet = gm.getInfrastructureScope(currentGroup.getGroupId());
		_log.debug("Client scopeToSet " + scopeToSet);
		getASLSession().setScope(scopeToSet);	
	}



	public static String getRootConfigFromGCore() {
		return PortalContext.getConfiguration().getInfrastructureName();
	}

	public static String extractOrgFriendlyURL(String portalURL) {
		String groupRegEx = "/group/";
		if (portalURL.contains(groupRegEx)) {
			_log.debug("LIFERAY PORTAL DETECTED");
			String[] splits = portalURL.split(groupRegEx);
			String friendlyURL = splits[1];
			if (friendlyURL.contains("/")) {
				friendlyURL = friendlyURL.split("/")[0];
			} else {
				friendlyURL = friendlyURL.split("\\?")[0].split("\\#")[0];
			}
			_log.trace("extracted friendly url: /" + friendlyURL);
			return "/"+friendlyURL;
		}
		return null;
	}



	private ASLSession getASLSession() {		
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("NO USER FOUND, exiting");
			return null;
		}
		else {
			_log.info("Found user=" + user);			
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
}
