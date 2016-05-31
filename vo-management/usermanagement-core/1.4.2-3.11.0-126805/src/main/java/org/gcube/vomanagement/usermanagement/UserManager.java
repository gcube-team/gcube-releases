package org.gcube.vomanagement.usermanagement;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;


/**
 * This interface defines the class that manages the users.
 * 
 * @author Giulio Galiero
 * @author Costantino Perciante (costantino.perciante@isti.cnr.it)
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

	/**
	 * Check if a user with such email exists.
	 * @param email
	 * @return true on success, false otherwise
	 */
	public boolean userExistsByEmail(String email);
	
	/**
	 * Returns the user's full name given his email
	 * @param email
	 * @return null on failure
	 */
	public String getFullNameFromEmail(String email);

	/**
	 * Create the user without sending notification mail and without forcing him to change the password at first login.
	 * @param autoScreenName set true if you want liferay to auto generate a screename for this user, false otherwise
	 * @param username the username of the user you want 
	 * @param email a valid email address
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param jobTitle
	 * @param backgroundSummary
	 * @param male
	 * @return an instance of the yet created user
	 * @throws UserManagementSystemException
	 */
	
	GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, String jobTitle, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer) throws UserManagementSystemException;
	/**
	 * Create the user and let you choose if you want to send him/her a mail notification and force or not the user to change his/her password.
	 * @param autoScreenName set true if you want liferay to auto generate a screename for this user, false otherwise
	 * @param username the username of the user you want 
	 * @param email a valid email address
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param jobTitle
	 * @param backgroundSummary
	 * @param male
	 * @return an instance of the yet created user
	 * @throws UserManagementSystemException
	 */
	GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, String jobTitle, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset) throws UserManagementSystemException;

	/**
	 * Check if the user changed his/her password from the one initially sent.
	 * @param email
	 * @return true if the password has been changed, false if it has not or the user doesn't exist.
	 */
	public boolean isPasswordChanged(String email);
	
	/**
	 * Delete a user given his mail
	 * @param email
	 */
	public void deleteUserByEMail(String email) throws UserManagementSystemException, UserManagementPortalException, PortalException, SystemException;
	
	/**
	 * Retrieve user's avatar as bytes
	 */
	public byte[] getUserAvatarBytes(String screenName);
	
	/**
	 * Retrieve user's openId field
	 */
	public String getUserOpenId(String screenName);
}
