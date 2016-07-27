package org.gcube.portlets.user.questions.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.gcubewidgets.server.ScopeServiceImpl;
import org.gcube.portlets.user.questions.client.QuestionsService;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * 
 * @author massi
 *
 */
@SuppressWarnings("serial")
public class QuestionsServiceImpl extends RemoteServiceServlet implements QuestionsService {
	private static final Logger _log = LoggerFactory.getLogger(QuestionsServiceImpl.class);
	private static final String TEST_USER = "test.user";
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = TEST_USER;
		user = "massimiliano.assante";
		return user;
	}

	@Override
	public ArrayList<UserInfo> getManagers() {
		ArrayList<UserInfo> toReturn = new ArrayList<>();	
		ASLSession session = getASLSession();
		if (session.getUsername().compareTo(TEST_USER) == 0) {
			_log.error("User is NULL, session expired?");
			return new ArrayList<>();
		}
		if (isWithinPortal()) {
			_log.trace("Asking user and roles ...");
			UserManager userM = new LiferayUserManager();
			Map<GCubeUser, List<GCubeRole>> usersAndRoles = null;
			try {
				usersAndRoles = userM.listUsersAndRolesByGroup(getCurrentGroupID());
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Set<GCubeUser> users = usersAndRoles.keySet();
			for (GCubeUser usr:users) {
				List<GCubeRole> roles = usersAndRoles.get(usr);
				for (int i = 0; i < roles.size(); i++) {
					if (roles.get(i).getRoleName().equals("VRE-Manager")) {
						String username = usr.getUsername();
						_log.trace("Found Manager ... " + username);
						String fullName = usr.getFullname();
						String thumbnailURL = "images/Avatar_default.png";
						try {
							GCubeUser user = userM.getUserByUsername(username);
							thumbnailURL = user.getUserAvatarURL();
							HashMap<String, String> vreNames = new HashMap<String, String>();
							String headline = user.getJobTitle();
							UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, headline, getUserProfileLink(username), user.isMale(), false, vreNames);
							toReturn.add(userInfo);

						} catch (Exception e) {
							e.printStackTrace();
						} 

					}
				}
			}
		} else {
			toReturn.add(new UserInfo("pino.pino", "With Photo Third User", null, "email@email.it", "", true, false, null)); 
			toReturn.add(new UserInfo("giorgi.giorgi", "Test Fourth User", null, "email@email.it", "", true, false, null)); 
			toReturn.add(new UserInfo("pinetti.giorgi", "Test Fifth User", null, "email@email.it", "", true, false, null)); 
			toReturn.add(new UserInfo("massimiliano.pinetti", "Test Sixth User", null, "email@email.it", "", true, false, null)); 
			toReturn.add(new UserInfo("massimiliano.giorgi", "Eighth Testing User", null, "email@email.it", "", true, false, null)); 
		}		
		return toReturn;
	}

	/**
	 *@return the redirect url if everything goes ok, null otherwise
	 */
	@Override
	public String removeUserFromVRE() {
		String username = getASLSession().getUsername();
		if (username.compareTo("test.user") == 0)
			return null;
		_log.debug("Going to remove user from the current Group: " + getCurrentGroupID() + ". Username is: " + username);
		UserManager userM = new LiferayUserManager();
		try {
			userM.dismissUserFromGroup(getCurrentGroupID(), userM.getUserId(username));
			sendUserUnregisteredNotification(username, getASLSession().getScope(), 
					PortalContext.getConfiguration().getGatewayURL(getThreadLocalRequest()),
					PortalContext.getConfiguration().getGatewayName(getThreadLocalRequest()));
			return "/";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	@Override
	public boolean isLeaveButtonAvailable(String portalURL) {

		if(isWithinPortal()){
			String friendlyURL = ScopeServiceImpl.extractOrgFriendlyURL(portalURL);
			GroupManager gm = new LiferayGroupManager();
			GCubeGroup currSite = null;
			try {			
				List<GCubeGroup> groups = gm.listGroups();
				for (GCubeGroup g : groups) {
					if (g.getFriendlyURL().compareTo(friendlyURL) == 0) {
						long groupId = g.getGroupId();		
						String scopeToSet = gm.getInfrastructureScope(groupId);
						getASLSession().setScope(scopeToSet);
						_log.info("GOT Selected Research Environment: " + scopeToSet);
						currSite = g;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Boolean isMandatory = false;
			try{
				isMandatory = (Boolean) gm.readCustomAttr(currSite.getGroupId(), org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys.MANDATORY.getKeyName());
			}catch(Exception e){
				_log.error("Unable to evaluate if the leave button can be added for the current group " + currSite.getGroupName(), e);
			}
			_log.debug("Is Leave button available in vre " + currSite.getGroupName() + " ? " + isMandatory);
			return !isMandatory;
		}else return true;
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
	/**
	 * Get the current group ID
	 * 
	 * @return the current group ID or null if an exception is thrown
	 * @throws Exception 
	 * @throws CurrentGroupRetrievalException 
	 */
	private long getCurrentGroupID(){
		GroupManager groupM = new LiferayGroupManager();
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		try {
			return groupM.getGroupId(session.getGroupName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private String getUserProfileLink(String username) {
		return "profile?"+ new String(Base64.encodeBase64(GCubeSocialNetworking.USER_PROFILE_OID.getBytes()))+"="+new String(Base64.encodeBase64(username.getBytes()));
	}

	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public void sendUserUnregisteredNotification(String username, String scope, String portalbasicurl, String gatewayName) {
		ArrayList<String> adminEmails = getAdministratorsEmails(scope);
		UserManager um = new LiferayUserManager();
		GCubeUser currUser = null;
		try {
			currUser = um.getUserByUsername(username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has left the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmail());
		body.append("</p>");

		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification(allMails , "Unregistration from VRE", body.toString(), getThreadLocalRequest());

		mailToAdmin.sendEmail();
	}

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
				_log.debug("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (Exception e) {
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
