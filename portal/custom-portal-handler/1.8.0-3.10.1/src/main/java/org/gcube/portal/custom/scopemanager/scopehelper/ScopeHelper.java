package org.gcube.portal.custom.scopemanager.scopehelper;

import static org.gcube.common.authorization.client.Constants.authorizationService;

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
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;


public class ScopeHelper {	

	public static final String USERNAME_ATTRIBUTE = "username";

	public static final String USER_FULL_NAME_ATTRIBUTE = "USER_FULL_NAME";
	public static final String USER_EMAIL_ATTRIBUTE = "USER_EMAIL";
	public static final String USER_AVATAR_ID_ATTRIBUTE = "USER_AVATAR_ID";
	public static final String USER_GENDER_ATTRIBUTE = "USER_GENDER";	
	
	public static final String ASSERTION_ID = "assertionID";	
	
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";

	public static final String CURR_ORG = "CURR_RE_NAME";

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
		request.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay, PortletSession.APPLICATION_SCOPE);
		User user = null;
		Group currentGroup = null;
		Organization curOrg = null;

		try {
			user = UserLocalServiceUtil.getUser(userid);

			currentGroup = GroupLocalServiceUtil.getGroup(themeDisplay.getLayout().getGroup().getGroupId());

			// the group MUST BE an Organization
			if (currentGroup.isOrganization()) {
				long organizationId = currentGroup.getClassPK();
				curOrg = OrganizationLocalServiceUtil.getOrganization(organizationId);

				if (curOrg.isRoot()) {
					scopeToSet = "/"+curOrg.getName();
				} else if (isVO(curOrg)) {
					scopeToSet = "/"+curOrg.getParentOrganization().getName()+"/"+curOrg.getName();
				} else { //is a VRE
					Organization vo = curOrg.getParentOrganization();
					scopeToSet = "/"+vo.getParentOrganization().getName()+"/"+vo.getName()+"/"+curOrg.getName();
				}
			} else { //
				scopeToSet = "PORTAL";
				_log.info("Not an organization, scopeToSet set to PORTAL");				
			}



			//get the username
			String username = user.getScreenName();
			String sessionID = request.getPortletSession().getId();

			_log.info("SETTING CONTEXT  ID: " + sessionID + "  - username: " + username);


			request.getPortletSession().setAttribute(username_attr, username, PortletSession.APPLICATION_SCOPE);
			request.getPortletSession().setAttribute(CURR_ORG, curOrg, PortletSession.APPLICATION_SCOPE);


			if (curOrg != null) {
				SessionManager.getInstance().getASLSession(sessionID, username).setGroupModelInfos(curOrg.getName(), curOrg.getOrganizationId());
				SessionManager.getInstance().getASLSession(sessionID, username).setScope(scopeToSet);	
				_log.info("CONTEXT INITIALIZED CORRECTLY SCOPE: " + scopeToSet);
			}
			else {
				String rootVO = getRootConfigFromGCore();
				_log.info("CONTEXT INITIALIZED CORRECTLY OUTSIDE VREs, setting rootvo as scope: " + rootVO);
				scopeToSet = "/"+rootVO;
				SessionManager.getInstance().getASLSession(sessionID, username).setScope(scopeToSet);	
			}
			//add the social information needed by apps
			String fullName = user.getFirstName() + " " + user.getLastName();
			String email = user.getEmailAddress();
			String thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
			boolean isMale = user.isMale();

			SessionManager.getInstance().getASLSession(sessionID, username).setUserFullName(fullName);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserEmailAddress(email);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserAvatarId(thumbnailURL);
			SessionManager.getInstance().getASLSession(sessionID, username).setUserGender(isMale? GenderType.MALE : GenderType.FEMALE);
	
			setAuthorizationToken(SessionManager.getInstance().getASLSession(sessionID, username));
			
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}	

	}
	private final static String DEFAULT_ROLE = "OrganizationMember";
	private final static String TEST_USER = "test.user";
	
	private static void setAuthorizationToken(ASLSession session) {
		
		String username = session.getUsername();
		_log.debug("Generating token");
		if (username.compareTo(TEST_USER) == 0)
			return;
		String scope = session.getScope();
		ScopeProvider.instance.set(scope);
		_log.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		if (username.compareTo("lucio.lelii")==0)
			userRoles.add("VRE-Manager");
		session.setSecurityToken(null);
		String token = authorizationService().build().generate(session.getUsername(), userRoles);
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
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
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
	 * 
	 * @param currentGroup
	 * @return true id the organization is a VO
	 * @throws SystemException 
	 * @throws PortalException 
	 * @throws SystemException .
	 * @throws PortalException .
	 */
	private static boolean isVO(Organization currentOrg) throws PortalException, SystemException {		
		return (currentOrg.getParentOrganization().getParentOrganization() == null); 
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
			Role adminRole = RoleLocalServiceUtil.getRole(OrganizationsUtil.getCompany().getCompanyId(),"Administrator");
			List<User> adminUsers = UserLocalServiceUtil.getRoleUsers(adminRole.getRoleId());
			if (adminUsers != null && adminUsers.size() > 0)
				toReturn = adminUsers.get(0).getScreenName();
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
