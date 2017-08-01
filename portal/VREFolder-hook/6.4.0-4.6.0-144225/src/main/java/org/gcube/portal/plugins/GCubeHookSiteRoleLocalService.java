package org.gcube.portal.plugins;

import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.Role;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalService;
import com.liferay.portal.service.UserGroupRoleLocalServiceWrapper;
import com.liferay.portal.service.UserLocalServiceUtil;

public class GCubeHookSiteRoleLocalService extends UserGroupRoleLocalServiceWrapper {
	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(GCubeHookSiteRoleLocalService.class);
	private GroupManager gm;
	public GCubeHookSiteRoleLocalService(UserGroupRoleLocalService userGroupRoleLocalService) {
		super(userGroupRoleLocalService);
		gm = new LiferayGroupManager();
		System.out.println("GCubeHookSiteRoleLocalService hook is UP & Listening ...");
	}

	@Override
	public java.util.List<com.liferay.portal.model.UserGroupRole> addUserGroupRoles(
			long userId, long groupId, long[] roleIds)
					throws com.liferay.portal.kernel.exception.SystemException {			
		List<UserGroupRole> toReturn = super.addUserGroupRoles(userId, groupId,	roleIds);
		try {
			_log.debug("Check if addUserGroupRoles is done in a VRE");
			if (gm.isVRE(groupId)) {
				_log.debug("addUserGroupRoles performed in a VRE, groupId=" + groupId);
				boolean vreManagerRolePresent = false;
				for (int i = 0; i < roleIds.length; i++) {
					Role role = RoleLocalServiceUtil.getRole(roleIds[i]);
					if (role.getName().compareTo(GCubeRole.VRE_MANAGER_LABEL) == 0) { 
						_log.info("User is being promoted (or was) as VREFolder Administrator, userId=" + userId + " on Site groupId="+groupId);
						vreManagerRolePresent = true;
						break;
					}					
				} 
				setVREFolderAdministrator(userId, groupId, vreManagerRolePresent);
			} else {
				_log.debug("addUserGroupRoles NOT done in a VRE, groupId=" + groupId);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private void setVREFolderAdministrator(long userId, long groupId, boolean enable) throws Exception {

		String scopeVREFolder = gm.getInfrastructureScope(groupId);

		String currScope = ScopeProvider.instance.get();
		String scopeToset = "/"+PortalContext.getConfiguration().getInfrastructureName();
		ScopeProvider.instance.set(scopeToset);		

		String username = UserLocalServiceUtil.getUser(userId).getScreenName();
		_log.debug("User " + username + " is going to be VRE Folder Admin?" + enable);
		UserManager hlUm = HomeLibrary.getHomeManagerFactory().getUserManager();
		if (enable)
			hlUm.setAdministrator(scopeVREFolder, username);
		else
			hlUm.removeAdministrator(scopeVREFolder, username);

		ScopeProvider.instance.set(currScope);
	}
}
