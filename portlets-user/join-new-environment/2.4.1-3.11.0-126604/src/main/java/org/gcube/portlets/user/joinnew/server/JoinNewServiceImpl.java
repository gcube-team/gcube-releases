package org.gcube.portlets.user.joinnew.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portlets.user.joinnew.client.JoinNewService;
import org.gcube.portlets.user.joinnew.server.portlet.LoginPortlet;
import org.gcube.portlets.user.joinnew.shared.UserBelonging;
import org.gcube.portlets.user.joinnew.shared.VO;
import org.gcube.portlets.user.joinnew.shared.VRE;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class JoinNewServiceImpl extends RemoteServiceServlet implements JoinNewService {

	/**
	 * 
	 */
	public static final String CACHED_VOS = "CACHED_VOS";

	private static final String REQUEST_BASED_GROUP = "Requestbasedgroup";
	private static final String IS_EXTERNAL = "Isexternal";

	private static DatabookStore store;
	/**
	 * 
	 */
	public String SELECTED_THEMEID = "";

	private VO rootVO = new VO();

	private static Logger _log = LoggerFactory.getLogger(JoinNewServiceImpl.class);	

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
	/**
	 * the user to the VRE and in the HL Group, plus send notifications to the vre manages of the vre
	 */
	@Override
	public boolean registerUser(String scope, long organizationid) {
		UserManager userM = new LiferayUserManager();
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			//add the user to the VRE
			userM.assignUserToGroup(""+organizationid, userM.getUserId(username));
			//add him to the HL			
			addUserToHLGroup(username, scope, session.getUsername());

			String gatewayName = "D4Science Gateway";
			if (this.getThreadLocalRequest().getSession().getAttribute(LoginPortlet.GATEWAY_NAME) != null) {
				gatewayName = this.getThreadLocalRequest().getSession().getAttribute(LoginPortlet.GATEWAY_NAME).toString();
				_log.debug("Gateway Label was Found="+gatewayName);
			} else
				_log.debug("Gateway Label Not Found");

			initStore();
			String inviteId = store.isExistingInvite(scope, session.getUserEmailAddress());
			if (inviteId != null) {
				Invite invite = store.readInvite(inviteId);
				store.setInviteStatus(scope, session.getUserEmailAddress(), InviteStatus.ACCEPTED);
				LoginServiceUtil.notifyUserAcceptedInvite(username, rootVO, scope, getPortalBasicUrl(), gatewayName, invite);
			}
			else {			
				LoginServiceUtil.notifyUserSelfRegistration(username, rootVO, scope, getPortalBasicUrl(), gatewayName);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * first method called by the UI
	 */
	public ArrayList<VO> getInfrastructureVOs() {	
		_log.trace("getInfrastructureVOs method called");
		if (! isWithinPortal()) {
			//return new ArrayList<VO>();
			return LoginServiceUtil.getFakeVOs();
		}
		else {

			try {
				User currUser = OrganizationsUtil.validateUser(getASLSession().getUsername());
				List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());

				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);

				Organization rootOrganization = null;
				for (Organization organization : organizations) {
					if (organization.getName().equals( LoginServiceUtil.getRootOrganizationName() ) ) {
						rootOrganization = organization;
						break;
					}
				}		
				try {
					_log.info("root: " + rootOrganization.getName() );
				}
				catch (NullPointerException e) {
					_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
					return new ArrayList<VO>();
				}
				// Create the list of the Infrastructure VOs
				List<VO> infrastructureVOs = new ArrayList<VO>();

				//create and check the root VO
				rootVO = new VO();
				rootVO.setName(rootOrganization.getName());
				rootVO.setGroupName("/"+rootOrganization.getName());
				rootVO.setRoot(true);	

				Group orgGroup = rootOrganization.getGroup();
				String friendlyURL = orgGroup.getPathFriendlyURL(true, themeDisplay) + orgGroup.getFriendlyURL();

				rootVO.setFriendlyURL(getPortalBasicUrl()+friendlyURL);
				long logoId = rootOrganization.getLogoId();
				String logoURL =  themeDisplay.getPathImage()+"/organization_logo?img_id="+ logoId +"&t" + ImageServletTokenUtil.getToken(logoId);
				rootVO.setImageURL(logoURL);


				if (rootOrganization.getComments() != null)
					rootVO.setDescription(rootOrganization.getComments());
				if (hasRole(getVoAdminRoleName(), rootOrganization.getName(), currUser))
					rootVO.setUserBelonging(UserBelonging.BELONGING);
				else if (LoginServiceUtil.checkPending(currUser.getScreenName(), rootOrganization.getOrganizationId()))
					rootVO.setUserBelonging(UserBelonging.PENDING);
				else
					rootVO.setUserBelonging(UserBelonging.NOT_BELONGING);

				//for each root sub organizations (VO)
				for (Organization vOrg : rootOrganization.getSuborganizations()) {
					_log.debug("FOUND VO: " + vOrg.getName() );
					//create the VO
					VO voToAdd = new VO();
					voToAdd.setName(vOrg.getName());
					voToAdd.setGroupName("/"+vOrg.getParentOrganization().getName()+"/"+vOrg.getName());
					voToAdd.setRoot(false);	

					for (Organization vre : vOrg.getSuborganizations()) {
						if (!isExternal(vre)) {
							VRE vreToAdd = new VRE();
							vreToAdd.setId(vre.getOrganizationId());
							vreToAdd.setName(vre.getName());
							vreToAdd.setGroupName("/"+vOrg.getParentOrganization().getName()+"/"+vOrg.getName()+"/"+vre.getName());

							logoId = vre.getLogoId();
							logoURL =  themeDisplay.getPathImage()+"/organization_logo?img_id="+ logoId +"&t" + ImageServletTokenUtil.getToken(logoId);
							vreToAdd.setImageURL(logoURL);

							String vreUrl = vre.getGroup().getPathFriendlyURL(true, themeDisplay) + vre.getGroup().getFriendlyURL();
							vreToAdd.setFriendlyURL(getPortalBasicUrl()+vreUrl);

							//set the description for the vre
							if (vre.getComments() != null)	{
								vreToAdd.setDescription(vre.getComments());
							}

							//as default all require access grant
							vreToAdd.setUponRequest(true);
							//check if the user belongs to it
							if (currUser.getOrganizations().contains(vre)) {
								vreToAdd.setUserBelonging(UserBelonging.BELONGING);
							}
							else if (LoginServiceUtil.checkPending(currUser.getScreenName(), vre.getOrganizationId()))
								vreToAdd.setUserBelonging(UserBelonging.PENDING);
							else {
								vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
								boolean requireAccessGrant = LoginServiceUtil.isEnabled(currUser.getScreenName(), vre, REQUEST_BASED_GROUP);
								vreToAdd.setUponRequest(requireAccessGrant);
							}

							if (! currUser.getOrganizations().contains(vre))
								voToAdd.addVRE(vreToAdd);
						}
					}



					Group group = vOrg.getGroup();
					String url = group.getPathFriendlyURL(true, themeDisplay) + group.getFriendlyURL();
					voToAdd.setFriendlyURL(getPortalBasicUrl() + url);

					//set the description for the vre
					if (vOrg.getComments() != null)	voToAdd.setDescription(vOrg.getComments());
					//check if the user belongs to it
					if (currUser.getOrganizations().contains(vOrg)) {
						voToAdd.setUserBelonging(UserBelonging.BELONGING);
					}
					else if (LoginServiceUtil.checkPending(currUser.getScreenName(), vOrg.getOrganizationId()))
						voToAdd.setUserBelonging(UserBelonging.PENDING);
					else
						voToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);

					infrastructureVOs.add(voToAdd);

				}

				ArrayList<VO> toReturn = new ArrayList<VO>();


				for (VO vo : infrastructureVOs) {
					for (VRE vre : vo.getVres()) {
						_log.debug("VRE FOUND.... " + vre.getName());
					}
					toReturn.add(vo);
				}			
				//sort the VOs
				Collections.sort(toReturn, Collections.reverseOrder()); 

				//set the root vo as FIRST
				toReturn.add(0, rootVO);

				ArrayList<VO> toStoreInSession = toReturn;			
				_log.debug("SETTING INFRASTRUCTURE VOS in ASLSession");
				getASLSession().setAttribute(CACHED_VOS, toStoreInSession);

				return toReturn;

			} 
			catch (Exception e) {			
				e.printStackTrace();
			} 
			return new ArrayList<VO>();
		}
	}

	/**
	 * addMembershipRequest
	 */
	public void addMembershipRequest(String scope, String optionalMessage) {
		String username = getASLSession().getUsername();

		String gatewayName = "D4Science Gateway";
		if (this.getThreadLocalRequest().getSession().getAttribute(LoginPortlet.GATEWAY_NAME) != null) {
			gatewayName = this.getThreadLocalRequest().getSession().getAttribute(LoginPortlet.GATEWAY_NAME).toString();
			_log.debug("Gateway Label was Found="+gatewayName);
		} else
			_log.debug("Gateway Label Not Found");
		LoginServiceUtil.addMembershipRequest(username, rootVO, scope, optionalMessage, getPortalBasicUrl(), gatewayName);
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
		//_log.trace("getPortalBasicUrl: " +toReturn + "request.getServerPort: " +	request.getServerPort());
		return toReturn;
	}

	public boolean isExternal(Organization organization) throws PortalException, SystemException {

		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			if (organization.getExpandoBridge().getAttribute(IS_EXTERNAL) == null || organization.getExpandoBridge().getAttribute(IS_EXTERNAL).equals("")) {
				_log.trace(String.format("Attribute %s not initialized. In this case by default we assume it is an internal VRE", IS_EXTERNAL));
				return false;
			} else {
				Boolean attributeValue = (Boolean) organization.getExpandoBridge().getAttribute(IS_EXTERNAL);
				return attributeValue;

			}
		} catch (Exception e) {
			_log.error("Something went wrong when trying to read VRE Custom Attr, " + e);
			return false;
		}	
	}


	/**
	 * return the root org
	 */
	public VO getRootVO() {
		//_log.debug("root called");
		getASLSession().invalidate();

		if (rootVO != null) return rootVO;
		else {
			Organization rootOrganization = null;
			List<Organization> organizations = null;
			try {
				organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			for (Organization organization : organizations) {
				if (organization.getName().equals( LoginServiceUtil.getRootOrganizationName() ) ) {
					rootOrganization = organization;
					break;
				}
			}		
			rootVO.setName(rootOrganization.getName());
			rootVO.setGroupName("/"+rootOrganization.getName());
			rootVO.setRoot(true);	

			ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);

			Group orgGroup = rootOrganization.getGroup();
			String friendlyURL = orgGroup.getPathFriendlyURL(true, themeDisplay) + orgGroup.getFriendlyURL();
			rootVO.setFriendlyURL(getPortalBasicUrl()+friendlyURL);
			return rootVO;
		}
	}
	/**
	 * return the selected VRE "from outside" with information about the user
	 */
	public VRE getSelectedVRE(long organizationId) {
		_log.info("*getting Selected Research Environment from referral, org id = " + organizationId);
		ASLSession session = getASLSession();
		try {
			User currUser = OrganizationsUtil.validateUser(session.getUsername());
			long[] userOrgs = currUser.getOrganizationIds(); 
			_log.debug("Scanning " + currUser.getScreenName() + " organizations ids ... ");
			for (int i = 0; i < userOrgs.length; i++) {
				System.out.println("FoundOrgId="+userOrgs[i]);
			}

			UserBelonging belonging = UserBelonging.NOT_BELONGING;
			for (int i = 0; i < userOrgs.length; i++) {
				if (userOrgs[i] == organizationId) {
					belonging = UserBelonging.BELONGING;
				}		
			}		
			_log.debug("* * GetOrganizaion By Id() " + organizationId);
			Organization org = OrganizationLocalServiceUtil.getOrganization(organizationId);
			VRE toReturn =  null;
			if (org != null) {
				String scope = "/"+PortalContext.getConfiguration().getInfrastructureName()+"/"+org.getParentOrganization().getName()+"/"+org.getName();
				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
				Group orgGroup = org.getGroup();
				String friendlyURL = orgGroup.getPathFriendlyURL(true, themeDisplay) + orgGroup.getFriendlyURL();
				boolean requireAccessGrant = LoginServiceUtil.isEnabled(currUser.getScreenName(), org, REQUEST_BASED_GROUP);
				toReturn =  new VRE(org.getOrganizationId(), org.getName(), "", "", scope, friendlyURL, belonging, requireAccessGrant);
				_log.debug("* * Received referral, returning " + toReturn.toString());
			}
			return toReturn;

		} catch (PortalException | SystemException e) {
			_log.error("Something wrong happened while trying to getOrganization, probably the organization id is wrong. " + e.getMessage());
		}
		return null;
	}

	@Override
	public String isExistingInvite(long organizationid) {
		VRE vre = getSelectedVRE(organizationid);
		String email = getASLSession().getUserEmailAddress();
		_log.debug("checking if invite exists for " + email + " on " + vre.getGroupName());
		initStore();
		return store.isExistingInvite(vre.getGroupName(), email);
	}	

	/**
	 * 
	 * @return
	 */
	private String getVoAdminRoleName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty("voadminRole");
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * 
	 * @param rolename
	 * @param organizationName
	 * @param user
	 * @return
	 * @throws SystemException 
	 */
	private boolean hasRole(String rolename, String organizationName, User user) throws SystemException {
		for (Role role : user.getRoles()) {
			//_log.trace("COMPARING ROLE: " +role.getName() + " -> " + rolename + "-" + organizationName);
			if (role.getName().compareTo( rolename + "-" + organizationName) == 0 ) 
				return true;
		}
		return false;
	}


	/**
	 * the first method to call
	 */
	public Boolean isUserRegistered() {
		getASLSession();
		return new Boolean(false);
	}
	/**
	 * 
	 * @param scope
	 */
	public void loadLayout(String scope, String URL) {
		_log.trace("Calling Load Layout...");
		HttpSession session = this.getThreadLocalRequest().getSession();
		ASLSession mysession = SessionManager.getInstance().getASLSession(session.getId(), session.getAttribute("username").toString());
		mysession.setAttribute("loadlayout", "true");
		session.setAttribute("loadLayout", "true");
		session.setAttribute("selectedVRE", scope);
		mysession.logUserLogin(scope);
		mysession.setScope(scope);

		_log.trace("User login logged to: " + scope);
	}


	private void addUserToHLGroup(String username, String group, String adminUsername) {
		try {
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			um.associateUserToGroup(group, username, adminUsername);
		} catch (Exception e) {
			_log.error("Failed to get the usermanager from HL. Could not add user to the HL group");
		}
	}
	/**
	 * TODO: Look which portlets are in the current VRE layout and create a List of names with them
	 * @param vre
	 */
	private void setVREBelonginApplication(VRE vre) {
		_log.info("AvailablePortlets stored in session");
		ArrayList<String> toSet = new ArrayList<String>();
		toSet.add("AnnotationFrontEnd_V2");
		getASLSession().setAttribute("availablePortlets", toSet);
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
}
