package org.gcube.portlets.widgets.inviteswidget.server;


import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.invites.InvitesManager;
import org.gcube.portlets.widgets.inviteswidget.client.InviteService;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class InviteServiceImpl extends RemoteServiceServlet implements InviteService {

	private final static Logger _log = LoggerFactory.getLogger(InviteServiceImpl.class);
	/**
	 * 
	 */
	@Override
	public InviteOperationResult sendInvite(String name, String lastName, String email) throws IllegalArgumentException {
		String lowercaseEmail = email.toLowerCase();
		String portalUrl = null;
		String vreDescription  = null;
		long groupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
		try {
			portalUrl = PortalContext.getConfiguration().getGatewayURL(this.getThreadLocalRequest());
			vreDescription = new LiferayGroupManager().getGroup(groupId).getDescription();
		} catch (Exception e1) {
			e1.printStackTrace();
			_log.warn("While trying to send email for invitation to " + lowercaseEmail);
			return null;
		} 
		String portalSenderEmail = PortalContext.getConfiguration().getSenderEmail(getThreadLocalRequest());
		return InvitesManager.getInstance().sendInvite(this.getThreadLocalRequest(), portalSenderEmail, portalUrl, name, lastName, lowercaseEmail, vreDescription);
	}
	
}
