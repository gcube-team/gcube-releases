package org.gcube.portal.staging.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.portlet.ActionRequest;

import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.communitymanager.ThemesIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.impl.GCubeSiteManagerImpl;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author Massimiliano Assante
 * this class create the default community, its layout and its content
 *
 */
public class CommunityCreator {
	private static Log _log = LogFactoryUtil.getLog(PortalStaging.class);
	/**
	 * 
	 */
	public static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";

	public static final String USERNAME = "test";

	private static CommunityCreator instance = null;

	protected CommunityCreator() {}

	public static CommunityCreator getInstance() {
		if(instance == null) {
			instance = new CommunityCreator();
		}		
		return instance;
	}
	/**
	 * check if the user exists in the database and has AuthZ to perform the operation
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private User validateUser(String email) throws PortalException, SystemException {
		Company company = getCompany();
		return UserLocalServiceUtil.getUserByEmailAddress(company.getCompanyId(), email);			
	}
	/**
	 * return the companyId
	 * @param webId .
	 * @return the company bean
	 * @throws SystemException 
	 * @throws PortalException 
	 * @throws PortalException .
	 * @throws SystemException .
	 */
	public static Company getCompany() throws PortalException, SystemException {
		return CompanyLocalServiceUtil.getCompanyByWebId(getDefaultCompanyWebId());
	}
	/**
	 * 
	 * @return the default company web-id (e.g. iMarine.eu)
	 */
	public static String getDefaultCompanyWebId() {
		String defaultWebId = "";
		try {
			defaultWebId = GetterUtil.getString(PropsUtil.get("company.default.web.id"));
		}
		catch (NullPointerException e) {
			_log.info("Cound not find property company.default.web.id in portal.ext file returning default web id: " + DEFAULT_COMPANY_WEB_ID);
			return DEFAULT_COMPANY_WEB_ID;
		}
		return defaultWebId;
	}
	/**
	 * Create the group in the database
	 * 
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	protected Group createDefaultCommunity(ActionRequest request) throws Exception {
		User currentUser = PortalUtil.getUser( request );
		String defaultCommunityName =  getDefaultCommunityName();
		_log.info("Creating Default Site " + defaultCommunityName );	
		Group toReturn = createGroup(defaultCommunityName, "Default Site", -1);
		_log.info("GROUP created  id:" + toReturn.getGroupId());
		associateLayout(currentUser, toReturn, defaultCommunityName, "desc", 0);		
		_log.info("Layout Associated correctly for " + defaultCommunityName);
	
		String themid = "";
		themid = SiteManagerUtil.getgCubeThemeId(ThemesIdManager.GCUBE_LOGGEDIN_THEME);
			
		
		//apply the theme to the default community
		GroupManager gm = new LiferayGroupManager();
		long groupId = gm.getGroupId(defaultCommunityName);						
		Theme themeToApply = ThemeLocalServiceUtil.getTheme(SiteManagerUtil.getCompany().getCompanyId(), themid, false);
		LayoutSetLocalServiceUtil.updateLookAndFeel(groupId, themeToApply.getThemeId(), "", "", false);
		_log.debug("LayoutSet Theme with id " + themid +  " Applied Correctly to Default Community = " + defaultCommunityName);
		
		UserManager uman = new LiferayUserManager();
		long uid = currentUser.getUserId();
		uman.assignUserToGroup(toReturn.getGroupId(), uid);
		_log.debug("Added user " + currentUser.getFullName() + " to group " + toReturn.getName() + " with Success");	
		
		//update logo
		InputStream is = GCubeSiteManagerImpl.class.getResourceAsStream(GCubeSiteManagerImpl.SITE_DEFAULT_LOGO);
		//FileInputStream fis = new FileInputStream(writeTempLogo(is));
		LayoutSetLocalServiceUtil.updateLogo(toReturn.getGroupId(), true, true, is);
		
		return toReturn;
	}
	/**
	 * 
	 * @param groupName
	 * @param description
	 * @param parentGroupId
	 * @return
	 */
	private Group createGroup(String groupName, String description, long parentGroupId) {
		Group group = null;
		if (parentGroupId < 0)
			parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
		try {
			//get the userId for the default user
			Company company = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
			long companyId = company.getCompanyId();
			long defaultUserId = UserLocalServiceUtil.getDefaultUserId(companyId);
			group = GroupLocalServiceUtil.addGroup(defaultUserId, 
					parentGroupId, 
					Group.class.getName(), 0, 
					GroupConstants.DEFAULT_LIVE_GROUP_ID, 
					groupName, 
					description, 
					GroupConstants.TYPE_SITE_OPEN, 
					true, 
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, "/" + groupName, true, true, 					
					new ServiceContext());
			_log.info("Created Group with name " + groupName);
			return group;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return group;
	}


	/**
	 * Use this method for creating Site programmatically and associate a default layout to it
	 * 
	 * @param communityName -
	 * @param communityDesc -
	 * @param parentID -
	 * @return the community created id
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public long associateLayout(User currentUser, Group group, String communityName, String communityDesc, long parentID) throws PortalException, SystemException {
		GCUBESiteLayout siteLayout = new GCUBESiteLayout(SiteManagerUtil.getCompany(), communityName, currentUser.getEmailAddress());			
		siteLayout.addTab(new GCUBELayoutTab("Home", GCUBELayoutType.ONE_COL, getGCUBELoginPorlet()));
		createLayout(group, validateUser(currentUser.getEmailAddress()), siteLayout);
		return 1;

	}

	/**
	 * 
	 * @return a list of GCUBEPortlet for the first community Tab
	 */
	private List<GCUBEPortlet> getGCUBELoginPorlet() {
		List<GCUBEPortlet> toReturn = new ArrayList<GCUBEPortlet>();
		toReturn.add(new GCUBEPortlet("gCube Login", getLPortletName("gCubeLogin")));
		return toReturn;
	}
	/**
	 * match the portlet names returned by the VRE Modeler Service, return the portlet name for Liferay 
	 * @return the HashMap containing the external links for the existent portlets
	 */
	private String getLPortletName(String gCubePortletName) {

		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";
		try {
			String propertyfile = System.getenv("CATALINA_HOME")+"/conf/gcube-portlets.properties";
			_log.info("Loading gCube-portlets list from " + propertyfile);
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis );
			toReturn = props.getProperty(gCubePortletName);			
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Use this method for creating VRE programmatically and associate it a layout
	 * 
	 * @param group
	 * @param user
	 * @param siteLayout
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private long createLayout(Group group, User user, GCUBESiteLayout siteLayout) throws PortalException, SystemException {

		Layout layout = null;

		for (GCUBELayoutTab tab : siteLayout.getTabs()) {
			String layoutName = siteLayout.getName().replaceAll(" ", "-");
			String friendlyURL= "/"+tab.getCaption().replaceAll(" ", "-");
			_log.debug("Trying creating layout " + layoutName + " url:" + friendlyURL + " userid:" + user.getUserId() + " groupid:" + group.getGroupId());

			ServiceContext ctx = new ServiceContext();			
			layout = LayoutLocalServiceUtil.addLayout(user.getUserId(), group.getGroupId(), true, 0,  tab.getCaption(), layoutName, 
					group.getDescription(), "portlet", false, friendlyURL, ctx);

			_log.debug("Trying creating tab " + tab.getCaption());
			//get the typeSettings string for the liferay database from the tab Object
			String typeSettings = tab.getLayoutTypeSettings();
			//set the typesettings in the model
			layout.setTypeSettings(typeSettings);
			//actually update the layout 
			LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(), layout.getTypeSettings());
			_log.debug("Added tab " + tab.getCaption() + " to layout for community: " + siteLayout.getName());		



			/**
			 * removing portlet frame for each portlet of the layout 
			 */
			for (GCUBEPortlet gPortlet : tab.getPortlets()) {

				long companyId = getCompany().getCompanyId();

				Portlet lPortlet = PortletLocalServiceUtil.getPortletById(companyId, ""+gPortlet.getPortletId());

				long ownerId = 0;
				int ownerType = 3;

				String portletPreferencesString = 
						"<portlet-preferences>" +
								"<preference><name>lfr-wap-initial-window-state</name><value>NORMAL</value></preference>"+
								"<preference><name>portlet-setup-show-borders</name><value>false</value></preference>" +
								"<preference><name>	portlet-setup-use-custom-title</name><value>false</value></preference>" +
								"</portlet-preferences>"
								;

				PortletPreferencesLocalServiceUtil.addPortletPreferences(
						companyId, ownerId, ownerType, layout.getPlid(),  lPortlet.getPortletId(), lPortlet, portletPreferencesString);

				_log.debug("Added Preference for portlet " + lPortlet.getPortletName() + " for layout plid:" + layout.getPlid());	
			}


		}
		return layout.getLayoutId();
	}
	/**
	 * read tDefault Community name from a property file and returns it
	 */
	public String getDefaultCommunityName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = SiteManagerUtil.getTomcatFolder() +"conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty("defaultcommunity");
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "D4science Gateway";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Portal Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Gateway Name: " + toReturn );
		return toReturn;
	}
}
