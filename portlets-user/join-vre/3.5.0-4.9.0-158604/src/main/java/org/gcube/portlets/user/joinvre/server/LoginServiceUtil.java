package org.gcube.portlets.user.joinvre.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.mailing.message.Recipient;
import org.gcube.portal.mailing.service.EmailTemplateService;
import org.gcube.portal.mailing.templates.TemplateUserAcceptedInvite;
import org.gcube.portal.mailing.templates.TemplateUserRequestedAccessVRE;
import org.gcube.portal.mailing.templates.TemplateUserSelfRegisteredVRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
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
 * @author Massimiliano Assante ISTI-CNR
 * 
 */
public class LoginServiceUtil {
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";

	/**
	 * 
	 */
	public static final String PUBLIC_LAYOUT_NAME = "Data e-Infrastructure gateway";
	/**
	 * 
	 */
	public static final String GUEST_COMMUNITY_NAME = "Guest";

	private static Logger _log = LoggerFactory.getLogger(LoginServiceUtil.class);	
	
	protected static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		long groupId = -1;
		try {
			List<GCubeGroup> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				long grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getInfrastructureScope(grId);
				System.out.println("Comparing: " + groupScope + " " + scope);
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

	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void addMembershipRequest(String username, String scope, String optionalMessage, HttpServletRequest request) throws Exception{
		
		String gatewayName = PortalContext.getConfiguration().getGatewayName(request);
		String gatewayURL = PortalContext.getConfiguration().getGatewayURL(request);
		_log.info("gatewayName = " + gatewayName + " Message=" + optionalMessage);
			
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);
		ArrayList<Recipient> recipients = new ArrayList<>();
		for (String email : adminEmails) {
			recipients.add(new Recipient(email));
		}
		UserManager um = new LiferayUserManager();
		GCubeUser currUser = um.getUserByUsername(username);

		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		
		_log.info("Requested MEMBERSHIP for: " + selectedVRE + " scope: " +	scope);
		GroupManager gm = new LiferayGroupManager();

		long vreGroupId = gm.getGroupIdFromInfrastructureScope(scope);
		um.requestMembership(currUser.getUserId(),vreGroupId, optionalMessage);
		GCubeGroup theRequestingVRE = gm.getGroup(vreGroupId);		

		EmailTemplateService.send(
				"Request for access to VRE " + selectedVRE, 
				new TemplateUserRequestedAccessVRE(currUser, theRequestingVRE, optionalMessage, gatewayName, gatewayURL), 
				request, 
				recipients.toArray(new Recipient[recipients.size()]));
	}
	
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void notifyUserSelfRegistration(String username, String scope, HttpServletRequest request) throws Exception {
			
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);
		ArrayList<Recipient> recipients = new ArrayList<>();
		for (String email : adminEmails) {
			recipients.add(new Recipient(email));
		}	
		
		LiferayUserManager um = new LiferayUserManager();
		GCubeUser currUser = um.getUserByUsername(username);
		String gatewayURL = PortalContext.getConfiguration().getGatewayURL(request);
		String gatewayName = PortalContext.getConfiguration().getGatewayName(request);
		
		String[] allMails = new String[adminEmails.size()];
		adminEmails.toArray(allMails);
		
		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		
		_log.info("NotifyUser Self Registration for: " + selectedVRE + " scope: " +	scope);
		
		EmailTemplateService.send(
				"Self Registration to VRE " + selectedVRE, 
				new TemplateUserSelfRegisteredVRE(currUser, selectedVRE, gatewayName, gatewayURL), 
				request, 
				recipients.toArray(new Recipient[recipients.size()]));
	}
	
	
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void notifyUserAcceptedInvite(String username, String scope, Invite invite, HttpServletRequest request) throws Exception {			
		String gatewayURL = PortalContext.getConfiguration().getGatewayURL(request);
		String gatewayName = PortalContext.getConfiguration().getGatewayName(request);		
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);
		ArrayList<Recipient> recipients = new ArrayList<>();
		for (String email : adminEmails) {
			recipients.add(new Recipient(email));
		}	

		LiferayUserManager um = new LiferayUserManager();
		GCubeUser currUser = um.getUserByUsername(username);
		
		String[] allMails = new String[adminEmails.size()];
		adminEmails.toArray(allMails);

		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		
		_log.info("NotifyUser Self Registration for: " + selectedVRE + " scope: " +	scope);
		
		EmailTemplateService.send(
				"Accepted Invitation to VRE " + selectedVRE, 
				new TemplateUserAcceptedInvite(currUser, selectedVRE, invite.getSenderFullName(), invite.getSenderUserId(), invite.getTime(), gatewayName, gatewayURL), 
				request, 
				recipients.toArray(new Recipient[recipients.size()]));
	}
	
	
}
