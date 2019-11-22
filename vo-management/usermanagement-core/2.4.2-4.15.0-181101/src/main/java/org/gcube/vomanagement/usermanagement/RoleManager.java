package org.gcube.vomanagement.usermanagement;


import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

/**
 * This interface defines the manager class that manages the Roles and the Teams.
 * Groups (Sites) can group a set of user by creating a team. 
 * The notion of a Team is somewhat similar to a Role but a Role is a portal wide entity (Role exists in any Site)
 * while a Team is restricted to a particular Site.
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public interface RoleManager {
	/**
	 * 
	 * @param userId the LR UserId, not the username
	 * @return true if the user is a portal administrator, false otherwise
	 * @throws UserRetrievalFault
	 */
	boolean isAdmin(long userId) throws UserRetrievalFault;
	/**
	 * 
	 * @param userId userId the LR UserId, not the username
	 * @param groupId the LR groupId
	 * @param roleId  the LR roleId
	 * @return
	 */
	boolean hasRole(long userId, long groupId, long roleId);
	/**
	 * 
	 * @param userId userId the LR UserId, not the username
	 * @param teamId the LR teamId
	 * @param roleId  the LR roleId
	 * @return
	 */
	boolean hasTeam(long userId, long teamId);
	/**
	 * 
	 * @param userId the LR UserId
	 * @param groupId the LR groupId
	 * @param roleId the LR roleId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws RoleRetrievalFault
	 */
	boolean assignRoleToUser(long userId, long groupId, long roleId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,RoleRetrievalFault;
	/**
	 * 
	 * @param userId the LR UserId
	 * @param groupId the LR groupId
	 * @param roleId the LR roleId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws RoleRetrievalFault
	 */
	boolean assignRolesToUser(long userId, long groupId, long[] roleId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,RoleRetrievalFault;
	/**
	 * 
	 * @param roleName
	 * @param roleDescription
	 * @return true if the role is created succesfully
	 * @throws UserManagementSystemException
	 */
	boolean createRole(String roleName, String roleDescription) throws UserManagementSystemException;
	/**
	 * 
	 * @param roleId
	 * @return true if the role is deleted succesfully, false otherwise
	 * @throws UserManagementSystemException
	 * @throws RoleRetrievalFault
	 */
	boolean deleteRole(long roleId) throws UserManagementSystemException, RoleRetrievalFault ;
	/**
	 * @param userId
	 * @param groupId
	 * @param roleId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws RoleRetrievalFault
	 */
	boolean removeRoleFromUser(long userId, long groupId, long roleId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,RoleRetrievalFault;
	/**
	 * @param userId
	 * @param groupIds
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 */
	boolean removeAllRolesFromUser(long userId, long... groupIds) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault;
	/**
	 * @param roleId
	 * @param roleName
	 * @param roleDescription
	 * @return
	 */
	GCubeRole updateRole(long roleId, String roleName, String roleDescription) throws RoleRetrievalFault;
	/**
	 * @param roleId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws RoleRetrievalFault
	 */
	GCubeRole getRole(long roleId) throws UserManagementSystemException, RoleRetrievalFault;
	
	/**
	 * @param roleName
	 * @param groupId the LR groupId
	 * @return an instance of {@link GcubeRole} if the roleName exists, null otherwise
	 * @throws RoleRetrievalFault if the roleName does not exist
	 * @throws GroupRetrievalFault if the groupId does not exist
	 */
	GCubeRole getRole(String roleName, long groupId) throws RoleRetrievalFault, GroupRetrievalFault;
	/**
	 * * @param roleName
	 * @param groupId the LR groupId
	 * @return the LR RoleId if the roleName exists
	 * @throws RoleRetrievalFault if the roleName does not exist
	 * @throws GroupRetrievalFault if the groupId does not exist
	 */
	long getRoleId(String roleName, long groupId) throws RoleRetrievalFault, GroupRetrievalFault;
	/**
	 * * @param roleName
	 * @return the LR RoleId if the roleName exists
	 * @throws RoleRetrievalFault if the roleName does not exist
	 */
	long getRoleIdByName(String roleName) throws RoleRetrievalFault;
	/**
	 * 
	 * @return a list of {@link GcubeRole} independent from the roleType
	 */
	List<GCubeRole> listAllRoles();
	/**
	 * @return a list of {@link GcubeRole} of type Site Role (Type=2)
	 */
	List<GCubeRole> listAllGroupRoles();
	/** 
	 * @param groupId
	 * @param userId
	 * @return a list of {@link GcubeRole} of type Site Role (Type=2)
	 * @throws GroupRetrievalFault
	 * @throws UserRetrievalFault
	 */
	List<GCubeRole> listRolesByUserAndGroup(long userId, long groupId) throws GroupRetrievalFault,UserRetrievalFault;
	
	
	/*******************************
	 * 
	 *  TEAM MANAGEMENTS PART
	 *  
	 * The notion of a Team is somewhat similar to a Role but a Role is a portal wide entity, (Role exists in any Site)
	 * while a Team is restricted to a particular Site.
	 * 
	 * */
	
	
	/**
	 * @param userId the username of who is creating the team
	 * @param groupId the site group id where the team exists
	 * @param teamName the name you want to assign to this team
	 * @param teamDescription
	 * @return the GCubeTeam if the team is created succesfully, null otherwise
	 * @throws UserManagementSystemException
	 */
	GCubeTeam createTeam(long creatorUserId, long groupId, String teamName, String teamDescription) throws GroupRetrievalFault, TeamRetrievalFault, UserManagementSystemException;
	/**
	 * it will crate the Team as Administrator, use the other method to pass the userId
	 * @param groupId the site group id where the team exists
	 * @param teamName the name you want to assign to this team
	 * @param teamDescription
	 * @return the GCubeTeam if the team is created succesfully, null otherwise
	 * @throws UserManagementSystemException
	 */
	GCubeTeam createTeam(long groupId, String teamName, String teamDescription) throws GroupRetrievalFault, TeamRetrievalFault, UserManagementSystemException;
	
	/**
	 * 
	 * @param groupId
	 * @param teamName
	 * @return
	 * @throws GroupRetrievalFault
	 * @throws TeamRetrievalFault
	 */
	GCubeTeam getTeam(long groupId, String teamName) throws GroupRetrievalFault, TeamRetrievalFault;
	/**
	 * 
	 * @param teamId
	 * @return the GCubeTeam istance
	 * @throws UserManagementSystemException
	 * @throws RoleRetrievalFault
	 */
	GCubeTeam getTeam(long teamId) throws UserManagementSystemException, TeamRetrievalFault;
	/**
	 * @param groupId the site group id where the team exists
	 * @param teamId the LR team Id
	 * @return the team instance that was removed
	 * @throws UserManagementSystemException
	 * @throws RoleRetrievalFault
	 */
	GCubeTeam deleteTeam(long teamId) throws UserManagementSystemException, TeamRetrievalFault ;
	/**
	 * 
	 * @param groupId the LR GroupId of the Site
	 * @return true if the teams are deleted succesfully, false otherwise
	 * @throws UserManagementSystemException
	 */
	boolean deleteTeams(long groupId) throws UserManagementSystemException;
	/**
	 * delete a user from a list of teams
	 * @param userId
	 * @param teamIds
	 * @return
	 * @throws UserRetrievalFault
	 * @throws TeamRetrievalFault
	 */
	boolean deleteUserTeams(long userId, long[] teamIds);
	/**
	 * delete a user from a list of teams
	 * @param userId
	 * @param teams
	 * @return
	 * @throws UserRetrievalFault
	 * @throws TeamRetrievalFault
	 */
	boolean deleteUserTeams(long userId, List<GCubeTeam> teams);
	/** 
	 * @param roleId
	 * @param roleName
	 * @param roleDescription
	 * @return
	 */
	GCubeTeam updateTeam(long teamId, String teamName, String teamDescription) throws TeamRetrievalFault;
	/**
	 * associate or not associate teams to a user
	 * @param userId
	 * @param teamIds
	 * @return
	 * @throws TeamRetrievalFault if a system exception occurred or a team does not exists
	 */
	boolean setUserTeams(long userId, long[] teamIds) throws TeamRetrievalFault;
	/**
	 * associate one team to the existing teams of user
	 * @param userId
	 * @param teamId
	 * @return
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws TeamRetrievalFault
	 */
	boolean assignTeamToUser(long userId, long teamId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,TeamRetrievalFault;
	/**
	 * @return a list of {@link GCubeTeam} belonging to a give group
	 */
	List<GCubeTeam> listTeamsByGroup(long groupId) throws GroupRetrievalFault;
	/**
	 * 
	 * @param userId the LR userId of the Site
	 * @param groupId the LR GroupId of the Site
	 * @return a list of {@link GCubeTeam} belonging to a user in a given group
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 */
	List<GCubeTeam> listTeamsByUserAndGroup(long userId, long groupId) throws UserRetrievalFault, GroupRetrievalFault;
}
