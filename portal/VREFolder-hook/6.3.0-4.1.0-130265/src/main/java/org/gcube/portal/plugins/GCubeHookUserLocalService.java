package org.gcube.portal.plugins;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.service.UserLocalServiceWrapper;
/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class GCubeHookUserLocalService extends UserLocalServiceWrapper {
	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(GCubeHookUserLocalService.class);
	/* (non-Java-doc)
	 * @see com.liferay.portal.service.UserLocalServiceWrapper#UserLocalServiceWrapper(UserLocalService userLocalService)
	 */
	public GCubeHookUserLocalService(UserLocalService userLocalService) {
		super(userLocalService);
		System.out.println("GCubeHookUserLocalService hook is UP & Listening ...");
	}
	/** USERS ADD TO GROUP **/
	@Override
	public void addGroupUser(long groupId, long userId)	throws com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUser(groupId, userId);
		addUserToHLVREFolder(groupId, userId);
	}
	@Override
	public void addGroupUser(long groupId, com.liferay.portal.model.User user)	throws com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUser(groupId, user.getUserId());
		addUserToHLVREFolder(groupId, user.getUserId());
	}
	@Override
	public void addGroupUsers(long groupId, long[] userIds) throws com.liferay.portal.kernel.exception.PortalException,	com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUsers(groupId, userIds);
		addUsersToHLVREFolder(groupId, userIds);
	}	
	@Override
	public void addGroupUsers(long groupId,	java.util.List<com.liferay.portal.model.User> Users) throws com.liferay.portal.kernel.exception.PortalException, com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUsers(groupId, Users);
		for (User user : Users) {
			addUserToHLVREFolder(groupId, user.getUserId());
		}
	}
	
	
	
	/** USERS REMOVAL FROM GROUP  **/
	/**
	 * this is the method used from Liferay Sites Membership Admin
	 */
	@Override
	public void unsetGroupUsers(long groupId, long[] userIds, com.liferay.portal.service.ServiceContext serviceContext)	throws com.liferay.portal.kernel.exception.PortalException, SystemException {
		super.unsetGroupUsers(groupId, userIds, serviceContext);
		removeUsersFromHLVREFolder(groupId, userIds);
	}
	
	@Override
	public  void deleteGroupUser(long groupId, long userId)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUser(groupId, userId);
		removeUserFromHLVREFolder(groupId, userId);
	}	
	@Override
	public  void deleteGroupUser(long groupId, com.liferay.portal.model.User user)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUser(groupId, user);
		System.out.println("Non ci entra proprio deleteGroupUser");
		removeUserFromHLVREFolder(groupId, user.getUserId());
	}	
	@Override
	public void deleteGroupUsers(long groupId, long[] userIds)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUsers(groupId, userIds);
		System.out.println("Non ci entra proprio");
		removeUsersFromHLVREFolder(groupId, userIds);
	}	
	@Override
	public void deleteGroupUsers(long groupId, 	java.util.List<com.liferay.portal.model.User> Users)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUsers(groupId, Users);
		System.out.println("Non ci entra proprio");
		for (User user : Users) {
			removeUserFromHLVREFolder(groupId, user.getUserId());
		}		
	}		

	//		
	/**
	 * 
	 * @param groupId
	 * @param userId
	 */
	private void addUsersToHLVREFolder(long groupId, long[] userId)  {
		for (int i = 0; i < userId.length; i++) {
			addUserToHLVREFolder(groupId, userId[i]);
		}
	}
	/**
	 * 
	 * @param groupId
	 * @param userId
	 */
	private void addUserToHLVREFolder(long groupId, long userId) {
		_log.debug("GCube VRE Folder hook addGroupUser intercepted, trying to add user to VRE Folder");
		GroupManager gm = new LiferayGroupManager();
		String currScope = ScopeProvider.instance.get();
		String scopeToset = "/"+PortalContext.getConfiguration().getInfrastructureName();
		ScopeProvider.instance.set(scopeToset);
		try {
			if (gm.isVRE(groupId)) {
				_log.debug("Group is a VRE, proceeding with association ...");
				String scope = gm.getInfrastructureScope(groupId);
				org.gcube.vomanagement.usermanagement.UserManager um = new LiferayUserManager();
				String username = um.getUserById(userId).getUsername();
				org.gcube.common.homelibrary.home.workspace.usermanager.UserManager hlUm = HomeLibrary.getHomeManagerFactory().getUserManager();
				hlUm.associateUserToGroup(scope, username);
			} else {
				_log.debug("Group is not a VRE, SKIP adding");
			}
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		ScopeProvider.instance.set(currScope);
	}
	/**
	 * 
	 * @param groupId
	 * @param userId
	 */
	private void removeUsersFromHLVREFolder(long groupId, long[] userId)  {
		for (int i = 0; i < userId.length; i++) {
			removeUserFromHLVREFolder(groupId, userId[i]);
		}
	}
	/**
	 * 
	 * @param groupId
	 * @param userId
	 */
	private void removeUserFromHLVREFolder(long groupId, long userId) {
		_log.debug("GCube VRE Folder hook removeUserFromHLVREFolder intercepted, trying to remove user from VRE Folder");
		GroupManager gm = new LiferayGroupManager();
		String currScope = ScopeProvider.instance.get();
		String scopeToset = "/"+PortalContext.getConfiguration().getInfrastructureName();
		ScopeProvider.instance.set(scopeToset);
		try {
			if (gm.isVRE(groupId)) {
				_log.debug("Group is a VRE, proceeding with removal ...");
				String scope = gm.getInfrastructureScope(groupId);
				org.gcube.vomanagement.usermanagement.UserManager um = new LiferayUserManager();
				String username = um.getUserById(userId).getUsername();
				org.gcube.common.homelibrary.home.workspace.usermanager.UserManager hlUm = HomeLibrary.getHomeManagerFactory().getUserManager();
				hlUm.removeUserFromGroup(scope, username);
			} else {
				_log.debug("Group is not a VRE, SKIP removal");
			}
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		ScopeProvider.instance.set(currScope);
	}
	
}