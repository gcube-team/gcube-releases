package org.gcube.vomanagement.usermanagement;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;


/**
 * This interface defines the class that manages the users.
 * 
 * @author Giulio Galiero
 *
 */
public interface UserManager {

	public void createUser(UserModel usermodel) throws UserManagementSystemException, UserRetrievalFault;
	
	public void deleteUser(String userId) throws UserManagementSystemException, UserRetrievalFault;
	
	public void updateUser(UserModel user) throws UserRetrievalFault, UserManagementSystemException, UserManagementPortalException;
	
	public UserModel getUser(String userId) throws UserManagementSystemException, UserRetrievalFault ;

	public List<UserModel> listUsers() throws UserManagementSystemException, UserRetrievalFault;
	
	public List<UserModel> listUsersByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;

	public List<UserModel> listPendingUsersByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	
	public String getMembershipRequestComment(String userId, String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	
	public HashMap<UserModel, List<RoleModel>> listUsersAndRolesByGroup(String groupId) throws GroupRetrievalFault, UserManagementSystemException, UserRetrievalFault ;
	
	public HashMap<UserModel, List<GroupModel>> listUsersAndGroupsByRole(String roleId) throws UserManagementSystemException, RoleRetrievalFault, UserRetrievalFault ;
	
	public List<UserModel> listUsersByGroupAndRole(String groupId, String roleId) throws UserManagementSystemException, RoleRetrievalFault, GroupRetrievalFault, UserRetrievalFault;
	
	public void assignUserToGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault, UserManagementPortalException;
		
	public void dismissUserFromGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	
	public void requestMembership(String userId, String groupId, String comment) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	
	public List<UserModel> listUnregisteredUsersByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	
	public String getUserId(String screenName) throws UserManagementSystemException;
	
	public  List<UserModel> getMembershipRequests(String groupId) throws UserManagementSystemException,GroupRetrievalFault, UserRetrievalFault;
	
	public UserModel getUserByScreenName(String screenName) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException;

	public HashMap<String, String> getUserCustomAttributes(String userId)  throws UserManagementSystemException, UserRetrievalFault;
	
	public void setUserCustomAttributes(String userId, HashMap<String,String> hMap) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException;
	
	public String getUserCustomAttributeByName(String userId, String attrName)throws UserManagementSystemException, UserRetrievalFault;
	
	public void setUserCustomAttributeByName(String userId, String attrName, String attrValue) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException;
	
	public void denyMembershipRequest(String userId,String groupId)throws UserManagementSystemException,  GroupRetrievalFault, UserManagementPortalException;
}
