package org.gcube.portlets.user.questions.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.questions.client.QuestionsService;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
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
			HashMap<UserModel, List<RoleModel>> usersAndRoles = null;
			try {
				usersAndRoles = userM.listUsersAndRolesByGroup(getCurrentGroupID());
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Set<UserModel> users = usersAndRoles.keySet();
			for (UserModel usr:users) {
				List<RoleModel> roles = usersAndRoles.get(usr);
				for (int i = 0; i < roles.size(); i++) {
					if (roles.get(i).getRoleName().equals("VRE-Manager")) {
						String username = usr.getScreenName();
						_log.trace("Found Manager ... " + username);
						String fullName = usr.getFullname();
						String thumbnailURL = "images/Avatar_default.png";
						try {
							com.liferay.portal.model.User user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
							thumbnailURL = user.isMale() ? "/image/user_male_portrait?img_id="+user.getPortraitId() : "/image/user_female_portrait?img_id="+user.getPortraitId();
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
	private String getCurrentGroupID() throws Exception {
		GroupManager groupM = new LiferayGroupManager();
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		try {
			return groupM.getGroupId(session.getGroupName());
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getUserProfileLink(String username) {
		return "profile?"+ new String(Base64.encodeBase64(GCubeSocialNetworking.USER_PROFILE_OID.getBytes()))+"="+new String(Base64.encodeBase64(username.getBytes()));
	}
	


}
