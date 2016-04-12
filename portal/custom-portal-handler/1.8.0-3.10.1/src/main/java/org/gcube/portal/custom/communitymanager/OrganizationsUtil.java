package org.gcube.portal.custom.communitymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

public class OrganizationsUtil {
	/**
	 * 
	 */
	private static Log _log = LogFactoryUtil.getLog(OrganizationManager.class);
	/**
	 * 
	 */
	public static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";
	/**
	 * 
	 */
	private static final int LIFERAY_REGULAR_ROLE_ID = 1;
	/**
	 * return the companyId
	 * @param webId .
	 * @return the company bean
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
	 * 
	 * @return $CATALINA_HOME
	 */
	public static String getTomcatFolder() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}
	
	/**
	 * get the themes id for from the property file
	 * @param themeid see <class>org.gcube.portal.custom.communitymanager.ThemesIdManager</class>
	 * @return the themeid for LR
	 */
	public static String getgCubeThemeId(String themeid) {

		Properties props = new Properties();
		String toReturn = "";
		String propertyfile = "";
		try {
			propertyfile = getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(themeid);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.error("Error retrieving property "+ themeid +" from " + propertyfile);
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 *
	 * @return -
	 */
	protected List<GCUBEPortlet> getVREBelongingPortlets(List<String> belongingPorltets) {
		List<GCUBEPortlet> toReturn = new LinkedList<GCUBEPortlet>();
		Properties props = new Properties();
		try {
			String propertyfile = getTomcatFolder()+"conf/gcube-portlets.properties";
			_log.debug("Loading gCube-portlets list from " + propertyfile);
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis );
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}

		for (Object portlet : props.values()) {
			String lportletName = (String) portlet.toString();
			if (lportletName.compareTo("Login_WAR_Loginportlet") != 0 && lportletName.contains("WAR")) {
				toReturn.add(new GCUBEPortlet(lportletName, lportletName));
				_log.debug("Adding lportlet id:" + lportletName);
			}

		}
		return toReturn;
	}
	/**
	 * 
	 * @return the default template for a community (just one Tab)
	 */
	public List<GCUBEPortlet> getDefaultPortlets() {
		List<GCUBEPortlet> toReturn = new ArrayList<GCUBEPortlet>();
		toReturn.add(new GCUBEPortlet(PortletsIdManager.LR_CALENDAR, PortletsIdManager.getLRPortletId(PortletsIdManager.LR_CALENDAR)));
		toReturn.add(new GCUBEPortlet(PortletsIdManager.LR_ACTIVITIES, PortletsIdManager.getLRPortletId(PortletsIdManager.LR_ACTIVITIES)));
		return toReturn;
	}
	/**
	 * Create a Regular Manager Role for the community/Organization 
	 * @param vreName
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Role addManagerRole(String vreName, String username) throws PortalException, SystemException {		
		Company company = getCompany();
		User user = validateUser(username);		
		String roleName = "VRE-Manager-" + vreName.replaceAll(" ", "-");
		Locale english = new Locale("en");
		HashMap<Locale, String> roleNames = new HashMap<Locale, String>();
		roleNames.put(english, roleName);
		return RoleLocalServiceUtil.addRole(user.getUserId(), company.getCompanyId(), roleName, roleNames, "VRE Manager of " + vreName, LIFERAY_REGULAR_ROLE_ID);		
	}

	/**
	 * Create a Regular Manager Role for the community/Organization 
	 * @param vreName
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Role addManagerRole(String roleName, String vreName, long userid) throws PortalException, SystemException {		
		Company company = getCompany();
		Locale english = new Locale("en");
		HashMap<Locale, String> roleNames = new HashMap<Locale, String>();
		roleNames.put(english, roleName);
		return RoleLocalServiceUtil.addRole(userid, company.getCompanyId(), roleName, roleNames, "VRE Manager of " + vreName, LIFERAY_REGULAR_ROLE_ID);		
	}



	/**
	 * check if the user exists in the database and has AuthZ to perform the operation
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static User validateUser(String username) throws PortalException, SystemException {
		Company company = getCompany();
		return UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), username);			
	}

	/**
	 * Use this method to associate a layout to a Group (whether organization or community)
	 * 
	 * @param group . 
	 * @param user .
	 * @param siteLayout .
	 * @return the layoutid of the yet created layout
	 * @throws PortalException .
	 * @throws SystemException .
	 */
	protected static long createLayout(Group group, User user, GCUBESiteLayout siteLayout) throws PortalException, SystemException {

		Layout layout = null;

		for (GCUBELayoutTab tab : siteLayout.getTabs()) {
			String layoutName = siteLayout.getName().replaceAll(" ", "-");
			String friendlyURL= "/"+tab.getCaption().replaceAll(" ", "-");
			_log.debug("Trying creating layout " + layoutName + " url:" + friendlyURL + " userid:" + user.getUserId() + " groupid:" + group.getGroupId());

			ServiceContext ctx = new ServiceContext();

			layout = LayoutLocalServiceUtil.addLayout(user.getUserId(), group.getGroupId(), true, 0,  tab.getCaption(), layoutName, 
					group.getDescription(), "portlet", tab.isHidden(), friendlyURL, ctx);
			if (tab.hasChildren()) {
				for (GCUBELayoutTab subtab : tab.getSubTabs()) {
					String subtabfriendlyURL= "/"+subtab.getCaption().replaceAll(" ", "-");
					Layout subLayout = LayoutLocalServiceUtil.addLayout(user.getUserId(), group.getGroupId(), true, layout.getLayoutId(),  subtab.getCaption(), layoutName, 
							group.getDescription(), "portlet", subtab.isHidden(), subtabfriendlyURL, ctx);
					String typeSettings = subtab.getLayoutTypeSettings();
					subLayout.setTypeSettings(typeSettings);
					//actually update the sub layout 
					LayoutLocalServiceUtil.updateLayout(subLayout.getGroupId(), subLayout.isPrivateLayout(), subLayout.getLayoutId(), subLayout.getTypeSettings());
					if (! subtab.isUseBorder())
						removePortletFrame(subtab, subLayout);
					_log.debug("Added subtab " + subtab.getCaption() + " to layout for parent: " + layout.getName());		
				}
			}

			_log.debug("Trying creating tab " + tab.getCaption());
			//get the typeSettings string for the liferay database from the tab Object
			String typeSettings = tab.getLayoutTypeSettings();
			//set the typesettings in the model
			layout.setTypeSettings(typeSettings);
			//actually update the layout 
			LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(), layout.getTypeSettings());
			_log.debug("Added tab " + tab.getCaption() + " to layout for Group: " + siteLayout.getName());		
			if (! tab.isUseBorder())
				removePortletFrame(tab, layout);
		}
		return layout.getLayoutId();
	}
	/**
	 * remove portlet frame for each portlet of the layout 
	 * @param tab an instance of GCUBELayoutTab
	 * @param layout the layout
	 * @throws SystemException .
	 * @throws PortalException  .
	 */
	private static void removePortletFrame(GCUBELayoutTab tab, Layout layout) throws PortalException, SystemException {
		if (tab == null || layout == null) return;
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

			PortletPreferences pPref = PortletPreferencesLocalServiceUtil.addPortletPreferences(
					companyId, ownerId, ownerType, layout.getPlid(),  lPortlet.getPortletId(), lPortlet, portletPreferencesString);

			_log.debug("Added Preference for portlet " + lPortlet.getPortletName() + " for layout plid:" + layout.getPlid());	
		}
	}

	/**
	 * 
	 * @param groupid the groupid of the organization
	 * @param username
	 * @return
	 */
	public boolean addUserToGroup(long groupid, String username) {
		UserManager uman = new LiferayUserManager();
		try {
			uman.assignUserToGroup(""+groupid, uman.getUserId(username));
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Create a Regular Manager Role for the community 
	 * @param vreName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	protected static Role createRole(String roleName, String vreName, long userid){		
		try {
			Company company = 	getCompany();
			String roletoAdd = roleName+"-" + vreName.replaceAll(" ", "-");	
			return RoleLocalServiceUtil.addRole(userid, company.getCompanyId(), roletoAdd, null, roleName +" of " + vreName, LIFERAY_REGULAR_ROLE_ID);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * read the root VO name from a property file and retuns it
	 */
	public static String getRootOrganizationName() {
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
}
