package org.gcube.vomanagement.usermanagement.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.gcube.vomanagement.usermanagement.ws.utils.StringCoupleWrapper;
import org.gcube.vomanagement.usermanagement.ws.utils.UserGroupListCoupleWrapper;
import org.gcube.vomanagement.usermanagement.ws.utils.UserRoleListCoupleWrapper;

@WebService(name = "LiferaySOAPUserManager", serviceName = "LiferaySOAPUserManager")
public class LiferaySOAPUserManager {

	LiferayUserManager liferayUserManager;

	public LiferaySOAPUserManager() {
		this.liferayUserManager = new LiferayUserManager();
	}
	
	@WebMethod
	public String getMembershipRequestComment(String userId, String groupId) throws UserManagementSystemException, UserManagementPortalException, GroupRetrievalFault {
		return this.liferayUserManager.getMembershipRequestComment(userId, groupId);
	}
	
	@WebMethod
	public List<UserModel> getMembershipRequests(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		return this.liferayUserManager.getMembershipRequests(groupId);
	}
	
	@WebMethod
	public UserModel getUser(String userId) throws UserManagementSystemException, UserRetrievalFault {
		return this.liferayUserManager.getUser(userId);
	}
	
	@WebMethod
	public UserModel getUserByScreenName(String screenName) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException {
		return this.liferayUserManager.getUserByScreenName(screenName);
	}
	
	@WebMethod
	public String getUserId(String userName) throws UserManagementSystemException {
		return this.liferayUserManager.getUserId(userName);
	}
	
	@WebMethod
	public List<UserModel> listPendingUsersByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		return this.liferayUserManager.listPendingUsersByGroup(groupIdn);
	}
	
	@WebMethod
	public List<UserModel> listUnregisteredUsersByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		return this.liferayUserManager.listUnregisteredUsersByGroup(groupIdn);
	}
	
	@WebMethod
	public List<UserModel> listUsers() throws UserManagementSystemException, UserRetrievalFault {
		return this.liferayUserManager.listUsers();
	}
	
	@WebMethod
	public List<UserGroupListCoupleWrapper> listUsersAndGroupsByRole(String roleIdn) throws UserManagementPortalException, UserManagementSystemException, RoleRetrievalFault, UserRetrievalFault {
		
		List<UserGroupListCoupleWrapper> wrapper = new ArrayList<UserGroupListCoupleWrapper>();
		Map<UserModel, List<GroupModel>> resMap = this.liferayUserManager.listUsersAndGroupsByRole(roleIdn);
		
		if (resMap != null)
		{
			Iterator<UserModel> keys = resMap.keySet().iterator();
			
			while (keys.hasNext())
			{
				UserModel key = keys.next();
				List<GroupModel> value = resMap.get(key);
				
				wrapper.add(new UserGroupListCoupleWrapper(key,value));
			}
		}
		
		return wrapper;
	}
	
	@WebMethod
	public List<UserRoleListCoupleWrapper> listUsersAndRolesByGroup(String orgIdn) throws UserManagementPortalException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		
		List<UserRoleListCoupleWrapper> wrapper = new ArrayList<UserRoleListCoupleWrapper>();
		Map<UserModel, List<RoleModel>> resMap = this.liferayUserManager.listUsersAndRolesByGroup(orgIdn);
		
		if (resMap != null)
		{
			Iterator<UserModel> keys = resMap.keySet().iterator();
			
			while (keys.hasNext())
			{
				UserModel key = keys.next();
				List<RoleModel> value = resMap.get(key);
				
				wrapper.add(new UserRoleListCoupleWrapper(key,value));
			}
		}
		
		return wrapper;
	}
	
	@WebMethod
	public List<UserModel> listUsersByGroup(String groupId) throws UserManagementSystemException, UserRetrievalFault {
		return this.liferayUserManager.listUsersByGroup(groupId);
	}
	
	@WebMethod
	public List<UserModel> listUsersByGroupAndRole(String groupIdn, String roleIdn) throws UserManagementSystemException, UserRetrievalFault {
		return this.liferayUserManager.listUsersByGroupAndRole(groupIdn, roleIdn);
	}
	
	@WebMethod
	public String getUserCustomAttributeByName(String userId, String attrName) throws UserManagementSystemException, UserRetrievalFault{
		return this.liferayUserManager.getUserCustomAttributeByName(userId, attrName);
	}
	
	@WebMethod
	public List<StringCoupleWrapper> getUserCustomAttributes(String userId) throws UserManagementSystemException, UserRetrievalFault{
		List<StringCoupleWrapper> wrapper = new ArrayList<StringCoupleWrapper>();
		
		Map<String, String> resMap = liferayUserManager.getUserCustomAttributes(userId);
		
		if (resMap != null)
		{
		
			Iterator<String> keys = resMap.keySet().iterator();
			
			while (keys.hasNext())
			{
				String key = keys.next();
				String value = resMap.get(key);
				
				wrapper.add(new StringCoupleWrapper(key, value));
			}
		
		}
		
		return wrapper;
	}
}
