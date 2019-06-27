package org.gcube.portlets.widgets.inviteswidget.server;


import java.util.List;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.invites.InvitesManager;
import org.gcube.portlets.widgets.inviteswidget.client.InviteService;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class InviteServiceImpl extends RemoteServiceServlet implements InviteService {

	private final static Logger _log = LoggerFactory.getLogger(InviteServiceImpl.class);
	private final static GroupManager GM = new LiferayGroupManager();
	private final static UserManager UM = new LiferayUserManager();
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
	
	@Override
	public Boolean accountExistInVRE(String email) {
		long groupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
		try {
			if (!GM.isVRE(groupId)) {
				return false; //if deployed not in a VRE the check is not performed
			}
			else {
				try {
					GCubeUser theUser2Check = UM.getUserByEmail(email);
					List<GCubeUser> theVREUsers = UM.listUsersByGroup(groupId, false);
					for (GCubeUser vreUser : theVREUsers) {
						if (vreUser.getUserId() == theUser2Check.getUserId()) {
							_log.debug("User exists in this VRE, should not send the invite in VRE having id: " + groupId + " to " + theUser2Check.toString());
							return true;
						}
					}
				} catch (UserRetrievalFault e) {
					_log.debug("User does not exist in this VRE, invite can be sent");
					return false;
				}
			}
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
			return false;
		} 		
		return false;
	}
	
}
