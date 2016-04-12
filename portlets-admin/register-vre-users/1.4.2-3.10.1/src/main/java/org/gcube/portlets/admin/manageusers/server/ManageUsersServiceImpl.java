package org.gcube.portlets.admin.manageusers.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portlets.admin.manageusers.client.ManageUsersService;
import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

@SuppressWarnings("serial")
public class ManageUsersServiceImpl extends RemoteServiceServlet implements  ManageUsersService {

	private static final Logger _log = LoggerFactory.getLogger(ManageUsersServiceImpl.class);
	private boolean withinPortal = false;

	private GroupManager groupM = new LiferayGroupManager();
	private UserManager userM = new LiferayUserManager();
	
	private final static String POSITION = "POSITION";
	private final static String LABS = "LABS";

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
			withinPortal = true;
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	@Override
	public ArrayList<PortalUserDTO> getAvailableUsers() {
		getASLSession();
		ArrayList<PortalUserDTO> toReturn = new ArrayList<PortalUserDTO>();
		if (withinPortal) {
			return getNonBelongingUsersForVRE();
		}
		else {
			for (int i = 0; i < 20; i++) {
				String role = i % 2 == 0 ? "Research Staff" : "Graduate Fellow";
				String lastName = "TheLastName " + i;
				String firstLetter = lastName.substring(0,1);
				PortalUserDTO toAdd = new PortalUserDTO(""+i, firstLetter, "aName "+i, lastName, i+"email@isti.cnr.it", role, "HPC");
				toReturn.add(toAdd);
			}

			PortalUserDTO toAdd = new PortalUserDTO(""+1, "A", "Massimiliano", "Assante", "assante@isti.cnr.it", "Research Staff", "NeMIS");
			toReturn.add(toAdd);
			toAdd = new PortalUserDTO(""+1, "M", "Francesco", "Mangiacrapa", "mangi@isti.cnr.it", "Graduate Fellow", "NeMIS");
			toReturn.add(toAdd);
			toAdd = new PortalUserDTO(""+1, "N", "Salvatore", "Neri", "neri@isti.cnr.it", "Graduate Fellow", "KDD");
			toReturn.add(toAdd);
			toAdd = new PortalUserDTO(""+1, "P", "Giorgio", "Pini", "pini@isti.cnr.it", "Graduate Fellow", "SI");
			toReturn.add(toAdd);	
			toAdd = new PortalUserDTO(""+1, "F", "Dario", "Faggiu", "faggiu@isti.cnr.it", "Research Staff", "VC");
			toReturn.add(toAdd);	
			return toReturn;
		}

	}

	/**
	 * Retrieves all the users that are registered to portal but are not registered to the current VO
	 * 
	 * @return A list with the username of the unregistered users
	 */
	public ArrayList<PortalUserDTO> getNonBelongingUsersForVRE() {

		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			ArrayList<PortalUserDTO> unregisteredUsers = new ArrayList<PortalUserDTO>();
			List<UserModel> notBelongingUsers = userM.listUnregisteredUsersByGroup(getCurrentGroupID());
			for (UserModel u : notBelongingUsers) {
				String id = u.getScreenName();				
				user = UserLocalServiceUtil.getUserByScreenName(companyId, id);
			
				String position = (user.getExpandoBridge().getAttribute(POSITION) != null) ? user.getExpandoBridge().getAttribute(POSITION).toString() : "";
				String labs =  (user.getExpandoBridge().getAttribute(LABS) != null) ? user.getExpandoBridge().getAttribute(LABS).toString() : "";
				if ((u.getLastname() != null &&  u.getLastname().compareTo("") != 0)) {
					String firstLetter = u.getLastname().substring(0,1);
					String scope = getASLSession().getScope();
					ScopeBean validator = new ScopeBean(scope);
					String email = u.getEmail();
					String username = id;
					if (validator.is(Type.VRE)) {
						 email = "********@"+ email.split("@")[1];
					}
					PortalUserDTO myUser = new PortalUserDTO(username, firstLetter, u.getFirstname(), u.getLastname(), email, position, labs);
					unregisteredUsers.add(myUser);
				}
			}
			_log.debug("The total unregistered users are:  " + unregisteredUsers.size());
			
			_log.trace("Setting Thread Permission back to regular");
			user = UserLocalServiceUtil.getUserByScreenName(companyId, getASLSession().getUsername());
			permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			
			_log.trace("Setting Permission ok!");
			
			return unregisteredUsers;
		}	
		catch (Exception e) {
			_log.error("Failed to retrieve the unregistered users. An exception was thrown", e);
		}
		return null;
	}
	/**
	 * register the user to the VRE and in the HL Group, plus send notifications to the users
	 */
	@Override
	public boolean registerUsers(List<PortalUserDTO> users2Register) {
		for (PortalUserDTO user : users2Register) {
			try {
				//add the user to the VRE
				userM.assignUserToGroup(getCurrentGroupID(), userM.getUserId(user.getId()));
				//add him to the HL
				ASLSession session = getASLSession();
				addUserToHLGroup(user.getId(), session.getScope(), session.getUsername());
				//send notification
				sendNotificationToUser(user);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}
	/**
	 * 
	 * @param addressee
	 * @return
	 * @throws Exception
	 */
	private boolean sendNotificationToUser(PortalUserDTO addressee) throws Exception {
		ASLSession session = getASLSession();
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());

		List<String> recipientIds = new ArrayList<String>();
		recipientIds.add(addressee.getId());
		
		List<GenericItemBean> recipients = new ArrayList<GenericItemBean>();
		recipients.add(new GenericItemBean(addressee.getId(), addressee.getId(), addressee.getName() + " " + addressee.getLastName(), ""));
		
		String subject = "Registration to Group Notification";
		String body = "Dear "+addressee.getName()+", \n\n" + session.getUserFullName() + " has registered you to the group " + getASLSession().getGroupName()+".";
		String messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body, new ArrayList<String>(), recipientIds);
		
		_log.debug("Sending message notification to: " + recipientIds.toString());
		NotificationsManager nm = new ApplicationNotificationsManager(getASLSession());
		Thread thread = new Thread(new MessageNotificationsThread(recipients, messageId, subject, body, nm));
		thread.start();

		return (messageId != null);		
	}

	/**
	 * Get the current group ID
	 * 
	 * @return the current group ID or null if an exception is thrown
	 * @throws Exception 
	 */
	private String getCurrentGroupID() {
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		String toReturn = null;
		try {
			toReturn = groupM.getGroupId(session.getGroupName());
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return toReturn;
	}


	private void addUserToHLGroup(String username, String group, String adminUsername) {
		try {
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			um.associateUserToGroup(group, username, adminUsername);
		} catch (Exception e) {
			_log.error("Failed to get the usermanager from HL. Could not add user to the HL group");
		}
	}

	

}
