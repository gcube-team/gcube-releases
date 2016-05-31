package org.gcube.portal.custom.communitymanager.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;

import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 *
 */
public class OrganizationManagerImpl extends OrganizationsUtil {

	private static Log _log = LogFactoryUtil.getLog(OrganizationManagerImpl.class);

	/**
	 * FIXME public as generic resource  
	 */
	protected static final String ORGANIZATION_DEFAULT_LOGO = "/org/gcube/portal/custom/communitymanager/resources/default_logo.png";
	private static final String CATEGORY = "Virtualgroup";

	public static List<String> getVirtualGroups() throws PortalException, SystemException {
		try {
			ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(OrganizationsUtil.getCompany().getCompanyId(), Organization.class.getName());
			String[] groups = (String[]) expandoBridge.getAttributeDefault("Virtualgroup");	
			return Arrays.asList(groups);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	

	public static String getVirtualGroupName(Organization organization){
		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.debug("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.debug("Setting Permission ok!");

			if (organization.getExpandoBridge().getAttribute(CATEGORY) == null ||  organization.getExpandoBridge().getAttribute(CATEGORY).equals("")) {
				_log.warn(String.format("Attribute %s not initialized.", CATEGORY)); 
				return null;
			} else {
				String[] values = (String[]) organization.getExpandoBridge().getAttribute(CATEGORY);   
				return values[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception ";
		}
	}
	
	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the organizationid of the created VO
	 */
	public static long createVO(String username, String voName, String voDesc, long parentid, GCUBESiteLayout siteLayout,  String themeid)  {
		Group voToCreate = null;
		try {			
			//create the Group		
			User creator = validateUser(username);
			GroupManager gm = new LiferayGroupManager();			
			GroupModel groupModel = null;
			_log.info("createVO " + voName + " with parentid " + parentid);	
			groupModel = gm.createVRE(voName, ""+parentid, ""+creator.getUserId(), "Description for "+voName);
			long groupModelid = Long.parseLong(groupModel.getGroupId());			
			voToCreate = OrganizationLocalServiceUtil.getOrganization(groupModelid).getGroup();

			//associate the layout to the group
			createLayout(voToCreate, validateUser(username), siteLayout);

			Theme themeToApply = ThemeLocalServiceUtil.getTheme(getCompany().getCompanyId(), themeid, false);

			//update theme
			LayoutSetLocalServiceUtil.updateLookAndFeel(voToCreate.getGroupId(), themeToApply.getThemeId(), "", "", false);
			_log.debug("LayoutSet Theme with id " + themeid +  " Applied Correctly");

			//update logo
			InputStream is = OrganizationManagerImpl.class.getResourceAsStream(ORGANIZATION_DEFAULT_LOGO);
			FileInputStream fis = new FileInputStream(writeTempLogo(is));
			LayoutSetLocalServiceUtil.updateLogo(voToCreate.getGroupId(), true, true, fis);

			_log.debug("Adding the Admin Role VO-Admin for this VO");			
			//add the role ADMIN
			UserManager uman = new LiferayUserManager();
			long uid = Long.parseLong(uman.getUserId(username));
			Role created = OrganizationsUtil.createRole("VO-Admin", voName, uid);
			_log.debug("Admin Role VO-Admin Created Successfully");	

			uman.assignUserToGroup(""+voToCreate.getClassPK(), ""+uid);
			_log.debug("Added user " + username + " to group " + voName + " with Success");	

			_log.debug("Assigning Role:  VO-Admin");		
			RoleManager rm = new LiferayRoleManager();
			rm.assignRoleToUser(""+voToCreate.getClassPK(), ""+created.getRoleId(), ""+uid);
			_log.debug("Admin Role VO-Admin Associated to user " + username +  " .... returning ...");		

		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.info("Created"  + voName  + " with id " + voToCreate.getOrganizationId());		
		return voToCreate.getOrganizationId();
	}


	/**
	 * create a VO with no parent (root VO)
	 * @param voName the voName
	 * @param voDesc -
	 * @return the id of the created VO
	 */
	public static long createVO(String username, String voName, String voDesc, GCUBESiteLayout siteLayout, String themeid) {
		return createVO(username, voName, voDesc, 0, siteLayout, themeid);
	}
	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the organizationid of the created VO
	 */
	public static long createVRE(String username, String voName, String voDesc, long parentid, GCUBESiteLayout siteLayout, String themeid) {
		
		Group voToCreate = null;
		try {			
			//create the Group		
			User creator = validateUser(username);
			GroupManager gm = new LiferayGroupManager();			
			GroupModel groupModel = null;
			_log.info("createVRE " + voName + " with parentid " + parentid);	
			groupModel = gm.createVRE(voName, ""+parentid, ""+creator.getUserId(), voDesc);
			long groupModelid = Long.parseLong(groupModel.getGroupId());			
			voToCreate = OrganizationLocalServiceUtil.getOrganization(groupModelid).getGroup();

			//associate the layout to the group
			createLayout(voToCreate, validateUser(username), siteLayout);

			Theme themeToApply = ThemeLocalServiceUtil.getTheme(getCompany().getCompanyId(), themeid, false);

			//update theme
			LayoutSetLocalServiceUtil.updateLookAndFeel(voToCreate.getGroupId(), themeToApply.getThemeId(), "", "", false);
			_log.debug("LayoutSet Theme with id " + themeid +  " Applied Correctly");

			//update logo
			InputStream is = OrganizationManagerImpl.class.getResourceAsStream(ORGANIZATION_DEFAULT_LOGO);
			FileInputStream fis = new FileInputStream(writeTempLogo(is));
			LayoutSetLocalServiceUtil.updateLogo(voToCreate.getGroupId(), true, true, fis);

			_log.debug("Adding the MANAGER Role VRE-Manager for this VRE");			
			//add the role ADMIN
			UserManager uman = new LiferayUserManager();
			long uid = Long.parseLong(uman.getUserId(username));
			Role created = OrganizationsUtil.createRole("VRE-Manager", voName, uid);
			_log.debug("Admin Role VRE-Manager Created Successfully");	

			uman.assignUserToGroup(""+voToCreate.getClassPK(), ""+uid);
			_log.debug("Added user " + username + " to group " + voName + " with Success");	

			_log.debug("Assigning Role:  VRE-Manager");		
			RoleManager rm = new LiferayRoleManager();
			rm.assignRoleToUser(""+voToCreate.getClassPK(), ""+created.getRoleId(), ""+uid);
			_log.debug("Admin Role VRE-Manager Associated to user " + username +  " .... returning ...");		

		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.info("Created"  + voName  + " with id " + voToCreate.getOrganizationId());		
		return voToCreate.getOrganizationId();
	}
	
	/**
	 * 
	 * @return the built layout of a rootVO
	 * @throws SystemException .
	 * @throws PortalException .
	 */
	public static GCUBESiteLayout getBaseLayout(String voName, boolean isVO, String username) throws PortalException, SystemException {
		GCUBESiteLayout siteLayout = null;
		String email = validateUser(username).getEmailAddress();
		siteLayout = new GCUBESiteLayout(OrganizationManagerImpl.getCompany(), voName, email);			
		siteLayout.addTab(new GCUBELayoutTab(voName, GCUBELayoutType.ONE_COL,
				new GCUBEPortlet("gCube Loggedin", PortletsIdManager.getLRPortletId(PortletsIdManager.GCUBE_LOGGEDIN))));

		//create tab Users and Roles with 2 subtabs
		GCUBELayoutTab usersAndRoles = new GCUBELayoutTab("Administration", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Navigation", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_NAVIGATION)));
		GCUBELayoutTab usersTab = new GCUBELayoutTab("Manage User and Requests", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Users", PortletsIdManager.getLRPortletId(PortletsIdManager.GCUBE_USERS_MANAGE)));
		GCUBELayoutTab usersAddTab = new GCUBELayoutTab("Add new Users", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Users", PortletsIdManager.getLRPortletId(PortletsIdManager.GCUBE_ADD_USERS_MANAGE)));
		GCUBELayoutTab rolesTab = new GCUBELayoutTab("Add new Roles", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("Roles", PortletsIdManager.getLRPortletId(PortletsIdManager.GCUBE_ROLES_MANAGE)));
		usersAndRoles.addSubTab(usersTab);
		usersAndRoles.addSubTab(usersAddTab);
		usersAndRoles.addSubTab(rolesTab);
		//add the tab
		siteLayout.addTab(usersAndRoles);
		if (isVO)
			siteLayout.addTab(new GCUBELayoutTab("Resources Management", GCUBELayoutType.ONE_COL, 
					new GCUBEPortlet("Resources Management", PortletsIdManager.getLRPortletId(PortletsIdManager.RESOURCES_MANAGEMENT))));
		else
			siteLayout.addTab(new GCUBELayoutTab("Calendar", GCUBELayoutType.ONE_COL, 
					new GCUBEPortlet("Calendar", PortletsIdManager.getLRPortletId(PortletsIdManager.LR_CALENDAR)), true));
		return siteLayout;
	}


	/***
	 * simple helper method
	 * @param inputStream
	 * @return
	 */
	private static File writeTempLogo(InputStream inputStream) {
		try {
			File temp = File.createTempFile("logoimage", ".png");
			// write the inputStream to a FileOutputStream
			FileOutputStream out = new FileOutputStream(temp);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) 
				out.write(bytes, 0, read);
			inputStream.close();
			out.flush();
			out.close();
			return temp;
		} catch (IOException e) {
			return null;
		}
	}	
	/**
	 *  {@inheritDoc}
	 */
	public static Boolean readOrganizationCustomAttribute(String username, Organization currOrg, String attrToCheck) {
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
	private static void setOrgCustomAttribute(String username,  Organization currOrg, String attribute2Set) {
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
}
