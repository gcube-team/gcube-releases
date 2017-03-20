package it.eng.rdlab.soa3.um.rest;

import it.eng.rdlab.soa3.um.rest.bean.GroupModel;
import it.eng.rdlab.soa3.um.rest.bean.OrganizationModel;
import it.eng.rdlab.soa3.um.rest.bean.RoleModel;
import it.eng.rdlab.soa3.um.rest.bean.UserModel;





import java.util.HashMap;
import java.util.Map;

import java.util.List;


/**
 * 
 * The root interface in the user management hierarchy. 
 * @author Ermanno Travaglino
 * @version 1.0
 *
 */
public interface IUserManagementService 
{

	/**
	 * 
	 * The organization interface in the user management hierarchy. 
	 * @author Ermanno Travaglino
	 * @version 1.0
	 *
	 */
	public interface OrganizationManager{

		/**
		 * Creates an organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return organization creation process response String
		 */
		public String createOrganization(String organizationName,String adminUserId, String adminPassword);


		/**
		 * Deletes an organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the organization was removed as a result of this call
		 */
		public boolean deleteOrganization(String organizationName,String adminUserId, String adminPassword);


		/**
		 * Deletes all present organizations
		 * 
		 * @param adminUserId
		 * @param adminPassword
		 * @return 0 if the organizations have been removed as a result of this call, 1 if some organization have not been removed, 2 if no organization has been removed
		 */
		public int deleteOrganizations(String adminUserId, String adminPassword);


		/**
		 * Tests if the organization organizationName exists
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the organization exists
		 */
		public boolean existsOrganization(String organizationName,String adminUserId, String adminPassword);


		/**
		 * Updates organization's attribute
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if this organization updated as a result of the call
		 */
		public boolean updateOrganization(OrganizationModel organization,String adminUserId, String adminPassword);


		/**
		 * Lists all present organizations
		 * 
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all organizations
		 */
		public List<OrganizationModel> listOrganizations(String adminUserId, String adminPassword);


		/**
		 * Gets the organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return object with specified name OrganizationModel
		 */
		public OrganizationModel getOrganizationByName(String organizationName, String adminUserId, String adminPassword);

	}

	/**
	 * 
	 * The user interface in the user management hierarchy. 
	 * @author Ermanno Travaglino
	 * @version 1.0
	 *
	 */
	public interface UserManager
	{
		/**
		 *  Status code of user credentials' change operation
		 */
		public int 	CHANGE_PASSWORD_OK = 0,
				CHANGE_PASSWORD_WRONG_USER_PASSWORD = 1,
				CHANGE_PASSWORD_EQUAL_PASSWORDS = 2,
				CHANGE_PASSWORD_INVALID_NEW_PASSWORD = 3,
				CHANGE_PASSWORD_USER_NOT_FOUND = 4,
				CHANGE_PASSWORD_GENERIC_ERROR = 5;


		/**
		 * Creates a new user by UserModel (and organizationName)
		 * 
		 * @param user
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return user creation process response String
		 */
		public String createUser(UserModel user, String organizationName,String adminUserId, String adminPassword);


