package org.gcube.portal.plugins;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.plugins.thread.CheckShareLatexUserThread;
import org.gcube.portal.plugins.thread.RemoveUserTokenFromVREThread;
import org.gcube.portal.plugins.thread.UpdateUserToLDAPGroupThread;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
	private static Log _log = LogFactoryUtil.getLog(GCubeHookUserLocalService.class);
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
		addUserToVRERelatedServices(groupId, userId);
	}
	@Override
	public void addGroupUser(long groupId, com.liferay.portal.model.User user)	throws com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUser(groupId, user.getUserId());
		addUserToVRERelatedServices(groupId, user.getUserId());
	}
	@Override
	public void addGroupUsers(long groupId, long[] userIds) throws com.liferay.portal.kernel.exception.PortalException,	com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUsers(groupId, userIds);
		addUsersToVRERelatedServices(groupId, userIds);
	}	
	@Override
	public void addGroupUsers(long groupId,	java.util.List<com.liferay.portal.model.User> Users) throws com.liferay.portal.kernel.exception.PortalException, com.liferay.portal.kernel.exception.SystemException {
		super.addGroupUsers(groupId, Users);
		for (User user : Users) {
			addUserToVRERelatedServices(groupId, user.getUserId());
		}
	}



	/** USERS REMOVAL FROM GROUP  **/
	/**
	 * this is the method used from Liferay Sites Membership Admin
	 */
	@Override
	public void unsetGroupUsers(long groupId, long[] userIds, com.liferay.portal.service.ServiceContext serviceContext)	throws com.liferay.portal.kernel.exception.PortalException, SystemException {
		super.unsetGroupUsers(groupId, userIds, serviceContext);
		removeUsersFromVRERelatedServices(groupId, userIds);
	}

	@Override
	public  void deleteGroupUser(long groupId, long userId)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUser(groupId, userId);
		removeUserFromVREReleatedServices(groupId, userId);
	}	
	@Override
	public  void deleteGroupUser(long groupId, com.liferay.portal.model.User user)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUser(groupId, user);
		removeUserFromVREReleatedServices(groupId, user.getUserId());
	}	
	@Override
	public void deleteGroupUsers(long groupId, long[] userIds)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUsers(groupId, userIds);
		removeUsersFromVRERelatedServices(groupId, userIds);
	}	
	@Override
	public void deleteGroupUsers(long groupId, 	java.util.List<com.liferay.portal.model.User> Users)	throws com.liferay.portal.kernel.exception.SystemException {
		super.deleteGroupUsers(groupId, Users);
		for (User user : Users) {
			removeUserFromVREReleatedServices(groupId, user.getUserId());
		}		
	}		

	//		
	/**
	 * 
	 * @param groupId
	 * @param userId
	 */
	private void addUsersToVRERelatedServices(long groupId, long[] userId)  {
		for (int i = 0; i < userId.length; i++) {
			addUserToVRERelatedServices(groupId, userId[i]);
		}
	}
	/**
	 * this method add the user joiing a VRE to all the related services she was associated (VREFolder, LDAP Group, SecurityToken is not necessary in thi case)
	 * @param groupId
	 * @param userId
	 */
	private void addUserToVRERelatedServices(long groupId, long userId) {
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
				//add the user to LDAP Group
				Thread tLdap = new Thread(new UpdateUserToLDAPGroupThread(username, scope, groupId, false));
				tLdap.start();	
				//add the user to shareLatex
				Thread t = new Thread(new CheckShareLatexUserThread(username, scope));
				t.start();
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
	private void removeUsersFromVRERelatedServices(long groupId, long[] userId)  {
		for (int i = 0; i < userId.length; i++) {
			removeUserFromVREReleatedServices(groupId, userId[i]);
		}
	}
	/**
	 * this method remove the user leaving a VRE from all the related services she was associated (VREFolder, LDAP Group and SecurityToken)
	 * @param groupId
	 * @param userId
	 */
	private void removeUserFromVREReleatedServices(long groupId, long userId) {
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
				//remove the user to LDAP Group
				Thread tLdap = new Thread(new UpdateUserToLDAPGroupThread(username, scope, groupId, true));
				tLdap.start();				
				org.gcube.common.homelibrary.home.workspace.usermanager.UserManager hlUm = HomeLibrary.getHomeManagerFactory().getUserManager();
				hlUm.removeUserFromGroup(scope, username);
				Thread tToken = new Thread(new RemoveUserTokenFromVREThread(username, scope));
				tToken.start();			
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