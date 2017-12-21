package org.gcube.portal.custom.communitymanager.impl;


import java.io.InputStream;

import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Theme;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalServiceUtil;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 *
 */
public class GCubeSiteManagerImpl extends SiteManagerUtil {

	private static Log _log = LogFactoryUtil.getLog(GCubeSiteManagerImpl.class);

	/**
	 * FIXME public as generic resource  
	 */
	public static final String SITE_DEFAULT_LOGO = "/org/gcube/portal/custom/communitymanager/resources/default_logo.png";


	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the organizationid of the created VO
	 */
	public static long createVO(String username, String voName, String voDesc, long parentid, GCUBESiteLayout siteLayout,  String themeid)  {
		Group voToCreate = null;
		try {			
			GroupManager gm = new LiferayGroupManager();			
			GCubeGroup groupModel = null;
			_log.info("createVO " + voName + " with parentid " + parentid);	
			if (parentid == 0)
				groupModel = gm.createRootVO(voName, voDesc);
			else
				groupModel = gm.createVO(voName, parentid, voDesc);
			long groupModelid = groupModel.getGroupId();			
			voToCreate = GroupLocalServiceUtil.getGroup(groupModelid);

			//associate the layout to the group
			createLayout(voToCreate, validateUser(username), siteLayout);

			Theme themeToApply = ThemeLocalServiceUtil.getTheme(getCompany().getCompanyId(), themeid, false);

			//update theme
			LayoutSetLocalServiceUtil.updateLookAndFeel(voToCreate.getGroupId(), themeToApply.getThemeId(), "", "", false);
			_log.debug("LayoutSet Theme with id " + themeid +  " Applied Correctly");

			//update logo
			InputStream is = GCubeSiteManagerImpl.class.getResourceAsStream(SITE_DEFAULT_LOGO);
			//FileInputStream fis = new FileInputStream(writeTempLogo(is));
			LayoutSetLocalServiceUtil.updateLogo(voToCreate.getGroupId(), true, true, is);

			_log.debug("Adding the Admin Role VO-Admin for this VO");			
			//add the role ADMIN
			UserManager uman = new LiferayUserManager();
			long uid = uman.getUserId(username);
			
	
			uman.assignUserToGroup(groupModel.getGroupId(), uid);
			_log.debug("Added user " + username + " to group " + voName + " with Success");	

			_log.debug("Assigning Role: " + GCubeRole.VO_ADMIN_LABEL);		
			RoleManager rm = new LiferayRoleManager();
			long roleId = -1;
			for (GCubeRole role : rm.listAllGroupRoles()) {
				if (role.getRoleName().compareTo(GCubeRole.VO_ADMIN_LABEL) == 0) {
					roleId = role.getRoleId();
					break;
				}
			}
			rm.assignRoleToUser(uid, groupModel.getGroupId(), roleId);
			_log.debug("Admin Role VO-Admin Associated to user " + username +  " .... returning ...");		

		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.info("Created"  + voName  + " with id " + voToCreate.getGroupId());		
		return voToCreate.getGroupId();
	}


	/**
	 * create a VO with no parent (root VO)
	 * @param voName the voName
	 * @param voDesc -
	 * @return the id of the created VO
	 */
	public static long createRootVO(String username, String voName, String voDesc, GCUBESiteLayout siteLayout, String themeid) {
		return createVO(username, voName, voDesc, 0, siteLayout, themeid);
	}
	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the organizationid of the created VO
	 */
	public static long createVRE(String username, String vreName, String voDesc, long parentid, GCUBESiteLayout siteLayout, String themeid) {		
		Group vreToCreate = null;
		try {						
			GroupManager gm = new LiferayGroupManager();			
			GCubeGroup groupModel = null;
			_log.info("createVRE " + vreName + " with parentid " + parentid);	
			groupModel = gm.createVRE(vreName, parentid, voDesc);
			long groupModelid = groupModel.getGroupId();			
			vreToCreate = GroupLocalServiceUtil.getGroup(groupModelid);
			//associate the layout to the group
			createLayout(vreToCreate, validateUser(username), siteLayout);

			Theme themeToApply = ThemeLocalServiceUtil.getTheme(getCompany().getCompanyId(), themeid, false);

			//update theme
			LayoutSetLocalServiceUtil.updateLookAndFeel(vreToCreate.getGroupId(), themeToApply.getThemeId(), "", "", false);
			_log.debug("LayoutSet Theme with id " + themeid +  " Applied Correctly");

			//update logo
			InputStream is = GCubeSiteManagerImpl.class.getResourceAsStream(SITE_DEFAULT_LOGO);
			//FileInputStream fis = new FileInputStream(writeTempLogo(is));
			LayoutSetLocalServiceUtil.updateLogo(vreToCreate.getGroupId(), true, true, is);

			_log.debug("Adding the Admin Role VRE-Manager for this VRE");			
			//add the role ADMIN
			UserManager uman = new LiferayUserManager();
			long uid = uman.getUserId(username);
			
	
			uman.assignUserToGroup(groupModel.getGroupId(), uid);
			_log.debug("Added user " + username + " to group " + vreName + " with Success");	

			_log.debug("Assigning Role: " + GCubeRole.VRE_MANAGER_LABEL);		
			RoleManager rm = new LiferayRoleManager();
			long roleId = -1;
			for (GCubeRole role : rm.listAllGroupRoles()) {
				if (role.getRoleName().compareTo(GCubeRole.VRE_MANAGER_LABEL) == 0) {
					roleId = role.getRoleId();
					break;
				}
			}
			rm.assignRoleToUser(uid, groupModel.getGroupId(), roleId);
			_log.debug("Admin Role VRE-Manager Associated to user " + username +  " .... returning ...");		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.info("Created"  + vreName  + " with id " + vreToCreate.getGroupId());		
		return vreToCreate.getOrganizationId();
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
		siteLayout = new GCUBESiteLayout(GCubeSiteManagerImpl.getCompany(), voName, email);			
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
}
