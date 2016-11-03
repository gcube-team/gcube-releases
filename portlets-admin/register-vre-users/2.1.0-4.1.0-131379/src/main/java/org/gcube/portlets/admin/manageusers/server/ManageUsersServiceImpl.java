package org.gcube.portlets.admin.manageusers.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portlets.admin.manageusers.client.ManageUsersService;
import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;

@SuppressWarnings("serial")
public class ManageUsersServiceImpl extends RemoteServiceServlet implements  ManageUsersService {

	private static final Logger _log = LoggerFactory.getLogger(ManageUsersServiceImpl.class);


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
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	@Override
	public ArrayList<PortalUserDTO> getAvailableUsers() {
		getASLSession();
		ArrayList<PortalUserDTO> toReturn = new ArrayList<PortalUserDTO>();
		if (isWithinPortal()) {
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
	 * 
	 * @param request
	 * @return the current Group instance based on the request
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Group getSiteFromServletRequest(final HttpServletRequest request) throws Exception {
		String serverName = request.getServerName();
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				return site;
			}
		}
		_log.warn("serverName is " +  serverName + " but i could not find any virtualHost associated to it");
		return null;
	}
	/**
	 * Retrieves all the users that are registered to portal but are not registered to the current VO
	 * 
	 * @return A list with the username of the unregistered users
	 */
	public ArrayList<PortalUserDTO> getNonBelongingUsersForVRE() {

		try {
			long companyId = SiteManagerUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User userAdmin = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			_log.trace("Setting Thread Permission for admin="+userAdmin.getScreenName());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(userAdmin);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			ArrayList<PortalUserDTO> unregisteredUsers = new ArrayList<PortalUserDTO>();
			
			//List<GCubeUser> notBelongingUsers = userM.listUnregisteredUsersByGroup(getCurrentGroupID());
			long currSiteGroupId = getSiteFromServletRequest(getThreadLocalRequest()).getGroupId();
			List<GCubeUser> nonBelongingUsers = userM.listUsersByGroup(currSiteGroupId); //the users of i-marine or services etc
			nonBelongingUsers.removeAll(userM.listUsersByGroup(getCurrentGroupID()));
			
			for (GCubeUser u : nonBelongingUsers) {
				String id = u.getUsername();		
				User user = UserLocalServiceUtil.getUserByScreenName(companyId, id);
				String position = "";
				String labs = "";
				if (user.getExpandoBridge() != null && user.getExpandoBridge().hasAttribute(POSITION)) {
					position = (user.getExpandoBridge().getAttribute(POSITION) != null) ? user.getExpandoBridge().getAttribute(POSITION).toString() : "";
					labs =  (user.getExpandoBridge().getAttribute(LABS) != null) ? user.getExpandoBridge().getAttribute(LABS).toString() : "";
				}
				if ((u.getLastName() != null &&  u.getLastName().compareTo("") != 0)) {
					String firstLetter = u.getLastName().substring(0,1);
					String scope = getASLSession().getScope();
					ScopeBean validator = new ScopeBean(scope);
					String email = u.getEmail();
					String username = id;
					if (validator.is(Type.VRE)) {
						email = "********@"+ email.split("@")[1];
					}
					PortalUserDTO myUser = new PortalUserDTO(username, firstLetter, u.getFirstName(), u.getLastName(), email, position, labs);
					unregisteredUsers.add(myUser);
				}
			}
			_log.trace("Setting Thread Permission back to regular");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, getASLSession().getUsername());
			permissionChecker = PermissionCheckerFactoryUtil.create(user);
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
				long vreGroupId = getCurrentGroupID();
				//add the user to the VRE
				userM.assignUserToGroup(vreGroupId, userM.getUserId(user.getId()));
				//send notification
				sendNotificationToUser(vreGroupId, user);
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
	private boolean sendNotificationToUser(long groupId, PortalUserDTO addressee) throws Exception {
		ASLSession session = getASLSession();
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());

		List<String> recipientIds = new ArrayList<String>();
		recipientIds.add(addressee.getId());

		List<GenericItemBean> recipients = new ArrayList<GenericItemBean>();
		recipients.add(new GenericItemBean(addressee.getId(), addressee.getId(), addressee.getName() + " " + addressee.getLastName(), ""));

		String gatewayName = PortalContext.getConfiguration().getGatewayName(getThreadLocalRequest());
		String gatewayURL = PortalContext.getConfiguration().getGatewayURL(getThreadLocalRequest());
		String vreURL = gatewayURL + "/group" + new LiferayGroupManager().getGroup(groupId).getFriendlyURL();
		
		String subject = "Registration to VRE notification";
		String body = "Dear "+addressee.getName()+", \n\n I just registered you to the VRE " + session.getGroupName()+" on the " + gatewayName + ". \n\n";
		body += "Please, click here to access the VRE: " + vreURL;
		String messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body, new ArrayList<String>(), recipientIds);

		_log.debug("Sending message notification to: " + recipientIds.toString());
		NotificationsManager nm = new ApplicationNotificationsManager(new SocialNetworkingSite(getThreadLocalRequest()), session.getScope(), new SocialNetworkingUser(
				session.getUsername(), session.getUserEmailAddress(), session.getUserFullName(), session.getUserAvatarId()));
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
	private long getCurrentGroupID() {
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		long toReturn = -1;
		try {
			toReturn = groupM.getGroupId(session.getGroupName());
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return toReturn;
	}

}
