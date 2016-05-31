package org.gcube.portlets.user.gcubeloggedin.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.gcubeloggedin.client.LoggedinService;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject.UserBelongingClient;
import org.gcube.portlets.user.gcubeloggedin.shared.VREClient;
import org.gcube.portlets.user.gcubewidgets.server.ScopeServiceImpl;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoggedinServiceImpl extends RemoteServiceServlet implements LoggedinService {

	private static final Logger _log = LoggerFactory.getLogger(LoggedinServiceImpl.class);
	private static final String MANDATORY_GROUP = "Mandatory";
	private static final String REQUEST_BASED_GROUP = "Requestbasedgroup";

	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("LOGGEDIN PORTLET: USER IS NULL \n\n SESSION ID READ: " +sessionID );
			user = "test.user";
			_log.warn("session ID= *" + sessionID + "*  user= *" + user + "*" );		

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
	/**
	 * return the current selected VRE
	 */

	public VObject getSelectedRE(String portalURL) {
		ASLSession aslSession = getASLSession();
		String username = aslSession.getUsername();
		String friendlyURL = ScopeServiceImpl.extractOrgFriendlyURL(portalURL);

		if (friendlyURL == null) {//the URL is not a portal URL, we are in devmode.
			return new VREClient("Test", "", "" +
					"Fishery and Aquaculture Resources Management (FARM) Virtual Organisation</b>    The FARM Virtual Organisation is the <b><i>dynamic group of individuals</i></b> and/or <b><i>institutions</i></b>             defined around a set of <b><i>sharing rules</i></b> in which <b><i>resource providers</i></b> and <b><i>consumers</i></b>     specify clearly and carefully just what is shared, who is allowed to share, and the conditions under which sharing occurs to serve the needs of the     <b><i>Fisheries and Aquaculture Resources Management</i></b>.             This VO is conceived to support various application scenarios arising in the FARM Community including the production of Fisheries and Aquaculture Country Profiles, the management of catch statistics    including harmonisation, the dynamic generation of biodiversity maps and species distribution maps.            This Virtual Organisation currently consists of:<ul>                <li> approximately <b><i>13 gCube nodes</i></b>, i.e. machines dedicated to run the gCube system;</li>        <li> approximately <b><i>89 running instances</i></b>, i.e. running gCube services supporting the operation of the infrastructure;</li>        <li> approximately <b><i>25 collections</i></b>, i.e. set of D4Science Information Objects including Earth images, AquaMaps, Graphs on catch statistics;</li>        <li> approximately <b><i>66 metadata collections</i></b>, i.e. set of Metadata Objects describing the Information Objects through various features and schemas;</li>        <li> approximately <b><i>58 other resources</i></b> including transformation programs, index types, etc.</li></ul></div>" +
					"", "", "", UserBelongingClient.BELONGING, false, true);
		}
		_log.trace("getting Selected Research Environment");
		Organization currOrg = null;
		try {
			List<Group> groups = GroupLocalServiceUtil.getGroups(0, GroupLocalServiceUtil.getGroupsCount());
			for (Group g : groups) {
				if (g.isOrganization() || g.isCommunity()) 
					if (g.getFriendlyURL().compareTo(friendlyURL) == 0) {
						long organizationId = g.getClassPK();
						currOrg = OrganizationLocalServiceUtil.getOrganization(organizationId);
						String scopeToSet = ScopeServiceImpl.buildScope(g);
						getASLSession().setScope(scopeToSet);
						_log.info("GOT Selected Research Environment: " + scopeToSet);
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * set the current ORG bean in session
		 */
		aslSession.setAttribute(ScopeHelper.CURR_ORG, currOrg);

		aslSession.setGroupModelInfos(currOrg.getName(), currOrg.getOrganizationId());

		_log.trace("CURRENT ORG SET IN SESSION: " + currOrg.getName());


		String name = currOrg.getName();
		long logoId = currOrg.getLogoId();
		ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);		
		String logoURL =  themeDisplay.getPathImage()+"/organization_logo?img_id="+ logoId +"&t" + ImageServletTokenUtil.getToken(logoId);
		String desc = "";
		//set the description for the vre
		if (currOrg.getComments() != null)	
			desc = currOrg.getComments();
		VREClient vre = new VREClient(name, "", desc, logoURL, "", UserBelongingClient.BELONGING, isEnabled(username, currOrg, MANDATORY_GROUP), isEnabled(username, currOrg, REQUEST_BASED_GROUP));
		return vre;
	}


	private Boolean isEnabled(String username, Organization currOrg, String attrToCheck) {
		Boolean isEnabled = false;
		if (username.compareTo("test.user") == 0) {
			_log.warn("Found test.user maybe you are in dev mode, returning ... ");
			return true;
		}
		try {

			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			User currentUser = OrganizationsUtil.validateUser(username);
			if (currOrg.getExpandoBridge().getAttribute(attrToCheck) == null || currOrg.getExpandoBridge().getAttribute(attrToCheck).equals("")) {
				_log.trace("Attribute " + attrToCheck + " must be initialized");
				setOrgCustomAttribute(username, currOrg, attrToCheck);
				isEnabled = true;
			}
			else {
				String currVal = (String) currOrg.getExpandoBridge().getAttribute(attrToCheck);
				isEnabled = (currVal.compareTo("true") == 0);
			}

			_log.trace("Setting Thread Permission back to regular");			
			permissionChecker = PermissionCheckerFactoryUtil.create(currentUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok! returning ...");
			System.out.println(" returning *********** isEnabled=" + isEnabled);			
			return isEnabled;
		} catch (BeanLocatorException ex) {
			ex.printStackTrace();
			_log.warn("Could not read the property " + attrToCheck + " from LR DB, maybe you are in dev mode, returning true");
			return true;
		}
		catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * @param username
	 * @param attribute2Set
	 */
	private void setOrgCustomAttribute(String username,  Organization currOrg, String attribute2Set) {
		User currUser = null;
		if (username.compareTo("test.user") == 0) {
			_log.warn("Found Test User, returning ... ");
			return;
		}
		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			_log.debug("Creating and Setting custom attribute for colName " + attribute2Set + " to " +true);
			//add the custom attrs
			currUser = UserLocalServiceUtil.getUserByScreenName(companyId, username);

			if (! currOrg.getExpandoBridge().hasAttribute(attribute2Set)) 	
				currOrg.getExpandoBridge().addAttribute(attribute2Set);

			currOrg.getExpandoBridge().setAttribute(attribute2Set, "true");
			_log.trace("setAttribute true");


			_log.trace("Setting Thread Permission back to regular");
			permissionChecker = PermissionCheckerFactoryUtil.create(currUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * The Default Community is a community where all portal user belong to
	 * @return the default community URL
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public String getDefaultCommunityURL() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String comName = "";

		try {
			String propertyfile = this.getServletContext().getRealPath("")+"/config/resources.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			comName = props.getProperty("defaultcommunity");
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.error("/config/resources.properties not found, Returning \"Data e-Infrastructure gateway\" as default Community");
		}

		String toReturn = "";
		_log.trace("Default Community Read from config file: " + comName);
		Group myPlace = null;
		try {
			myPlace = GroupLocalServiceUtil.getGroup(OrganizationsUtil.getCompany().getCompanyId(), comName);

			if(myPlace.isCommunity()) {			 
				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
				toReturn = myPlace.getPathFriendlyURL(true, themeDisplay) + myPlace.getFriendlyURL();		     
			}
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		toReturn = getPortalBasicUrl()+toReturn;
		_log.trace("RETURNING Default Community URL: " + toReturn);
		return toReturn;
	}


	/**
	 * 
	 * @return the portal basic url, e.g. http://www.foo.com
	 */
	private String getPortalBasicUrl() {
		HttpServletRequest request = this.getThreadLocalRequest();
		String toReturn = "";
		//protocol
		String protocol = (request.isSecure()) ? "https://" : "http://" ;
		toReturn += protocol;
		//server name
		toReturn += request.getServerName();
		//port
		toReturn +=  (request.getServerPort() == 80) ? "" : ":"+request.getServerPort() ;

		request.getQueryString();

		_log.trace("\n getPortalBasicUrl: " +toReturn + "queryString: " +	request.getQueryString());
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
			removeUserFromHLGroup(username, getASLSession().getScope());
			sendUserUnregisteredNotification(username, getASLSession().getScope(), getPortalBasicUrl(), readGatewayName());
			return getDefaultCommunityURL();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	/**
	 * Get the current group ID
	 * 
	 * @return the current group ID or null if an exception is thrown
	 * @throws Exception 
	 */
	private String getCurrentGroupID(){
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		try {
			try {
				GroupManager groupM = new LiferayGroupManager();
				return groupM.getGroupId(session.getGroupName());
			} catch (UserManagementSystemException e) {
				throw new Exception(e.getMessage(), e.getCause());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void removeUserFromHLGroup(String username, String group) {
		try {
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			um.removeUserFromGroup(group, username, getASLSession().getUsername());
		} catch (InternalErrorException e) {
			_log.error("Failed to get the usermanager from HL. Could not add remove user from the HL group");
		} catch (ItemNotFoundException e1) {

		}
	}

	protected static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		String groupId = null;
		try {
			List<org.gcube.vomanagement.usermanagement.model.GroupModel> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				String grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getScope(grId);
				System.out.println("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		HashMap<UserModel, List<RoleModel>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<UserModel> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (UserModel usr:users) {
			List<RoleModel> roles = usersAndRoles.get(usr);
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
	public static void sendUserUnregisteredNotification(String username, String scope, String portalbasicurl, String gatewayName) {
		ArrayList<String> adminEmails = getAdministratorsEmails(scope);

		User currUser = null;
		try {
			currUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has left to the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmailAddress());
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
				" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
				"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");

		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] -  unregistration from VRE", body.toString());

		mailToAdmin.sendEmail();
	}


	private String readGatewayName()  {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String gatewayLabel = "gCube Gateway";

		try {
			String propertyfile = System.getenv("CATALINA_HOME")+"/conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			//set the gateway label in the session
			gatewayLabel = props.getProperty("portalinstancename");
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.warn("$CATALINA_HOME/conf/gcube-data.properties not found, Returning gateway name: " + gatewayLabel);
		}

		return gatewayLabel;
	}
}
