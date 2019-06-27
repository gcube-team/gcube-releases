package org.gcube.portal.invites;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
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
	public static final String SITEID_ATTR ="siteId";
	public static final String INVITEID_ATTR ="inviteId";
	public static final String INVITE_PAGE_ENDPOINT = "manage-invite";


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
			if (result == InviteOperationResult.ALREADY_INVITED) {
				invite.setKey(store.isExistingInvite(currScope, email));
			}			
			emailResult = sendInviteEmail(request, invite, currUser, vreName, name, email, vreDescription);
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
			Invite invite,
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

		;
			
			StringBuilder getParamsEncoded = new StringBuilder(URLEncoder.encode(new String(Base64.encodeBase64(INVITEID_ATTR.getBytes())), "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(new String(Base64.encodeBase64(invite.getKey().getBytes())), "UTF-8"))
					.append("&")
					.append(URLEncoder.encode(new String(Base64.encodeBase64(SITEID_ATTR.getBytes())), "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(new String(Base64.encodeBase64((""+groupId).getBytes())), "UTF-8"));

			StringBuilder linkToAcceptInvite = new StringBuilder(gatewayURL)
					.append("/")
					.append(INVITE_PAGE_ENDPOINT)
					.append("?")
					.append(getParamsEncoded);

			EmailTemplateService.send(
					subject, 
					new TemplatedJoinMeInvite(gatewayName, gatewayURL, currUser, name, vreName, vreDescription, linkToAcceptInvite.toString()), 
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
