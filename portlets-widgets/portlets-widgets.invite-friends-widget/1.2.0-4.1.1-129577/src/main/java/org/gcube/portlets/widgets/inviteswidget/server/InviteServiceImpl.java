package org.gcube.portlets.widgets.inviteswidget.server;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.invites.InvitesManager;
import org.gcube.portlets.widgets.inviteswidget.client.InviteService;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.util.PortalUtil;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InviteServiceImpl extends RemoteServiceServlet implements InviteService {

	private final static Logger _log = LoggerFactory.getLogger(InviteServiceImpl.class);

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user");
			user = "test.user";
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * 
	 */
	@Override
	public InviteOperationResult sendInvite(String name, String lastName, String email) throws IllegalArgumentException {
		ASLSession aslSession = getASLSession();
	
		String lowercaseEmail = email.toLowerCase();
		String portalUrl = null;
		String vreDescription  = null;
		long groupId = getASLSession().getGroupId();
		try {
			portalUrl = PortalContext.getConfiguration().getGatewayURL(this.getThreadLocalRequest());
			vreDescription = new LiferayGroupManager().getGroup(groupId).getDescription();
		} catch (Exception e1) {
			e1.printStackTrace();
			_log.warn("While trying to send email for invitation to " + lowercaseEmail);
			return null;
		} 
		String portalSenderEmail = PortalContext.getConfiguration().getSenderEmail();
		return InvitesManager.getInstance().sendInvite(this.getThreadLocalRequest(), aslSession, portalSenderEmail, portalUrl, name, lastName, lowercaseEmail, vreDescription);
	}
	

	


}
