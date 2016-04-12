package org.gcube.vomanagement.usermanagement.impl.voms;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

/**
 * VOMS plugin for the UserManager interface.
 * 
 * @author Giulio Galiero
 *
 */
public class VOMSUserManager implements UserManager {

	public void assignUserToGroup(String groupName, String userId) {
		// TODO Auto-generated method stub
		
	}

	public void createUser(UserModel usermodel) {
		// TODO Auto-generated method stub
		
	}

	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		
	}

	public void dismissUserFromGroup(String groupName, String userId) {
		// TODO Auto-generated method stub
		
	}

	public UserModel getUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UserModel> listPendingUsersByGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UserModel> listUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<UserModel, List<GroupModel>> listUsersAndGroupsByRole(
			String roleName) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<UserModel, List<RoleModel>> listUsersAndRolesByGroup(
			String groupName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UserModel> listUsersByGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UserModel> listUsersByGroupAndRole(String groupName,
			String roleName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateUser(UserModel user) {
		// TODO Auto-generated method stub
		
	}

	public List<UserModel> listUnregisteredUsersByGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void requestMembership(String userId, String groupId, String comment) {
		// TODO Auto-generated method stub
		
	}

	public String getUserId(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UserModel> getMembershipRequests(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMembershipRequestComment(String userId, String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserModel getUserByScreenName(String screenName) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> getCustomAttributes(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserCustomAttributeByName(String userId, String attrName)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> getUserCustomAttributes(String userId)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUserCustomAttributeByName(String userId, String attrName,
			String attrValue) throws UserManagementSystemException,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		
	}

	public void setUserCustomAttributes(String userId,
			HashMap<String, String> hMap) throws UserManagementSystemException,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		
	}

	public void denyMembershipRequest(String userId, String groupId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		
	}

	

}
