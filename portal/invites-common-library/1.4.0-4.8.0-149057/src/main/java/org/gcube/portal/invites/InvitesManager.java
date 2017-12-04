package org.gcube.portal.invites;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.mailing.message.EmailAddress;
import org.gcube.portal.mailing.message.Recipient;
import org.gcube.portal.mailing.message.RecipientType;
import org.gcube.portal.mailing.service.EmailTemplateService;
import org.gcube.portal.mailing.templates.TemplateUserHasInvited;
import org.gcube.portal.mailing.templates.TemplatedJoinMeInvite;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante
 *
 */
public class InvitesManager {
	private static final Logger _log = LoggerFactory.getLogger(InvitesManager.class);

	private static InvitesManager instance;
	private static DatabookStore store;

	private InvitesManager() { }

	public static InvitesManager getInstance(){
		instance = new InvitesManager();
		initStore();
		return instance;
	}
	/**
	 * 
	 * @return the unique instance of the store
	 */
	public static synchronized DatabookStore initStore() {
		if (store == null) {
			store = new DBCassandraAstyanaxImpl();
		}
		return store;
	}

	public InviteOperationResult sendInvite(
			HttpServletRequest request,
			String portalSenderEmail,
			String portalURL,
			String name, 
			String lastName,
			String email,
			String vreDescription) {

		GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(request);
		String username = currUser.getUsername();
		String fromFullName = currUser.getFullname();
		String controlcode = UUID.randomUUID().toString();
		String currScope =  PortalContext.getConfiguration().getCurrentScope(request);

		Invite invite = new Invite(UUID.randomUUID().toString(), username, currScope, email, controlcode, InviteStatus.PENDING, new Date(), fromFullName); 
		InviteOperationResult result = null;
		boolean emailResult = false;
		try {
			String vreName =  PortalContext.getConfiguration().getCurrentGroupName(request);
			result = store.saveInvite(invite);
			emailResult = sendInviteEmail(request, currUser, vreName, name, email, vreDescription);
			notifyInviteSent(request, currUser, currScope, invite, vreName);

		} catch (AddressException e) {
			_log.error("Email not valid " + e.getMessage());
			e.printStackTrace();
			return InviteOperationResult.FAILED;			
		}		

		return (emailResult) ? result : InviteOperationResult.FAILED;		
	}

	private Boolean sendInviteEmail(
			HttpServletRequest request,
			GCubeUser currUser,
			String vreName,
			String name, 
			String email,
			String vreDescription) {
		
		PortalContext pContext = PortalContext.getConfiguration();
		String gatewayURL = pContext.getGatewayURL(request);
		String gatewayName = pContext.getGatewayName(request);
		

		try {
			String subject = "Join me on " + vreName + " VRE";

			long groupId = PortalContext.getConfiguration().getCurrentGroupId(request);
			final String linkToAcceptInvite = gatewayURL + PortalContext.getConfiguration().getSiteLandingPagePath(request)+"/explore?siteId="+groupId;
			final String linkToCreateAccount = gatewayURL + "/?p_p_id=58&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&saveLastPath=false&_58_struts_action=%2Flogin%2Fcreate_account";
			
			EmailTemplateService.send(
					subject, 
					new TemplatedJoinMeInvite(gatewayName, gatewayURL, currUser, name, vreName, vreDescription, linkToAcceptInvite, linkToCreateAccount), 
					request,
					new Recipient(email), new Recipient(new EmailAddress(currUser.getEmail()), RecipientType.CC));
			
			
			_log.debug("Join Me Invite email message sent successfully to " + email );
		} catch (Exception mex) {
			mex.printStackTrace();
			_log.error("Sent message ERROR to " + email );
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param request
	 * @param username
	 * @param scope
	 * @param invite
	 * @param vreName
	 */
	public static void notifyInviteSent(HttpServletRequest request, GCubeUser currUser, String scope, Invite invite, String vreName) {

		List<Recipient> adminRecipients = new ArrayList<>();
		for (String email : getAdministratorsEmails(scope)) {
			adminRecipients.add(new Recipient(email));
		}
		String gatewayURL = PortalContext.getConfiguration().getGatewayURL(request);
		String gatewayName = PortalContext.getConfiguration().getGatewayName(request);

		String subject = new StringBuffer("An invite was sent on ").append(vreName).append(" by ").append(invite.getSenderFullName()).toString();
		EmailTemplateService.send(
				subject, 
				new TemplateUserHasInvited(currUser, invite.getInvitedEmail(), vreName, gatewayName, gatewayURL),
				request,
				adminRecipients.toArray(new Recipient[adminRecipients.size()]));		
	}

	
	private static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		long groupId = -1;
		try {
			List<GCubeGroup> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				long grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getInfrastructureScope(grId);
				_log.debug("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		}
		Map<GCubeUser, List<GCubeRole>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<GCubeUser> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (GCubeUser usr:users) {
			List<GCubeRole> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VO-Admin") || roles.get(i).getRoleName().equals("VRE-Manager")) {
					adminEmailsList.add(usr.getEmail());
					_log.debug("Admin: " + usr.getFullname());
					break;
				}
			}
		}
		return adminEmailsList;
	}

	
}
