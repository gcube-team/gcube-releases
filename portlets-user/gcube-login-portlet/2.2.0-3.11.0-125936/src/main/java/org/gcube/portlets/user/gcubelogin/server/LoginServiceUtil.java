package org.gcube.portlets.user.gcubelogin.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.ThemesIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.CheckType;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 2.0 Jan 10th 2012
 */
public class LoginServiceUtil {
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";
	/**
	 * 
	 */
	public static final String PUBLIC_LAYOUT_NAME = "	Data e-Infrastructure gateway";
	/**
	 * 
	 */
	public static final String GUEST_COMMUNITY_NAME = "Guest";

	private static final Logger _log = LoggerFactory.getLogger(LoginServiceImpl.class);
	/**
	 * @return true if the setup is performed correctly
	 * @throws PortalException .
	 * @throws SystemException .
	 */
	protected static boolean setupGuestCommunity(SelectedTheme theme) throws PortalException, SystemException {
		Company company = OrganizationsUtil.getCompany();
		Group publicCommunity = GroupServiceUtil.getGroup(company.getCompanyId(), GUEST_COMMUNITY_NAME);
		_log.debug("Found Guest Community, name: " + publicCommunity.getName() + " id:"  + publicCommunity.getGroupId());
		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(publicCommunity.getGroupId(), false);
		_log.debug("Found layout for " + publicCommunity.getName() + " id:"  + layouts.get(0).getLayoutId());
		//delete the existent layout
		LayoutLocalServiceUtil.deleteLayout(layouts.get(0));
		String email = "test@"+company.getWebId();
		GCUBESiteLayout siteLayout = new GCUBESiteLayout(company, GUEST_COMMUNITY_NAME, email);		

		List<GCUBEPortlet> portlets2Add = new ArrayList<GCUBEPortlet>();
		portlets2Add.add(new GCUBEPortlet("Login", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_LOGIN)));
		portlets2Add.add(new GCUBEPortlet("Edit Content", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_WEBCONTENT_DISPLAY)));

		siteLayout.addTab(new GCUBELayoutTab(PUBLIC_LAYOUT_NAME, GCUBELayoutType.TWO_COL_3070, portlets2Add));
		createGuestCommunityLayout(siteLayout, publicCommunity, theme);
		_log.info("Layout for " + publicCommunity.getName() + " Correctly Updated!");

		return true;
	}
	/**
	 * create the public community layout 
	 * @param siteLayout an instance of Liferay API <class>GCUBESiteLayout</class>
	 * @param group an instance of Liferay API <class>Group</class>
	 * @throws PortalException .
	 * @throws SystemException .
	 */
	private static void createGuestCommunityLayout(GCUBESiteLayout siteLayout, Group group, SelectedTheme selectedTheme) throws PortalException, SystemException {
		Layout layout = null;
		Company company = OrganizationsUtil.getCompany();
		String email = "test@"+company.getWebId();
		User user = UserLocalServiceUtil.getUserByEmailAddress(company.getCompanyId(), email);		

		for (GCUBELayoutTab tab : siteLayout.getTabs()) {
			String layoutName = siteLayout.getName().replaceAll(" ", "-");
			String friendlyURL= "/home";
			_log.info("Trying creating layout " + layoutName + " url:" + friendlyURL + " groupid:" + group.getGroupId());

			ServiceContext ctx = new ServiceContext();			
			layout = LayoutLocalServiceUtil.addLayout(user.getUserId(), group.getGroupId(), false, 0,  tab.getCaption(), layoutName, 
					group.getDescription(), "portlet", false, friendlyURL, ctx);

			_log.info("Trying creating tab " + tab.getCaption());
			//get the typeSettings string for the liferay database from the tab Object
			String typeSettings = tab.getLayoutTypeSettings();
			//set the typesettings in the model
			layout.setTypeSettings(typeSettings);
			//actually update the layout 
			LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(), layout.getTypeSettings());
			_log.info("Added tab " + tab.getCaption() + " to layout for community: " + siteLayout.getName());		

			long companyId = company.getCompanyId();

			String themid = "";
			boolean applyTheme = true;
			switch (selectedTheme) {
			case GENERIC:
				themid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.GCUBE_PUBLIC_THEME);
				break;
			case iMARINE:
				themid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.iMARINE_PUBLIC_THEME);
				break;
			default:
				//leave the current
				applyTheme = false;
				_log.info("User chose liferay theme");
				break;
			}
			if (applyTheme) {
				Theme publicTheme = ThemeLocalServiceUtil.getTheme(companyId, themid, false);
				LayoutSetLocalServiceUtil.updateLookAndFeel(group.getGroupId(), publicTheme.getThemeId(), "", "", false);
				_log.info("LayoutSet Theme with id " + themid +  ", " + selectedTheme +" Applied Correctly");
			}
			/**
			 * removing portlet frame for each portlet of the layout 
			 */
			for (GCUBEPortlet gPortlet : tab.getPortlets()) {

				Portlet lPortlet = PortletLocalServiceUtil.getPortletById(companyId, ""+gPortlet.getPortletId());

				long ownerId = 0;
				int ownerType = 3;

				String portletPreferencesString = 
						"<portlet-preferences>" +
								"<preference><name>lfr-wap-initial-window-state</name><value>NORMAL</value></preference>"+
								"<preference><name>portlet-setup-show-borders</name><value>true</value></preference>" +
								"<preference><name>	portlet-setup-use-custom-title</name><value>true</value></preference>" +
								"</portlet-preferences>"
								;

				PortletPreferencesLocalServiceUtil.addPortletPreferences(
						companyId, ownerId, ownerType, layout.getPlid(),  lPortlet.getPortletId(), lPortlet, portletPreferencesString);

				_log.info("Added Preference for portlet " + lPortlet.getPortletName() + " for layout plid:" + layout.getPlid());	
			}


		}
	}
	/**
	 * 
	 * @param screenName
	 * @param organizatioId
	 * @return
	 */
	protected static boolean checkPending(String screenName, long organizationId) {
		try {
			for (UserModel userModel : new LiferayUserManager().listPendingUsersByGroup(""+organizationId)) 
				if (userModel.getScreenName().compareTo(screenName) == 0) return true;
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * add a property to gcube-data.properties for root vo
	 */
	protected static void addPropertyDefaultLandingPageAndTheme(boolean automaticRedirect, SelectedTheme theme) {
		Properties props = new Properties();

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"webapps/ROOT/WEB-INF/classes/portal-ext.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			if (automaticRedirect)
				props.setProperty("default.landing.page.path", "/group/data-e-infrastructure-gateway/home");

			//apply the selected theme, if different form classic
			String themeid = "";
			boolean applyTheme = true;
			switch (theme) {
			case GENERIC:
				themeid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.GCUBE_LOGGEDIN_THEME);
				break;
			case iMARINE:
				themeid = OrganizationsUtil.getgCubeThemeId(ThemesIdManager.iMARINE_LOGGEDIN_THEME);
				break;
			default:
				//leave the current
				applyTheme = false;
				_log.info("User chose liferay theme");
				break;
			}
			if (applyTheme)
				props.setProperty("default.regular.theme.id", themeid);

			//is any of the two was selected store the props on disk
			if (applyTheme || automaticRedirect) {
				FileOutputStream fos = new FileOutputStream(propsFile);
				props.store(fos, null);
			}
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}
		_log.info("Added property default.landing.page.path and theme ");
	}
	/**
	 * add a property to gcube-data.properties for root vo, it make the login portlet understand the installation was setup already
	 * @param rootVoName .
	 */
	protected static void appendRootOrganizationName(String rootVoName) {
		Properties props = new Properties();

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			props.setProperty(ROOT_ORG, rootVoName);

			FileOutputStream fos = new FileOutputStream(propsFile);
			props.store(fos, null);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}
		_log.info("Added property Root VO Name: " + rootVoName );
	}
	/**
	 * read the root VO name from a property file and retuns it
	 */
	protected static String getRootOrganizationName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(ROOT_ORG);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "gcube";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default VO Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Root VO Name: " + toReturn );
		return toReturn;
	}
	/**
	 * simply returns fake VOS for debugging purpose
	 * @return
	 */
	protected static ArrayList<VO> getFakeVOs() {
		VO rootVO = new VO();
		rootVO.setRoot(true);
		rootVO.setName("/d4science.research-infrastructures.eu/");
		rootVO.setDescription("This is the description for the ROOT VO");
		rootVO.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		rootVO.setUserBelonging(UserBelonging.BELONGING);


		/***************************************/

		VO emVO = new VO();
		emVO.setRoot(false);
		emVO.setGroupName("/d4science.research-infrastructures.eu/EM/");
		emVO.setName("EM VO");
		emVO.setDescription("EM and AEM Virtual Organisation The FARM Virtual Organisation is the dynamic group of individuals and/or institutions defined around a set of sharing rules in which resource providers and consumers specify clearly and carefully just what is shared, who is allowed to share, and the conditions under which sharing occurs to serve the needs of the Fisheries and Aquaculture Resources Management.");
		emVO.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");

		emVO.setUserBelonging(UserBelonging.NOT_BELONGING);
		//			
		//			
		VRE cool_EM_VRE = new VRE();
		cool_EM_VRE.setName("COOL EM VRE");
		cool_EM_VRE.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		cool_EM_VRE.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		cool_EM_VRE.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/gcm-preview.jpg");
		cool_EM_VRE.setUserBelonging(UserBelonging.BELONGING);
		emVO.addVRE(cool_EM_VRE);

		VRE cool_EM_VRE2 = new VRE();
		cool_EM_VRE2.setName("COOL VRE 2");
		cool_EM_VRE2.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE2");
		cool_EM_VRE2.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE2.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		cool_EM_VRE2.setUserBelonging(UserBelonging.NOT_BELONGING);

		VRE cool_EM_VRE3 = new VRE();
		cool_EM_VRE3.setName("COOL EM VRE TRE");
		cool_EM_VRE3.setGroupName("/d4science.research-infrastructures.eu/EM/COOlVRE3");
		cool_EM_VRE3.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE3.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		cool_EM_VRE3.setUserBelonging(UserBelonging.BELONGING);

		VRE demo = new VRE();
		demo.setName("Demo");
		demo.setGroupName("/d4science.research-infrastructures.eu/EM/Demo");
		demo.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		demo.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		demo.setUserBelonging(UserBelonging.BELONGING);

		VRE vreGCM = new VRE();
		vreGCM.setName("GCM");
		vreGCM.setGroupName("/d4science.research-infrastructures.eu/EM/GCM");
		vreGCM.setDescription("Global Ocean Chlorophyll Monitoring (GCM) Virtual Research Environment<br />" 
				+ "The phytoplankton plays a similar role to terrestrial green plants in the photosynthetic process and are credited with removing as much carbon dioxide from the atmosphere as their earthbound counterparts, making it important to monitor and model plankton into calculations of future climate change.");
		vreGCM.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		vreGCM.setUserBelonging(UserBelonging.BELONGING);


		emVO.addVRE(cool_EM_VRE);
		emVO.addVRE(cool_EM_VRE2);
		emVO.addVRE(cool_EM_VRE3);
		emVO.addVRE(demo);
		emVO.addVRE(vreGCM);

		ArrayList<VO> toReturn = new ArrayList<VO>();
		toReturn.add(rootVO);
		toReturn.add(emVO);
		toReturn.add(emVO);
		return toReturn;
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
		} catch (UserManagementSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			// TODO Auto-generated catch block
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
	public static void addMembershipRequest(String username, VO rootVO, String scope, String optionalMessage,String portalbasicurl, String gatewayName) {
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);

		User currUser = null;
		try {
			currUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();



		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		
		_log.info("Request MEMBERSHIP for: " + selectedVRE + " scope: " +	scope);

		UserManager um = new LiferayUserManager();
		GroupManager gm  = new LiferayGroupManager();
		String userid = "";
		try {
			userid = um.getUserId(currUser.getScreenName());

			um.requestMembership(userid, gm.getGroupId(selectedVRE), optionalMessage );

		} catch (UserManagementSystemException e) {
			e.printStackTrace();		
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		}

		String imageURL = portalbasicurl + rootVO.getImageURL();

		StringBuffer body = new StringBuffer();
		body.append("<img src=\"" + imageURL +"\" />");
		body.append("<br /><br />");
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has requested access to the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmailAddress());
		body.append("</p>");
		body.append("<p>");
		body.append("The request is annotated with the following text: " + optionalMessage);
		body.append("</p>");
		body.append("<p>");
		body.append("You are kindly asked to manage such request by either approving or rejecting it through the user management " +
				"facilities available at ");
		body.append("<br />" + portalbasicurl);
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
				" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
				"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");

		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] - Request registration to VO/VREs", body.toString());

		mailToAdmin.sendEmail();
	}
	/**
	 * 
	 * @param scope pass a scope without "/"
	 * @return ArrayList<CheckResult> containing the result for the test done
	 */
	protected static ArrayList<CheckResult> checkScope(String scopename) {
		ScopeBean scope = null;
		ArrayList<CheckResult> toReturn = new ArrayList<CheckResult>();
		try {	
			scope = new ScopeBean("/"+scopename);
			_log.info("Ready to check scope: " + scope.toString());

			ScopeProvider.instance.set(scope.toString());	

			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ scope.type() +"'");

			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);

			if (client.submit(query).size() > 0) {
				toReturn.add(new CheckResult(CheckType.InformationSystem, true));
				toReturn.add(new CheckResult(CheckType.ResourceManager, true));
			} else
				toReturn.add(new CheckResult(CheckType.InformationSystem, true)); //if you get here then there is no RM
		} 
		catch (IllegalStateException ex) {
			_log.error("IllegalStateException: Could not locate service map for infrastructure " + scope.toString() 
					+ " check your $PortalBundle/infrastructure.properties file, make sure you added <artifactId>common-scope-maps</artifactId> to your classpath. Message: " + ex.getMessage());
			toReturn.add(new CheckResult(CheckType.ServiceMap, false));
			toReturn.add(new CheckResult(CheckType.InformationSystem, false));
			toReturn.add(new CheckResult(CheckType.ResourceManager, false));
			return toReturn;
		}
		catch (DiscoveryException e) {
			_log.error("DiscoveryException: This means either your Service map for infrastructure " + scope.toString() 
					+ " is not present or that scope: "+ scope.name() +" is missing check your $PortalBundle/infrastructure.properties. Exception message: " + e.getMessage());
			toReturn.add(new CheckResult(CheckType.ServiceMap, false));
			toReturn.add(new CheckResult(CheckType.InformationSystem, false));
			toReturn.add(new CheckResult(CheckType.ResourceManager, false));
			return toReturn;
		}
		catch (Exception e) {
			toReturn.add(new CheckResult(CheckType.InformationSystem, false)); //if you get here then there is no RM
			toReturn.add(new CheckResult(CheckType.ResourceManager, false)); //if you get here then there is no RM
			_log.error("Generic Exception, Message: " + e.getMessage());
			e.printStackTrace();
			return toReturn;
		}
		return toReturn;

	}

	/**
	 * 
	 * @param scopename a string
	 * @return true if any VRE Exists
	 */
	protected static Boolean checkVresPresence(String scopename) {
		ScopeBean scope = null;
		scope = new ScopeBean("/"+scopename);

		try {
			_log.info("Searching for VREs into " + scope.name());
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope.toString());	
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ Type.VRE +"'");

			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			boolean result = client.submit(query).size() > 0;			
			ScopeProvider.instance.set(currScope);	
			return result;
		} catch (Exception e) {
			_log.error("Generic Exception for " + scope.name()  + " " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * @param scopename a string
	 * @return an arraylist of <class>VRE</class> with just name and description filled
	 */
	protected static ArrayList<VRE> getVREsFromInfrastructure(String scopename) {
		ArrayList<VRE> toReturn = new  ArrayList<VRE>();
		ScopeBean scope = null;
		scope = new ScopeBean("/"+scopename);

		try {
			_log.info("Searching for VREs into " + scope.name());
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope.toString());	
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ Type.VRE +"'");

			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			for (GenericResource genres : client.submit(query)) {
				toReturn.add(new VRE(genres.profile().name(), genres.profile().description(), "", "", "", null));
			}
			ScopeProvider.instance.set(currScope);	
			return toReturn;
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Generic Exception for " + scope.name()  + " " + e.getMessage());
			return null;
		}
	}
	/**
	 * this method create a user and assigns the role Administrator to this user, also assigns the VO-ADMIN role or VRE-Manager 
	 * for each VO/VRE present in the infrastructure
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Boolean createAdministratorAccountAndAssignRoles(long creatorUserId, 
			String email, String password, String firstname, String lastname) throws Exception {

		Locale locale = new Locale("en_US");
		ServiceContext serviceContext = new ServiceContext();
		//get the long[] organizationid
		int orgNo = OrganizationLocalServiceUtil.getOrganizationsCount();
		long[] organizationIds = new long[orgNo];
		for (int i = 0; i < orgNo; i++) organizationIds[i] = OrganizationLocalServiceUtil.getOrganizations(0, orgNo).get(i).getOrganizationId();
		//get the Administrator Role
		ArrayList<Role> roles = new ArrayList<Role>();
		roles.add(RoleLocalServiceUtil.getRole(OrganizationsUtil.getCompany().getCompanyId(), "Administrator"));
		//get the other VO/VRE Roles
		GroupManager gm = new LiferayGroupManager();
		for (GroupModel g: gm.listGroups()) {
			Role role = null;
			try {
				if (gm.isRootVO(g.getGroupId()) || gm.isVO(g.getGroupId()) ) {
					role = RoleLocalServiceUtil.getRole(OrganizationsUtil.getCompany().getCompanyId(), "VO-Admin-"+g.getGroupName());
				}
				else if (gm.isVRE(g.getGroupId())) {
					role = RoleLocalServiceUtil.getRole(OrganizationsUtil.getCompany().getCompanyId(), "VRE-Manager-"+g.getGroupName());
				}
			} catch (com.liferay.portal.NoSuchRoleException ex) {
				_log.error("Error NoSuchRoleException " + ex.getMessage());
			}	
			if (role != null) {
				roles.add(role);
			}
		}
		long[] roleIds = new long[roles.size()];
		//copy the roleids
		for (int i = 0; i < roles.size(); i++) 
			roleIds[i] = roles.get(i).getRoleId();

		User created = UserLocalServiceUtil.addUser(creatorUserId, OrganizationsUtil.getCompany().getCompanyId(), false, password, password, true, "", 
				email, 0L, "", locale, firstname, "", lastname, 0, 0, true, 
				1, 1, 1970, "", null, organizationIds, roleIds, null, true, serviceContext);
		_log.info("Administrator user added correctly with email " + email + " trying to assign him groups ...");
		UserManager um = new LiferayUserManager();
		for (GroupModel g: gm.listGroups()) {
			um.assignUserToGroup(g.getGroupId(), ""+created.getUserId());
		}
		_log.info("Administrator user added to groups correctly");
		return true;
	}
}
