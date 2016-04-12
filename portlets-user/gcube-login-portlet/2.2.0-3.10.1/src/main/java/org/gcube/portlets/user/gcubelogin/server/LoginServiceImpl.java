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
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.ThemesIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.impl.OrganizationManagerImpl;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginService;
import org.gcube.portlets.user.gcubelogin.server.portlet.LoginPortlet;
import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.ResearchEnvironment;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;
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
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 2.0 SEP 2013
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

	private boolean withinPortal = false;

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
			withinPortal = true;
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * first method called by the UI
	 */
	public ArrayList<VO> getInfrastructureVOs() {	
		_log.trace("getInfrastructureVOs method called");
		if (! withinPortal) {
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
						VRE vreToAdd = new VRE();
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

						//check if the user belongs to it
						if (currUser.getOrganizations().contains(vre)) {
							vreToAdd.setUserBelonging(UserBelonging.BELONGING);
						}
						else if (LoginServiceUtil.checkPending(currUser.getScreenName(), vre.getOrganizationId()))
							vreToAdd.setUserBelonging(UserBelonging.PENDING);
						else
							vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);

						voToAdd.addVRE(vreToAdd);
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

		GCUBESiteLayout siteLayout = OrganizationManagerImpl.getBaseLayout(rootVoName, true, username);
		

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

		return OrganizationManagerImpl.createVO(username, rootVoName, "desc", siteLayout, themeid);
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

		GCUBESiteLayout siteLayout = OrganizationManagerImpl.getBaseLayout(voName, true, username);
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

		return OrganizationManagerImpl.createVO(username, voName, "desc", parentid, siteLayout, themeid);
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
		GCUBESiteLayout siteLayout = OrganizationManagerImpl.getBaseLayout(vreName, false, username);
		return OrganizationManagerImpl.createVRE(username, vreName, desc, parentid, siteLayout, themeid);
	}


	
	/**
	 * return the infrastructure name in [0], the scopes in [1]
	 */
	public String[] getConfigFromGCore() {
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
	 * this method start the portal installation, tipically the parameters come from the gHNConfig.xml
	 * @param infrastructure the name of the infrastructure
	 * @param startScopes the name of the starting scopes, comma separated
	 */
	public Boolean installPortalEnv(String infrastructure, String startScopes, SelectedTheme theme, boolean automaticRedirect) {
		String rootVoName = infrastructure.replaceAll(" ", "");
		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				organizations[i] = organizations[i].replaceAll(" ", "");
			}
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes.replaceAll(" ", "");
		}
		_log.info("Starting create Root VO for infrastructure " + rootVO);
		try {
			if (LoginServiceUtil.setupGuestCommunity(theme)) {
				//apply the loggedinTheme to the default community
				String themid = "";
				boolean applyTheme = true;
				switch (theme) {
				case GENERIC:
					themid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.GCUBE_LOGGEDIN_THEME);
					break;
				case iMARINE:
					themid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.iMARINE_LOGGEDIN_THEME);
					break;
				default:
					//leave the current
					applyTheme = false;
					_log.info("User chose liferay theme");
					break;
				}
				SELECTED_THEMEID = themid;
				//here the guest community has been set up already, need to start the VO creation
				long parentid = createRootVO(rootVoName, themid);
				_log.info("Root VO for infrastructure " + rootVoName + " Created with Success id: " + parentid + " themid:" + SELECTED_THEMEID);

				for (int i = 0; i < organizations.length; i++) {
					createVO(organizations[i], parentid, themid);
				}	

				if (applyTheme) {
					Theme loggedinTheme = ThemeLocalServiceUtil.getTheme(OrganizationsUtil.getCompany().getCompanyId(), themid, false);
					ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
					LayoutSetLocalServiceUtil.updateLookAndFeel(themeDisplay.getScopeGroupId(), loggedinTheme.getThemeId(), "", "", false);
					_log.info("Loggedin Theme with id " + themid +  " to Default Community Applied Correctly");
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
		for (VO vo : parents) {
			if (vo.getVres().size() > 0) {
				long orgid = -1;
				try {
					orgid = OrganizationLocalServiceUtil.getOrganization(OrganizationsUtil.getCompany().getCompanyId(), vo.getName()).getOrganizationId();

					for (final VRE vre : vo.getVres())
						if (vre.getUserBelonging() != null) //then this VREs has to be installed
							createVRE(vre.getName(), vre.getDescription(), orgid, SELECTED_THEMEID);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * this method create a user and assigns the role Administrator to this user, also assigns the VO-ADMIN role or VRE-Manager 
	 * for each VO/VRE present in the infrastructure
	 */
	public Boolean createAdministratorAccount(String email, String password, String firstname, String lastname) {
		User creator = null;
		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
			creator = themeDisplay.getUser();
			return LoginServiceUtil.createAdministratorAccountAndAssignRoles(creator.getUserId(), email, password, firstname, lastname);
		}
		catch (com.liferay.portal.NoSuchRoleException ex) {
			_log.error("Error NoSuchRoleException userid for username " + creator.getScreenName() + " id: " + creator.getUserId());
			ex.printStackTrace();
			return false;
		}
		catch (Exception e) {
			_log.error("Error while creating account administrator " + creator.getScreenName() + " id: " + creator.getUserId());
			e.printStackTrace();
			return false;
		} 		
	}
}
