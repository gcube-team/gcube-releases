package org.gcube.portal.custom.scopemanager.scopehelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.GenderType;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;

import static org.gcube.common.authorization.client.Constants.authorizationService;

public class ScopeHelper {	

	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String CURR_ORG = "CURR_RE_NAME";
	public static final String USER_FULL_NAME_ATTRIBUTE = "USER_FULL_NAME";
	public static final String USER_EMAIL_ATTRIBUTE = "USER_EMAIL";
	public static final String USER_AVATAR_ID_ATTRIBUTE = "USER_AVATAR_ID";
	public static final String USER_GENDER_ATTRIBUTE = "USER_GENDER";	

	public static final String ASSERTION_ID = "assertionID";	

	/**
	 * 
	 */
	public static final String ROOT_VO = "rootVO";

	public static final String CURR_RESEARCH_ENV = "CURR_RE_NAME";

	public static final String MAIL = "notificationSenderEmail";

	public static void setContext(RenderRequest request) {
		setContext(request, USERNAME_ATTRIBUTE);
	}
	private static Log _log = LogFactoryUtil.getLog(ScopeHelper.class);
	/**
	 * 
	 * @param session the session
	 * @return the singleton
	 */
	public static void setContext(RenderRequest request, String username_attr) {
		_log.info("SETTING CONTEXT .. ");
		String scopeToSet = "";
		long userid = Long.parseLong(request.getRemoteUser());
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		User user = null;
		GCubeGroup currentGroup = null;
		try {
			user = UserLocalServiceUtil.getUser(userid);

			GroupManager gm = new LiferayGroupManager();
			long groupId = themeDisplay.getLayout().getGroup().getGroupId();
			currentGroup = gm.getGroup(groupId);

			boolean isInfrastructureScope = false;
			if (gm.isRootVO(groupId)) {
				scopeToSet = "/"+PortalContext.getConfiguration().getInfrastructureName();
			}
			else if (gm.isVO(groupId) || gm.isVRE(groupId)) {
				scopeToSet = gm.getInfrastructureScope(groupId);
				isInfrastructureScope = true;
			} else { 
				scopeToSet = "PORTAL";
				_log.info("Not a VO or VRE, scopeToSet set to PORTAL");				
			}



			//get the username
			String username = user.getScreenName();
			String sessionID = request.getPortletSession().getId();

			_log.info("SETTING CONTEXT  ID: " + sessionID + "  - username: " + username);


			request.getPortletSession().setAttribute(username_attr, username, PortletSession.APPLICATION_SCOPE);
			request.getPortletSession().setAttribute(CURR_RESEARCH_ENV, currentGroup, PortletSession.APPLICATION_SCOPE);


			if (isInfrastructureScope) {
				SessionManager.getInstance().getASLSession(sessionID, username).setGroupModelInfos(currentGroup.getGroupName(), groupId);
				SessionManager.getInstance().getASLSession(sessionID, username).setScope(scopeToSet);	
				_log.info("CONTEXT INITIALIZED CORRECTLY SCOPE: " + scopeToSet);
			}
			else {
				scopeToSet = "/"+PortalContext.getConfiguration().getInfrastructureName();
				_log.info("CONTEXT INITIALIZED CORRECTLY OUTSIDE VREs, set rootvo as scope: " + scopeToSet);
				SessionManager.getInstance().getASLSession(sessionID, username).setScope(scopeToSet);	
			}
			//add the social information needed by apps
			String fullName = user.getFirstName() + " " + user.getLastName();
			String email = user.getEmailAddress();
			String thumbnailURL = themeDisplay.getUser().getPortraitURL(themeDisplay);
			boolean isMale = user.isMale();

			SessionManager.getInstance().getASLSession(sessionID, username).setUserFullName(fullName);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserEmailAddress(email);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserAvatarId(thumbnailURL);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserGender(isMale? GenderType.MALE : GenderType.FEMALE);

			setAuthorizationToken(SessionManager.getInstance().getASLSession(sessionID, username));
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	private final static String DEFAULT_ROLE = "OrganizationMember";

	private static void setAuthorizationToken(ASLSession session) throws Exception {
		String username = session.getUsername();
		String scope = session.getScope();
		ScopeProvider.instance.set(scope);
		_log.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		session.setSecurityToken(null);
		String token = authorizationService().generateUserToken(new UserInfo(session.getUsername(), userRoles), scope);
		
		_log.debug("received token: "+token);
		session.setSecurityToken(token);
		_log.info("Security token set in session for: "+username + " on " + scope);
	}
	/**
	 * read the root VO name from a property file and retuns it
	 */
	public static String getSupportMainlingListAddr() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = SiteManagerUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(MAIL);
			if (toReturn == null || toReturn.compareTo("")==0) {
				toReturn = "do-not-reply@isti.cnr.it";
				_log.error("Property "+MAIL+" in $CATALINA_HOME/conf/gcube-data.properties was not found, returning default support mailing address " + toReturn);
			} else
				_log.trace("Found Mail Support address: " + toReturn );
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "do-not-reply@isti.cnr.it";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default support mailing address " + toReturn);
			return toReturn;
		}
		return toReturn;
	}
	/**
	 * return the infrastructure name 
	 */
	public static String getRootConfigFromGCore() {
		return PortalContext.getConfiguration().getInfrastructureName();
	}

	/**
	 * return one of the Administrators Username from LiferayDB
	 */
	public static String getAdministratorUsername()  {

		String toReturn = "";

		try {
			Role adminRole = RoleLocalServiceUtil.getRole(SiteManagerUtil.getCompany().getCompanyId(),"Administrator");
			List<User> adminUsers = UserLocalServiceUtil.getRoleUsers(adminRole.getRoleId());
			if (adminUsers != null && adminUsers.size() > 0) {
				for (User user : adminUsers) {
					if (user.isActive()) {
						toReturn = adminUsers.get(0).getScreenName();
						break;
					}
				}				
			}
			else
				_log.warn("No users with Administrator Role in this portal!");
		}
		//catch exception in case properties file does not exist
		catch(Exception e) {
			toReturn = "Exception";
			_log.error("No users with Administrator Role in this portal, returning " + toReturn);
			e.printStackTrace();
			return toReturn;
		}
		_log.debug("Returning ADMIN username=" + toReturn );
		return toReturn;
	}

}