		/**
		 * Deletes an user by userId (and organizationName)
		 * 
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the user was removed as a result of this call
		 */
		public boolean deleteUser(String userId, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Changes user's password by userId and old/newPassword (and organizationName)
		 * 
		 * @param userId
		 * @param oldPassword
		 * @param newPassword
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return status code as a result of this call
		 */
		public int changePassword(String userId, String oldPassword, String newPassword, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Deletes all users under an organization by organizationId 
		 * 
		 * @param organizationId
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the users have been removed as a result of this call
		 */
		public boolean deleteUsers(String organizationId,String adminUserId, String adminPassword);

		/**
		 * Gets user by userId (and organizationName)
		 * 
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return object with specified name UserModel
		 */
		public UserModel getUser (String userId, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Updates user's attributes
		 * 
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if this user updated as a result of the call
		 */
		public boolean updateUser(UserModel user, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all the users with the attributes send as parameters
		 * 
		 * @param attributes key-value couple of the attributes
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users
		 */
		public List<UserModel> listUsersByAttribute(Map<String, String> attributes,String adminUserId, String adminPassword);
		
		/**
		 * Lists all present users
		 * 
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users
		 */
		public List<UserModel> listUsers(String adminUserId, String adminPassword);

		/**
		 * Lists all users under an organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users by organization as a result of the call
		 */
		public List<UserModel>listUsersByOrganization(String organizationName,String adminUserId, String adminPassword);
		
		/**
		 * Lists all users under an organization by organizationName
		 * 
		 * @param organizationName
		 * @param attributes key-value couple of the attributes
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users by organization as a result of the call
		 */
		public List<UserModel>listUsersByOrganizationAndAttributes(String organizationName,Map<String, String> attributes,String adminUserId, String adminPassword);

		/**
		 * Lists all users with the roleName role
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users by role as a result of the call
		 */
		public List<UserModel> listUsersByRole(String roleName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all users with the groupName group
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all users by group as a result of the call
		 */
		public List<UserModel> listUsersByGroup(String groupName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all users with the associated roles (by organizationName)
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return an HashMap containing all users and associated roles as a result of the call
		 */
		public HashMap<UserModel,List<RoleModel>>  listAllUsersAndRoles(String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all users with the associated groups (by organizationName)
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return an HashMap containing all users and associated groups as a result of the call
		 */
		public HashMap<UserModel,List<GroupModel>>  listAllUsersAndGroups(String organizationName,String adminUserId, String adminPassword);

		/**
		 * Assigns a role, by roleName, to an user, by userId (and organizationName)
		 * 
		 * @param roleName
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the user's role has been assigned as a result of the call
		 */
		public boolean assignRoleToUser(String roleName, String userId,String organizationName,String adminUserId, String adminPassword);

		/**
		 * Dismisses a role, by roleName, to an user, by userId (and organizationName)
		 * 
		 * @param roleName
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the user's role has been dismissed as a result of the call
		 */
		public boolean dismissRoleToUser(String roleName, String userId,String organizationName,String adminUserId, String adminPassword);

		/**
		 * Adds an user, by userId, to a group, by groupName (and organizationName)
		 * 
		 * @param groupName
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the user has been added to group as a result of the call
		 */
		public boolean addUserToGroup(String userId, String groupName,String organizationName,String adminUserId, String adminPassword);

		/**
		 * Removes an user, by userId, from a group, by groupName (and organizationName)
		 * 
		 * @param groupName
		 * @param userId
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the user has been removed from group as a result of the call
		 */
		public boolean removeUserFromGroup(String userId, String groupName,String organizationName,String adminUserId, String adminPassword);

	}

	/**
	 * 
	 * The role interface in the user management hierarchy. 
	 * @author Ermanno Travaglino
	 * @version 1.0
	 *
	 */
	public interface RoleManager
	{
		
		/**
		 * Creates a new role by roleName (and organizationName)
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return role creation process response String
		 */
		public String createRole(String roleName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Deletes a role by roleName (and by organizationName)
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the organization was removed as a result of this call
		 */
		public boolean deleteRole(String roleName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Deletes all present roles under an organization by organizationName 
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the roles have been removed as a result of this call
		 */
		public boolean deleteRoles(String organizationName,String adminUserId, String adminPassword);

		/**
		 * Updates role's attribute
		 * 
		 * @param role
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if this role updated as a result of the call
		 */
		public boolean updateRole(RoleModel role, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Gets the role by roleName (and by organizatioName)
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return object with specified name RoleModel
		 */
		public RoleModel getRole (String roleName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all present roles
		 * 
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all roles
		 */
		public List<RoleModel> listRoles(String adminUserId, String adminPassword);

		/**
		 * Gets the roleId by roleName (and by organizationName)
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return object with specified name OrganizationModel
		 */
		public String getRoleIdByName(String roleName, String organizationName);

		/**
		 * Lists all roles under an organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all roles by organization as a result of the call
		 */
		public List<RoleModel> listRolesByOrganization(String organizationName,String adminUserId, String adminPassword);

		/**
		 * Removes all users with a specific roleName role (and by organizationName)
		 * 
		 * @param roleName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the role was removed from all users as a result of this call
		 */
		public boolean removeAllUsers(String roleName, String organizationName,String adminUserId, String adminPassword);


	}

	/**
	 * 
	 * The group interface in the user management hierarchy. 
	 * @author Ermanno Travaglino
	 * @version 1.0
	 *
	 */
	public interface GroupManager
	{
		/**
		 * Creates a new group by groupName (and organizationName)
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return group creation process response String
		 */
		public String createGroup(String groupName, String organizationName, String description,String adminUserId, String adminPassword);

		/**
		 * Deletes a group by groupName (and by organizationName)
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the group was removed as a result of this call
		 */
		public boolean deleteGroup(String groupName, String organizationName, String adminUserId, String adminPassword);

		/**
		 * Deletes all present groups under an organization by organizationName 
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the group have been removed as a result of this call
		 */
		public boolean deleteGroups(String organizationName, String adminUserId, String adminPassword);

		/**
		 * Updates group's attribute
		 * 
		 * @param group
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if this group updated as a result of the call
		 */
		public boolean updateGroup(GroupModel group, String organizationName, String adminUserId, String adminPassword);

		/**
		 * Gets the group by groupName (and by organizatioName)
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return object with specified name GroupModel
		 */
		public GroupModel getGroup(String groupName, String organizationName, String adminUserId, String adminPassword);

		/**
		 * Removes all users with a specific groupName group (and by organizationName)
		 * 
		 * @param groupName
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return true if the group was removed from all users as a result of this call
		 */
		public boolean removeAllUsers(String groupName, String organizationName,String adminUserId, String adminPassword);

		/**
		 * Lists all groups under an organization by organizationName
		 * 
		 * @param organizationName
		 * @param adminUserId
		 * @param adminPassword
		 * @return a List containing all groups by organization as a result of the call
		 */
		public List<GroupModel> listGroupsByOrganization(String organizationName,String adminUserId, String adminPassword);


	}
}
