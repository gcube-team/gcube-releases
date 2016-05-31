package org.gcube.portlets.user.vremembers.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.user.vremembers.client.MembersService;
import org.gcube.portlets.user.vremembers.shared.BelongingUser;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * @author Massimiliano Assante, ISTI-CNR
 */
@SuppressWarnings("serial")
public class MembersServiceImpl extends RemoteServiceServlet implements MembersService {
	private static final Logger _log = LoggerFactory.getLogger(MembersServiceImpl.class);

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
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube");
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
		//user = "massimiliano.assante";
		return user;
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
	 * 
	 * @param session the Asl Session
	 * @param withinPortal true when is on Liferay portal
	 * @return the users belonging to the current organization (scope)
	 */
	@Override
	public ArrayList<BelongingUser> getOrganizationUsers() {
		ArrayList<BelongingUser> portalUsers = new ArrayList<BelongingUser>();
		String scope = getASLSession().getScope();
		if (scope == null)
			return portalUsers;
		try {
			if (isWithinPortal()) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				ScopeBean sb = new ScopeBean(scope);
				List<UserModel> users = null;

				if (sb.is(Type.INFRASTRUCTURE)) {
					users = new ArrayList<UserModel>();
					return new ArrayList<BelongingUser>();
				}
				else if (sb.is(Type.VRE)) { //must be in VRE
					//get the name from the scope
					String orgName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
					//ask the users
					users = um.listUsersByGroup(gm.getGroupId(orgName));
				}
				else {
					_log.error("Error, you must be in SCOPE VRE OR INFRASTURCTURE, you are in VO SCOPE returning no users");
					return portalUsers;
				}		
				for (UserModel user : users) {
					if (user.getScreenName().compareTo("test.user") != 0)  { //skip test.user
						String thumbnailURL = "";
						com.liferay.portal.model.User lifeUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), user.getScreenName());
						thumbnailURL = lifeUser.isMale() ? "/image/user_male_portrait?img_id="+lifeUser.getPortraitId() : "/image/user_female_portrait?img_id="+lifeUser.getPortraitId();
						portalUsers.add(new BelongingUser(user.getScreenName(), user.getFullname(), thumbnailURL, lifeUser.getJobTitle(), lifeUser.getOpenId(), getUserProfileLink(user.getScreenName() ),lifeUser.getPortraitId() != 0));
					}
				}

			}
			else { //test users
				portalUsers.add(new BelongingUser("massimiliano.assante", "Test User #1", "1111", "headline", "isti", "",false));
				portalUsers.add(new BelongingUser("pino.assante", "Test Second User #2", "1111", "headline1", "istitution complex", "",false));
				portalUsers.add(new BelongingUser("pino.pino", "With Photo Third User", "1111", "hard worker", "acme Ltd",  "",true));
				portalUsers.add(new BelongingUser("giorgi.giorgi", "Test Fourth User", "1111", "hard worker 3", "isti3",  "",false));
				portalUsers.add(new BelongingUser("pinetti.giorgi", "Test Fifth User", "1111", "hard worker 4", "super acme Inc.",  "",false));
				portalUsers.add(new BelongingUser("massimiliano.pinetti", "Test Sixth User", "1111", "hard worker the5th", "istiw", "", false));
				portalUsers.add(new BelongingUser("giorgi.assante", "Ninth Testing User", "1111", "hard worker the9th", "istiw9",  "",false));
				portalUsers.add(new BelongingUser("massimiliano.giorgi", "Eighth Testing User", "1111", "hard worker the8th", "istiw56", "", false));
				portalUsers.add(new BelongingUser("giogio.giorgi", "Seventh Test User", "1111", "hard worker the7th", "istiw7", "", false));
				portalUsers.add(new BelongingUser("pino.pinetti", "Tenth Testing User Photoed", "1111", "hard worker the10th", "istiw777",  "",true));
			}
		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		//users having photo go first
		Collections.sort(portalUsers);
		return portalUsers;
	}

	private String getUserProfileLink(String username) {
		return (username.compareTo(getASLSession().getUsername()) != 0) ?
			 		"profile?"+ new String(Base64.encodeBase64(GCubeSocialNetworking.USER_PROFILE_OID.getBytes()))+"="+new String(Base64.encodeBase64(username.getBytes()))
					: GCubeSocialNetworking.USER_PROFILE_LINK;
	}
	
	
	
}
