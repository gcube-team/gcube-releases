package org.gcube.vomanagement.usermanagement.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.DuplicateTeamException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

public class LiferayRoleManager implements RoleManager {
	/**
	 * logger
	 */
	private static final Log _log = LogFactoryUtil.getLog(LiferayRoleManager.class);
	private static final String ADMINISTRATOR = "Administrator";
	private static final int ROLE_TYPE = 2;  // role type. 1=regular, 2=site, 3=organization 

	//simple role mapping
	protected static GCubeRole mapLRRole(Role r) throws PortalException, SystemException {
		if (r != null) {
			return new GCubeRole( r.getRoleId(), r.getName(), r.getDescription());
		}
		else 
			return null;
	}

	//simple team mapping
	protected static GCubeTeam mapLRTeam(Team t) throws PortalException, SystemException {
		if (t != null) {
			return new GCubeTeam(t.getGroupId(), t.getTeamId(), t.getName(), t.getDescription(), t.getUserId(), t.getCreateDate(), t.getModifiedDate());
		}
		else 
			return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAdmin(long userId) throws UserRetrievalFault {
		try {
			UserLocalServiceUtil.getUser(userId);
		} catch (PortalException e) {
			throw new UserRetrievalFault("User not existing", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}		
		try {
			long roleId = RoleLocalServiceUtil.getRole(ManagementUtils.getCompany().getCompanyId(), ADMINISTRATOR).getRoleId();
			return UserLocalServiceUtil.hasRoleUser(roleId, userId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeRole getRole(String roleName, long groupId) throws RoleRetrievalFault, GroupRetrievalFault {
		try {
			GroupLocalServiceUtil.getGroup(groupId);
			List<Role> roles = RoleLocalServiceUtil.getGroupRelatedRoles(groupId);
			for (Role role : roles) {
				_log.debug(role.toString());
				if (role.getName().compareTo(roleName) == 0)
					return (mapLRRole(role));
			}
		} catch (PortalException e) {
			_log.warn(roleName + " Role not existing");
		} catch (SystemException e) {
			throw new RoleRetrievalFault(e.getMessage(), e);
		}	
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeRole getRole(long roleId) throws RoleRetrievalFault, RoleRetrievalFault {
		try {
			return mapLRRole(RoleLocalServiceUtil.getRole(roleId));
		} catch (PortalException e) {
			_log.warn(roleId + " Role id not existing");
		} catch (SystemException e) {
			_log.error("Liferay SystemException");
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getRoleId(String roleName, long groupId) throws RoleRetrievalFault, GroupRetrievalFault {
		return getRole(roleName, groupId).getRoleId();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getRoleIdByName(String roleName) throws RoleRetrievalFault {
		try {
			Role toReturn = RoleLocalServiceUtil.getRole(ManagementUtils.getCompany().getCompanyId(), roleName);
			return toReturn.getRoleId();
		} catch (PortalException e) {
			throw new RoleRetrievalFault("Role not existing: " + roleName);
		} catch (SystemException e) {
			_log.error("Liferay SystemException");
		}
		return -1;
	}

	@Override
	public boolean hasRole(long userId, long groupId, long roleId) {
		try {
			List<GCubeRole> roles = listRolesByUserAndGroup(userId, groupId);
			for (GCubeRole gCubeRole : roles) {
				if (gCubeRole.getRoleId() == roleId)
					return true;
			}
		} catch (GroupRetrievalFault | UserRetrievalFault e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean hasTeam(long userId, long teamId) {		
		try {
			return UserLocalServiceUtil.hasTeamUser(teamId, userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assignRoleToUser(long userId, long groupId, long roleId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, RoleRetrievalFault {
		try {			
			_log.debug("Trying to assign role to " + UserLocalServiceUtil.getUser(userId).getFullName() +" in group " + groupId);
			long[] roleIds = {roleId};
			return (UserGroupRoleLocalServiceUtil.addUserGroupRoles(userId, groupId, roleIds).size() > 0);
		} catch (PortalException e) {
			throw new UserRetrievalFault("User, not existing or roleId could not be found", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}		
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assignRolesToUser(long userId, long groupId, long[] roleIds) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, RoleRetrievalFault {
		try {			
			_log.debug("Trying to assign role to " + UserLocalServiceUtil.getUser(userId).getFullName() +" in group " + groupId);
			return (UserGroupRoleLocalServiceUtil.addUserGroupRoles(userId, groupId, roleIds).size() > 0);
		} catch (PortalException e) {
			throw new UserRetrievalFault("User, not existing, or group/roleId could not be found", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}		
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createRole(String roleName, String roleDescription) throws UserManagementSystemException {
		_log.debug("Check  createRole " + roleName);
		long existingroleId = -1;
		try {
			existingroleId = getRoleIdByName(roleName);
		} catch (Exception e) {
			_log.debug("Site Role having name " + roleName + " does not exist, proceed with creation OK");
		}
		if (existingroleId == -1) {
			try {
				_log.debug("Trying createRole " + roleName);
				Date now = new Date();
				Long defaultCompanyId = PortalUtil.getDefaultCompanyId();
				Long defaultUserId = UserLocalServiceUtil.getDefaultUserId(defaultCompanyId);
				Long roleClassNameId = ClassNameLocalServiceUtil.getClassNameId(Role.class);
				Long roleId = CounterLocalServiceUtil.increment();

				Role role = RoleLocalServiceUtil.createRole(roleId);
				role.setName(roleName);
				role.setDescription(roleDescription);
				role.setType(ROLE_TYPE);
				role.setUserId(defaultUserId);
				role.setCompanyId(defaultCompanyId);
				role.setClassNameId(roleClassNameId);
				role.setClassPK(roleId);
				role.setCreateDate(now);
				role.setModifiedDate(now);

				RoleLocalServiceUtil.addRole(role);
				_log.debug("CreateRole " + roleName + " SUCCESS");
				return true;
			} catch (SystemException | PortalException e) {
				e.printStackTrace();
			}
		} else {
			_log.error("Site Role having name " + roleName + " exist already, skipping creation");
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteRole(long roleId) throws UserManagementSystemException, RoleRetrievalFault {
		try {
			RoleLocalServiceUtil.deleteRole(roleId);
			return true;
		} catch (PortalException e) {
			throw new RoleRetrievalFault("The roleId does not exists", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeRole updateRole(long roleId, String roleName,	String roleDescription) throws RoleRetrievalFault {
		Role toEdit;
		try {
			toEdit = RoleLocalServiceUtil.getRole(roleId);
			toEdit.setName(roleName);
			toEdit.setDescription(roleDescription);
			Role toReturn = RoleLocalServiceUtil.updateRole(toEdit);
			return mapLRRole(toReturn);
		} catch (PortalException e) {
			throw new RoleRetrievalFault("The roleId does not exists", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeRoleFromUser(long userId, long groupId, long roleId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, RoleRetrievalFault {
		try {			
			_log.debug("Trying to remove role to " + UserLocalServiceUtil.getUser(userId).getFullName() +" in group " + groupId);
			long[] roleIds = {roleId};
			UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(userId, groupId, roleIds);
			return true;
		} catch (PortalException e) {
			throw new UserRetrievalFault("User, not existing, or group/roleId could not be found", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}		
		return false;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAllRolesFromUser(long userId, long... groupIds) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault {
		try {			
			_log.debug("Trying to remove all roles to " + UserLocalServiceUtil.getUser(userId).getFullName() +" in groups " + groupIds);
			UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(userId, groupIds);
			return true;
		} catch (PortalException e) {
			throw new UserRetrievalFault("User, not existing, or group/roleId could not be found", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}		
		return false;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeRole> listAllRoles() {
		List<GCubeRole> toReturn = new ArrayList<GCubeRole>();
		try {			
			List<Role> roles = RoleLocalServiceUtil.getRoles(ManagementUtils.getCompany().getCompanyId());
			for (Role role : roles) {
				toReturn.add(mapLRRole(role));
			}
		} catch (SystemException | PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeRole> listAllGroupRoles() {
		List<GCubeRole> toReturn = new ArrayList<GCubeRole>();
		List<Role> roles;
		try {
			roles = RoleLocalServiceUtil.getRoles(ManagementUtils.getCompany().getCompanyId());
			for (Role role : roles) {
				if (role.getType()==ROLE_TYPE)
					toReturn.add(mapLRRole(role));
			}
		} catch (SystemException | PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeRole> listRolesByUserAndGroup(long userId, long groupId) throws GroupRetrievalFault, UserRetrievalFault {
		List<GCubeRole> toReturn = new ArrayList<GCubeRole>();
		try {
			List<UserGroupRole> roles = UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroup(groupId);
			for (UserGroupRole ugr : roles) {
				if (ugr.getUserId()==userId)
					toReturn.add(mapLRRole(ugr.getRole()));
			}
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			throw new UserRetrievalFault("User, not existing, or groupId could not be found", e);
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam createTeam(long userId, long groupId, String teamName, String teamDescription) throws GroupRetrievalFault, TeamRetrievalFault, UserManagementSystemException {
		try {
			return mapLRTeam(TeamLocalServiceUtil.addTeam(userId, groupId, teamName, teamDescription));
		}
		catch (DuplicateTeamException ex) {
			throw new TeamRetrievalFault("A Team with this name exists already: name="+teamName);
		}
		catch (PortalException e) {
			throw new GroupRetrievalFault("The groupId could not be found", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam createTeam(long groupId, String teamName, String teamDescription) throws GroupRetrievalFault, TeamRetrievalFault, UserManagementSystemException {
		long userId = LiferayUserManager.getAdmin().getUserId();
		return createTeam(userId, groupId, teamName, teamDescription);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam deleteTeam(long teamId) throws UserManagementSystemException, TeamRetrievalFault {
		try {
			Team deleted = TeamLocalServiceUtil.deleteTeam(teamId);
			return mapLRTeam(deleted);
		} catch (PortalException e) {
			throw new TeamRetrievalFault("The teamId does not exists", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteTeams(long groupId) throws UserManagementSystemException {
		try {
			TeamLocalServiceUtil.deleteTeams(groupId);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam getTeam(long teamId) throws UserManagementSystemException,	TeamRetrievalFault {
		try {
			return mapLRTeam(TeamLocalServiceUtil.getTeam(teamId));
		} catch (PortalException e) {
			_log.warn(teamId + " Team id not existing");
		} catch (SystemException e) {
			_log.error("Liferay SystemException");
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeTeam> listTeamsByGroup(long groupId) throws GroupRetrievalFault {
		List<GCubeTeam> toReturn = new ArrayList<>();
		List<Team> teams;
		try {
			teams =	TeamLocalServiceUtil.getGroupTeams(groupId);
			for (Team team : teams) {
				toReturn.add(mapLRTeam(team));
			}			
		} catch (SystemException | PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeTeam> listTeamsByUserAndGroup(long userId, long groupId) throws UserRetrievalFault, GroupRetrievalFault {
		List<GCubeTeam> toReturn = new ArrayList<>();
		List<Team> teams;
		try {
			teams =	TeamLocalServiceUtil.getUserTeams(userId, groupId);
			for (Team team : teams) {
				toReturn.add(mapLRTeam(team));
			}	
		} catch (SystemException | PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam updateTeam(long teamId, String teamName, String teamDescription) throws TeamRetrievalFault {
		Team toEdit;
		try {
			toEdit = TeamLocalServiceUtil.updateTeam(teamId, teamName, teamDescription);
			return mapLRTeam(toEdit);
		} catch (PortalException e) {
			throw new TeamRetrievalFault("The teamId does not exists", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setUserTeams(long userId, long[] teamIds) throws TeamRetrievalFault {
		try {
			TeamLocalServiceUtil.setUserTeams(userId, teamIds);
		} catch (SystemException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteUserTeams(long userId, long[] teamIds) {
		try {
			TeamLocalServiceUtil.deleteUserTeams(userId, teamIds);
		} catch (SystemException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteUserTeams(long userId, List<GCubeTeam> teams) {
		long[] teamIds = new long[teams.size()];
		int i = 0;
		for (GCubeTeam r : teams) {
			teamIds[i] = r.getTeamId();
			i++;
		}
		try {
			TeamLocalServiceUtil.deleteUserTeams(userId, teamIds);
		} catch (SystemException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeTeam getTeam(long groupId, String teamName)	throws GroupRetrievalFault, TeamRetrievalFault {
		GCubeTeam toReturn = null;
		try {
			toReturn = mapLRTeam(TeamLocalServiceUtil.getTeam(groupId, teamName));
		} catch (PortalException e) {
			throw new TeamRetrievalFault("The teamname or groupdid does not exists", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public boolean assignTeamToUser(long userId, long teamId)	throws UserManagementSystemException, UserRetrievalFault, TeamRetrievalFault {
		try {
			List<Team> currentTeams = TeamLocalServiceUtil.getUserTeams(userId);
			
			// add is not supported in the returned list, so we need a workaround
			List<Team> currentTeamsArrayList = new ArrayList<Team>(currentTeams);
			Team toAdd = TeamLocalServiceUtil.getTeam(teamId);
			currentTeamsArrayList.add(toAdd);
			
			long[] teamIdstoSet = new long[currentTeamsArrayList.size()];
			int i = 0;
			for (Team t : currentTeamsArrayList) {
				teamIdstoSet[i] = t.getTeamId();
				i++;
			}
			return setUserTeams(userId, teamIdstoSet);
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			throw new TeamRetrievalFault("The teamId or groupdid does not exists", e);
		}
		return false;
	}

	

}
