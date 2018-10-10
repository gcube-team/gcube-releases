package org.gcube.portlets.user.gcubelogin.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.communitymanager.ThemesIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.impl.GCubeSiteManagerImpl;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginService;
import org.gcube.portlets.user.gcubelogin.server.portlet.LoginPortlet;
import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.ResearchEnvironment;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 */
@SuppressWarnings("serial")
public class LoginServiceImpl  extends RemoteServiceServlet implements NewLoginService {
	/**
	 * 
	 */
	public static final String CACHED_VOS = "CACHED_VOS";
	/**
	 * 
	 */
	public String SELECTED_THEMEID = "";

	private VO rootVO = new VO();
	private static final Logger _log = LoggerFactory.getLogger(LoginServiceImpl.class);

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute("username");
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
	 * first method called by the UI
	 */
	public ArrayList<VO> getInfrastructureVOs() {	
		ASLSession session = getASLSession();
		_log.trace("getInfrastructureVOs method called");
		if (!isWithinPortal()) {
			return null;
			//return LoginServiceUtil.getFakeVOs();
		}
		else {
			try {
				User currUser = SiteManagerUtil.validateUser(getASLSession().getUsername());
				GroupManager gm = new LiferayGroupManager();
				GCubeGroup rootGroupVO = gm.getRootVO();
			
	
				try {
					_log.info("root: " + rootGroupVO.getGroupName());
				}
				catch (NullPointerException e) {
					_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
					return new ArrayList<VO>();
				}
				// Create the list of the Infrastructure VOs
				List<VO> infrastructureVOs = new ArrayList<VO>();

				//create and check the root VO
				rootVO = new VO();
				rootVO.setName(rootGroupVO.getGroupName());
				rootVO.setGroupName(gm.getInfrastructureScope(rootGroupVO.getGroupId()));
				rootVO.setRoot(true);	

				String friendlyURL = rootGroupVO.getFriendlyURL();
				rootVO.setFriendlyURL(GCubePortalConstants.PREFIX_GROUP_URL+friendlyURL);
				long logoId = rootGroupVO.getLogoId();
				String logoURL = "/image/layout_set_logo?img_id="+rootGroupVO.getLogoId();
				rootVO.setImageURL(logoURL);


				if (rootGroupVO.getDescription() != null)
					rootVO.setDescription(rootGroupVO.getDescription());
				if (hasRole(getVoAdminRoleName(), rootGroupVO.getGroupName(), currUser))
					rootVO.setUserBelonging(UserBelonging.BELONGING);
				else
					rootVO.setUserBelonging(UserBelonging.NOT_BELONGING);

				
				_log.debug("rootVO = " + rootGroupVO.getGroupName() + " children? = " + rootGroupVO.getChildren().size());
				
				UserManager um = new LiferayUserManager();
				
				//for each root sub organizations (VO)
				for (GCubeGroup vOrg : rootGroupVO.getChildren()) {
					_log.debug("FOUND VO: " + vOrg.getGroupName() );
					//create the VO
					VO voToAdd = new VO();
					voToAdd.setName(vOrg.getName());
					voToAdd.setGroupName(gm.getInfrastructureScope(vOrg.getGroupId()));
					voToAdd.setRoot(false);	
					logoURL = "/image/layout_set_logo?img_id="+vOrg.getLogoId();
					voToAdd.setImageURL(logoURL);

					for (GCubeGroup vre : vOrg.getChildren()) {
						VRE vreToAdd = new VRE();
						vreToAdd.setName(vre.getName());
						vreToAdd.setGroupName(gm.getInfrastructureScope(vre.getGroupId()));

						logoId = vre.getLogoId();
						logoURL =  "/image/layout_set_logo?img_id="+ logoId;
						_log.debug("VRE logoURL=" + logoURL);
						vreToAdd.setImageURL(logoURL);

						String vreUrl = vre.getFriendlyURL();
						vreToAdd.setFriendlyURL(GCubePortalConstants.PREFIX_GROUP_URL+vreUrl);

						//set the description for the vre
						if (vre.getDescription() != null)	{
							vreToAdd.setDescription(vre.getDescription());
						}
						
						vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
						List<GCubeUser> users = um.listUsersByGroup(vre.getGroupId());
						for (GCubeUser gCubeUser : users) {
							if (currUser.getScreenName().compareTo(gCubeUser.getUsername()) == 0) {
								vreToAdd.setUserBelonging(UserBelonging.BELONGING);
								break;
							}
								
						}
						voToAdd.addVRE(vreToAdd);
					}

					String url = vOrg.getFriendlyURL();
					voToAdd.setFriendlyURL(GCubePortalConstants.PREFIX_GROUP_URL + url);

					//set the description for the vre
					if (vOrg.getDescription() != null)	voToAdd.setDescription(vOrg.getDescription());
					//check if the user belongs to it
					voToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
					List<GCubeUser> users = um.listUsersByGroup(vOrg.getGroupId());
					for (GCubeUser gCubeUser : users) {
						if (currUser.getScreenName().compareTo(gCubeUser.getUsername()) == 0) {
							voToAdd.setUserBelonging(UserBelonging.BELONGING);
							break;
						}							
					}
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
		if (getASLSession().getAttribute(LoginPortlet.GATEWAY_NAME) != null)
			gatewayName = getASLSession().getAttribute(LoginPortlet.GATEWAY_NAME).toString();
		LoginServiceUtil.addMembershipRequest(username, rootVO, scope, optionalMessage, getPortalBasicUrl(), gatewayName);
	}	

	/**
	 * create a standard RootVO together with its layout
	 * @param rootVoName
	 * @return the groupid of the created VO
	 * @throws SystemException .
	 * @throws PortalException .
	 */
	private long createRootVO(String rootVoName, String themeid) throws PortalException, SystemException {

		String username = getASLSession().getUsername();

		GCUBESiteLayout siteLayout = GCubeSiteManagerImpl.getBaseLayout(rootVoName, true, username);
		

		//create tab Accounting with 2 subtabs
		GCUBELayoutTab accounting = new GCUBELayoutTab("Accounting", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Navigation", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_NAVIGATION)));
		GCUBELayoutTab portalAccounting = new GCUBELayoutTab("Portal Accounting", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("PortalAccounting", PortletsIdManager.getLRPortletId(PortletsIdManager.ACCOUNTING_PORTAL)));
		GCUBELayoutTab nodeAccounting = new GCUBELayoutTab("Node Accounting", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("PortalAccounting", PortletsIdManager.getLRPortletId(PortletsIdManager.ACCOUNTING_NODES)));
		GCUBELayoutTab serviceAccounting = new GCUBELayoutTab("Service Accounting", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("PortalAccounting", PortletsIdManager.getLRPortletId(PortletsIdManager.ACCOUNTING_NODES)));
		accounting.addSubTab(portalAccounting);
		accounting.addSubTab(nodeAccounting);
		accounting.addSubTab(serviceAccounting);
		siteLayout.addTab(accounting);

		//create tab Service Availability
		siteLayout.addTab(new GCUBELayoutTab("Services Availability", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Services", PortletsIdManager.getLRPortletId(PortletsIdManager.ACCOUNTING_SERVICES))));
		//create tab Ecosystem Monitoring
		siteLayout.addTab(new GCUBELayoutTab("Monitoring", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Monitoring", PortletsIdManager.getLRPortletId(PortletsIdManager.MONITORING_ECOSYSTEM))));

		return GCubeSiteManagerImpl.createRootVO(username, rootVoName, "desc", siteLayout, themeid);
	}
	/**
	 * create a standard Virtual Organization layout
	 * @param voName
	 * @param parentid the organizationid of the root VO
	 * @throws SystemException  .
	 * @throws PortalException  .
	 * @return the groupid of the created VO
	 */
	private long createVO(String voName, long parentid, String themeid) throws PortalException, SystemException {
		
		String username = getASLSession().getUsername();

		GCUBESiteLayout siteLayout = GCubeSiteManagerImpl.getBaseLayout(voName, true, username);
		//create tab VRE Management with 2 subtabs
		GCUBELayoutTab vreManagementTab = new GCUBELayoutTab("VRE Management", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("VRE Management", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_NAVIGATION)));
		GCUBELayoutTab vreDefinitionTab = new GCUBELayoutTab("VRE Definition", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("VRE Definition", PortletsIdManager.getLRPortletId(PortletsIdManager.VRE_DEFINITION)));
		GCUBELayoutTab vreDeploymentTab = new GCUBELayoutTab("VRE Deployment", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("VRE Deployment", PortletsIdManager.getLRPortletId(PortletsIdManager.VRE_DEPLOYMENT)));
		GCUBELayoutTab vreDeployerTab = new GCUBELayoutTab("VRE Deployer", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("VRE Deployer", PortletsIdManager.getLRPortletId(PortletsIdManager.VRE_DEPLOYER)));
		vreDeployerTab.setHidden(true);
		//add the tabs
		vreManagementTab.addSubTab(vreDefinitionTab);
		vreManagementTab.addSubTab(vreDeploymentTab);
		vreManagementTab.addSubTab(vreDeployerTab);

		siteLayout.addTab(vreManagementTab);

		return GCubeSiteManagerImpl.createVO(username, voName, "desc", parentid, siteLayout, themeid);
	}
	/**
	 * create a standard VRE layout
	 * @param vreName
	 * @param parentid the organizationid of the root VO
	 * @throws SystemException  .
	 * @throws PortalException  .
	 * @return the groupid of the created VO
	 */
	private long createVRE(String vreName, String desc, long parentid, String themeid) throws PortalException, SystemException {
		String username = getASLSession().getUsername();
		GCUBESiteLayout siteLayout = GCubeSiteManagerImpl.getBaseLayout(vreName, false, username);
		return GCubeSiteManagerImpl.createVRE(username, vreName, desc, parentid, siteLayout, themeid);
	}


	
	/**
	 * return the infrastructure name in [0], the scopes in [1]
	 */
	public String[] getInfrastructureConfig() {
		String[] toReturn = new String[2];
		toReturn[0] = PortalContext.getConfiguration().getInfrastructureName();
		toReturn[1] = PortalContext.getConfiguration().getVOsAsString();
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
		_log.trace("getPortalBasicUrl: " +toReturn + "queryString: " +	request.getQueryString());
		return toReturn;
	}


	/**
	 * return the root org
	 */
	public VO getRootVO() {
		_log.debug("root called");
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
	 * return the current selected VRE
	 */
	public ResearchEnvironment getSelectedRE() {
		_log.debug("getting Selected Research Environment");
		HttpSession session = this.getThreadLocalRequest().getSession();		
		String selectedGroupName = getASLSession().getScopeName();
		@SuppressWarnings("unchecked")
		ArrayList<VO> vos = (ArrayList<VO>) session.getAttribute(CACHED_VOS);
		for (VO vo : vos) {
			if (vo.getGroupName().equals(selectedGroupName))
				return vo;
			else {
				ArrayList<VRE> vres = vo.getVres();
				for (VRE vre : vres) {
					if (vre.getGroupName().equals(selectedGroupName)) {
						setVREBelonginApplication(vre);
						return new ResearchEnvironment(vre.getName(), vre.getDescription(), vre.getImageURL(), vre.getGroupName(), vre.getFriendlyURL(), vre.getUserBelonging());
					}
				}
			}

		}	
		return null;
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
			String propertyfile = SiteManagerUtil.getTomcatFolder() +"conf" + File.separator + "gcube-data.properties";			
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
	 * this method start the portal installation
	 * @param infrastructure the name of the infrastructure
	 * @param startScopes the name of the starting scopes, comma separated
	 */
	public Boolean installPortalEnv(String infrastructure, String startScopes, SelectedTheme theme, boolean automaticRedirect) {
		String rootVoName = infrastructure.replaceAll(" ", "");
		String[] sites;
		if (startScopes.contains(",")) {
			sites = startScopes.split(",");
			for (int i = 0; i < sites.length; i++) {
				sites[i] = sites[i].replaceAll(" ", "");
			}
		}
		else {
			sites = new String[1];
			sites[0] = startScopes.replaceAll(" ", "");
		}
		_log.info("Starting create Root VO for infrastructure " + rootVO.toString());
		try {
			_log.info("Creating Site Roles for infrastructure " + rootVO.toString());
			RoleManager rm = new LiferayRoleManager();
			rm.createRole(GCubeRole.DATA_MANAGER_LABEL, "Data Manager");
			rm.createRole(GCubeRole.INFRA_MANAGER_LABEL, "Infrastructure Manager");
			rm.createRole(GCubeRole.VO_ADMIN_LABEL, "Virtual Organization Administrator");
			rm.createRole(GCubeRole.VRE_DESIGNER_LABEL, "The Virtual Research Environment Designer");
			rm.createRole(GCubeRole.VRE_MANAGER_LABEL, "The Virtual Research Environment Manager");
			
			
			if (LoginServiceUtil.setupGuestSite(theme)) {
				String themid = "";
				themid = SiteManagerUtil.getgCubeThemeId(ThemesIdManager.GCUBE_LOGGEDIN_THEME);
				SELECTED_THEMEID = themid;				
						
				//here the guest community has been set up already, need to start the VO creation
				long parentid = createRootVO(rootVoName, themid);
				_log.info("Root VO for infrastructure " + rootVoName + " Created with Success id: " + parentid + " themid:" + SELECTED_THEMEID);

				for (int i = 0; i < sites.length; i++) {
					createVO(sites[i], parentid, themid);
				}				
			}
			else 
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		LoginServiceUtil.appendRootOrganizationName(rootVoName);
		LoginServiceUtil.addPropertyDefaultLandingPageAndTheme(automaticRedirect, theme);
		return true;
	}
	/**
	 * the first method to call
	 */
	public Boolean isLayoutLoaded() {
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
	 * @param infrastructure the infrastructure name as in GHNConfig.xml
	 * @param startScopes the starting scopes as in GHNConfig.xml
	 * @return an Hashmap containing for each scope, and array of <class>CheckResult</class>
	 */
	public HashMap<String, ArrayList<CheckResult>> checkInfrastructure(String infrastructure, String startScopes) {
		HashMap<String, ArrayList<CheckResult>> toReturn = new HashMap<String, ArrayList<CheckResult>>();
		//check the root
		toReturn.put(infrastructure, LoginServiceUtil.checkScope(infrastructure));
		//split the vos
		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				toReturn.put(organizations[i], LoginServiceUtil.checkScope(infrastructure+"/"+organizations[i]));
			}
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes;
			toReturn.put(organizations[0], LoginServiceUtil.checkScope(infrastructure+"/"+organizations[0]));
		}

		_log.debug("Infra check result: " + toReturn);
		return toReturn;
	}
	/**
	 * check if the vo scope contain VREs
	 */
	public Boolean checkVresPresence(String infrastructure, String startScopes) {
		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				if (LoginServiceUtil.checkVresPresence(infrastructure+"/"+organizations[i]))
					return true;
			}
			return false;
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes;
			return LoginServiceUtil.checkVresPresence(infrastructure+"/"+organizations[0]);
		}
	}
	/**
	 * 
	 * @param infrastructure the infrastructure name
	 * @param startScopes the starting scopes
	 * @return an arraylist of <class>VO</class> containing their child VREs
	 */
	public ArrayList<VO> getVresFromInfrastructure(String infrastructure, String startScopes) {
		ArrayList<VO> toReturn = new ArrayList<VO>();

		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				VO toAdd = new VO();
				toAdd.setName(organizations[i]);
				toAdd.setVres(LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[i]));
				toReturn.add(toAdd);
			}
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes;
			VO toAdd = new VO();
			toAdd.setName(organizations[0]);
			toAdd.setVres(LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[0]));
			toReturn.add(toAdd);
		}
		return toReturn;
	}

	/**
	 * install the VREs chosen by the end user
	 */
	public Boolean installVREs(ArrayList<VO> parents) {
		GroupManager gm = new LiferayGroupManager();
		for (VO vo : parents) {
			if (vo.getVres().size() > 0) {
				long parentGroupId = -1;
				try {
					parentGroupId = gm.getGroupId(vo.getName());
					for (final VRE vre : vo.getVres())
						if (vre.getUserBelonging() != null) //then this VREs has to be installed
							createVRE(vre.getName(), vre.getDescription(), parentGroupId, SELECTED_THEMEID);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
}
