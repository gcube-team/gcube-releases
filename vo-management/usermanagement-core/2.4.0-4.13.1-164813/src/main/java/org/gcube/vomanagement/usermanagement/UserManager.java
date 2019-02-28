package org.gcube.vomanagement.usermanagement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GCubeMembershipRequest;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.MembershipRequestStatus;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;


/**
 * 
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public interface UserManager {
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

	GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, String jobTitle, String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer) throws UserManagementSystemException;
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
	GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, String jobTitle,  String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset) throws UserManagementSystemException;
	/**
	 * Create the user and let you choose if you want to send him/her a mail notification and force or not the user to change his/her password. 
	 * You can also pass the avatar to set as bytes.
	 * @param autoScreenName set true if you want liferay to auto generate a screename for this user, false otherwise
	 * @param username the username of the user you want 
	 * @param email a valid email address
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param jobTitle
	 * @param backgroundSummary
	 * @param male
	 * @param portraitBytes the bytes of the avatar to use for the user
	 * @param openId user's openId
	 * @return an instance of the yet created user
	 * @throws UserManagementSystemException
	 */
	GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, String jobTitle,  String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset, byte[] portraitBytes) throws UserManagementSystemException;

	/**
	 * Create the user and let you choose if you want to send him/her a mail notification and force or not the user to change his/her password. 
	 * You can also pass the avatar to set as bytes. Moreover, contact information can be passed.
	 * @param autoScreenName
	 * @param username
	 * @param email
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param jobTitle
	 * @param location_industry
	 * @param backgroundSummary
	 * @param male
	 * @param reminderQuestion
	 * @param reminderAnswer
	 * @param sendEmail
	 * @param forcePasswordReset
	 * @param portraitBytes
	 * @param mySpacesn please note that this field will be used for Linkedin information!!
	 * @param twittersn
	 * @param facebooksn
	 * @param skypesn
	 * @param jabbersn
	 * @param aimsn
	 * @return
	 * @throws UserManagementSystemException
	 */
	GCubeUser createUser(boolean autoScreenName, 
			String username, 
			String email, 
			String firstName, 
			String middleName, 
			String lastName, 
			String jobTitle,  
			String location_industry, 
			String backgroundSummary,
			boolean male, 
			String reminderQuestion, 
			String reminderAnswer, 
			boolean sendEmail, 
			boolean forcePasswordReset,
			byte[] portraitBytes,
			String mySpacesn, // note: this will be linkedin
			String twittersn,
			String facebooksn,
			String skypesn, 
			String jabbersn,
			String aimsn
			) throws UserManagementSystemException;

	/**
	 * 
	 * @param username the username of the user you want to get
	 * @return the instance of the user
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	GCubeUser getUserByUsername(String username) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * @deprecated use getUserByUsername
	 * @param username
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	@Deprecated 
	GCubeUser getUserByScreenName(String username) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * 
	 * @param username the email of the user you want to get
	 * @return the instance of the user
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	GCubeUser getUserByEmail(String email) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * 
	 * @param userId the LR UserId
	 * @return the instance of the user
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	GCubeUser getUserById(long userId) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * 
	 * @param username the LR screenname
	 * @return the LR userId associated to this screenname
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	long getUserId(String username) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * 
	 * @param userId the LR UserId
	 * @return the user professional background
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	String getUserProfessionalBackground(long userId) throws UserManagementSystemException, UserRetrievalFault;
	/**
	 * Save the user professional background
	 * @param userId
	 * @param summary
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	void setUserProfessionalBackground(long userId, String summary) throws UserManagementSystemException, UserRetrievalFault;

	/**
	 * 
	 * @return a list of all portal active users (using indexed version search) no matter if the belong or not to a VRE, if a user is deactived is not returned
	 * @throws UserManagementSystemException
	 */
	List<GCubeUser> listUsers() throws UserManagementSystemException;
	/**
	 * 
	 * Returns a range of all the users.
	 *
	 * Useful when paginating results. 
	 * Returns a maximum of end - start instances, 0 refers to the first result in the set. 
	 * 
	 * @param start start the lower bound of the range of users
	 * @param end end the upper bound of the range of users (not inclusive)
	 * @return the range of users
	 * @throws UserManagementSystemException
	 */
	List<GCubeUser> listUsers(int start, int end) throws UserManagementSystemException;
	/**
	 * Returns the number of total users.
	 *
	 * @return the number of users
	 * @throws UserManagementSystemException 
	 */
	int getUsersCount() throws UserManagementSystemException;
	/**
	 * @param indexed set to true to use the indexed version search false for a real time query to the DB.
	 * @return a list of all portal active users no matter if the belong or not to a VRE, if a user is deactived is not returned
	 * @throws UserManagementSystemException
	 */
	List<GCubeUser> listUsers(boolean indexed) throws UserManagementSystemException;
	/**
	 * 
	 * @param keywords the keywords (space separated), which may occur in the user's first name, middle name, last name, username, or email address
	 * @param groupId
	 * @return all the users who match the keywords ordered by username (Ascendent order)
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> searchUsersByGroup(String keywords, long groupId) throws GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUsersByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUsersByGroup(long groupId, int start, int end) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * Returns the number of users in the group (VRE).
	 *
	 * @return the number of users in the group (VRE).
	 * @throws GroupRetrievalFault 
	 */
	int getGroupUsersCount(long groupId) throws GroupRetrievalFault;
	/**
	 * Retrieve a list of users having a global role (e.g., Administrator)
	 * @param roleId
	 * @return
	 */
	List<GCubeUser> listUsersByGlobalRole(long roleId);
	/**
	 * @param groupId the LR groupId
	 * @param indexed set to true to use the indexed version search false for a real time query to the DB.
	 * 
	 * @returna list of all portal active users belonging to the given groupid
	 * @throws UserManagementSystemException .
	 * @throws GroupRetrievalFault .
	 * @throws UserRetrievalFault .
	 */
	List<GCubeUser> listUsersByGroup(long groupId, boolean indexed) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @param the name of the VO or VRE (e.g. devVRE, or gcube etc.)
	 * @return
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUsersByGroupName(String name) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * @param scopeGroupId the liferay groupid number of the current Site
	 * @param userId the LR userid for which you need to fetch the contacts
	 * @return a set containing all portal active users that the user can contact/see (depending on the scopeGroupId he/she can see the users a VRE, or the users of all the VREs he/she is subscribed)
	 * 
	 * @throws UserManagementSystemException .
	 * @throws GroupRetrievalFault .
	 * @throws UserRetrievalFault .
	 */	
	Set<GCubeUser> getUserContactsByGroup(long userId, long scopeGroupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return the list of {@link GCubeMembershipRequest} with their status and users given the LR GroupId (status can be requested, approved or rejected, @see {@link MembershipRequestStatus}  
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeMembershipRequest> listMembershipRequestsByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @param requestId
	 * @return an istance of  {@link GCubeMembershipRequest}
	 */
	GCubeMembershipRequest getMembershipRequestsById(long membershipRequestId);
	/**
	 * 
	 * @param userId the LR userid	
	 * @param groupId the LR groupId the 
	 * @param status an istance of  {@link MembershipRequestStatus}
	 * @return  the list of {@link GCubeMembershipRequest} with their status and users given the LR GroupId and userId (status can be requested, approved or rejected, @see {@link MembershipRequestStatus}  
	 */
	List<GCubeMembershipRequest> getMembershipRequests(long userId, long groupId, MembershipRequestStatus status) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * request the membership to a restricted Site, no further actions are performed (no emails are sent, nor notifications)
	 * @param userId the LR userid	
	 * @param groupId the LR groupId the 
	 * @param comment a comment to the request must not be null
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	GCubeMembershipRequest requestMembership(long userId, long groupId, String comment) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * accept a membership request and optionally add the user to the group
	 * @param requestUserId the LR userid of the request			
	 * @param groupId the LR groupId
	 * @param addUserToGroup if you want it to also add the user to the group
	 * @param String the username (screenname) of the user accepting the request
	 * @param replyComment a comment attachd to the reply (must not be null)
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserManagementPortalException
	 */
	GCubeMembershipRequest acceptMembershipRequest(long requestUserId,long groupId, boolean addUserToGroup, String replyUsername, String replyComment) throws UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException;
	/**
	 * rejects a membership request
	 * @param requestUserId the LR userid of the request			
	 * @param groupId the LR groupId
	 * @param String the username (screenname) of the user accepting the request
	 * @param replyComment a comment attachd to the reply (must not be null)
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserManagementPortalException
	 */
	GCubeMembershipRequest rejectMembershipRequest(long userId, long groupId,  String replyUsername, String replyComment) throws UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return the list of {@link GCubeUser} with their GCubeRole given the LR GroupId 
	 * @throws GroupRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	Map<GCubeUser, List<GCubeRole>> listUsersAndRolesByGroup(long groupId) throws GroupRetrievalFault, UserManagementSystemException, UserRetrievalFault ;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @parmam roleId the LR roleId
	 * @return the list of {@link GCubeUser} having group and role that matches
	 * @throws GroupRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUsersByGroupAndRole(long groupId, long roleId) throws UserManagementSystemException, RoleRetrievalFault, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @parmam teamId the LR teamId
	 * @return the list of {@link GCubeUser} having teamId that matches
	 * @throws TeamRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUsersByTeam(long teamId) throws UserManagementSystemException, TeamRetrievalFault, UserRetrievalFault;
	/**
	 * this method assigns the user to the Group and perform other necessary actions (such as adding him to the HL Folder)
	 * @param groupId LR groupId
	 * @param userId LR userId
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 * @throws UserManagementPortalException
	 */
	void assignUserToGroup(long groupId, long userId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault, UserManagementPortalException;
	/**
	 * this method removes the user to the Group and perform other necessary actions (such as removing him from the HL Folder)
	 * @param groupId LR groupId
	 * @param userId LR userId
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 * @throws UserManagementPortalException
	 */
	void dismissUserFromGroup(long groupId, long userId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;
	/**
	 * 
	 * @param groupId LR groupId
	 * @return all the users of the portal not belonging to the groupId
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeUser> listUnregisteredUsersByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault;

	/**
	 * Check if the user changed his/her password from the initial one.
	 * @param email the email of the user to consider
	 * @return true if the password has been changed, false if it has been not or the user doesn't exist.
	 */
	boolean isPasswordChanged(String email);

	/**
	 * Check if a user with such email exists.
	 * @param email
	 * @return true on success, false otherwise
	 */
	boolean userExistsByEmail(String email);

	/**
	 * Returns the user's full name given his email
	 * @param email
	 * @return null on failure
	 */
	String getFullNameFromEmail(String email);

	/**
	 * Delete a user given his mail
	 * @param email
	 */
	void deleteUserByEMail(String email) throws UserManagementSystemException, UserManagementPortalException, PortalException, SystemException;
	/**
	 * Retrieve user's avatar as bytes
	 */
	byte[] getUserAvatarBytes(String screenName);

	/**
	 * Retrieve user's openId field
	 */
	String getUserOpenId(String screenName);

	/**
	 * Update screenName user's contact information
	 * @param screenName
	 * @param mySpacesn used for linkedin contact info
	 * @param twittersn
	 * @param facebooksn
	 * @param skypesn
	 * @param jabbersn
	 * @param aimsn
	 * @return true on success, false otherwise
	 */
	boolean updateContactInformation(String screenName, String mySpacesn,
			String twittersn, String facebooksn, String skypesn,
			String jabbersn, String aimsn);
	/**
	 * 
	 * @param userId
	 * @param theJob the job headline
	 * @return true if ok
	 */
	boolean updateJobTitle(long userId, String theJob);

	/**
	 * 
	 * @param attributeKey the name of the attribute you want to read its value
	 * @return the attributeKey value if existing, null otherwise
	 * @return
	 * @throws UserRetrievalFault
	 */
	Serializable readCustomAttr(long userId, String attributeKey) throws UserRetrievalFault;
	/**
	 * 
	 * @param userId
	 * @param attributeKey the name of the attribute you want to save
	 * @param value the value
	 * @throws UserRetrievalFault
	 */
	void saveCustomAttr(long userId, String attributeKey, Serializable value) throws UserRetrievalFault;

	/**
	 * Retrieve the list of user identifiers by group id.
	 * @return
	 */
	List<Long> getUserIdsByGroup(long groupId);
}
