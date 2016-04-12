package org.gcube.vomanagement.usermanagement.impl.liferay;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementFileNotFoundException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementIOException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * Liferay plugin for the RoleManager interface, this implementation interacts with Liferay through a LocalService.
 * 
 * @author Giulio Galiero
 *
 */
public class LiferayRoleManager implements RoleManager {
	
	private static final String ADMIN_ROLE = "Administrator";
	protected static final String PRODUCTION_SUPPORT = "Production-Support";
	public static final String VRE = "vre";
	public static final String VO = "vo";
	public static final String ROOT_VO = "root-vo";

	/**
	 * @return true if the user is a portal administrator, false otherwise
	 */
	public boolean isAdmin(String userId) {
		LiferayGroupManager gm = new LiferayGroupManager();
		try {
			com.liferay.portal.model.User currUser = UserLocalServiceUtil.getUserByScreenName(gm.getCompanyId(), userId);
			for (Role role : currUser.getRoles()) 
				if (role.getName().compareTo(ADMIN_ROLE) == 0 ) 
					return true;	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; 
	}
	
	long convertType(String stringId){
		return Long.parseLong(stringId);
	}

	protected String getRoleName(String roleNameFull){
		List<Organization> orgs;
		String roleName = null;
		try {
			orgs = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			for(Organization org : orgs){
				if(roleNameFull.contains(org.getName())){
					roleName = roleNameFull.substring(0, roleNameFull.indexOf(org.getName())-1);
					break;
				}
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roleName;
	}

	public HashMap <String, String> listAllowedRoles (String groupName) throws UserManagementSystemException, GroupRetrievalFault, UserManagementFileNotFoundException, UserManagementIOException
	{
		GroupManager gm = new LiferayGroupManager();
		String groupId = gm.getGroupId(groupName);
		HashMap<String,String> hMap = new HashMap<String,String>();
		// Code changed to remove hardcoded role names to external config file as requested by Leonordo and Manzi
		/*	List<String> vreRoles = Arrays.asList("VRE-Manager", "VRE-User");
		List<String> voRoles = Arrays.asList("VO-Admin", "VRE-Designer", "VRE-Manager", "Data-Manager");
		List<String> rootVoRoles = Arrays.asList("Infrastructure-Manager", "Site-Manager");*/

		if(gm.isRootVO(groupId)){
			hMap = ParseXML.getRoles(ROOT_VO);
		}else if (gm.isVO(groupId)){
			hMap = ParseXML.getRoles(VO);
		}else if (gm.isVRE(groupId)){
			hMap = ParseXML.getRoles(VRE);
		}
		return hMap;
	}





	public String getRoleId(String roleName, String groupName) throws UserManagementSystemException{
		List<Role> roles = null;
		String rolenameLR = null;
		long roleId = 0;
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving roles ",  e);
		}
		if(!roleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
			roleName = roleName + "-" + groupName;
		}

		for(Role role: roles){
			rolenameLR = role.getName();			
			if(rolenameLR.equalsIgnoreCase(roleName)){
				roleId = role.getRoleId();
				break;
			}
		}
		return String.valueOf(roleId);		
	}

	public void assignRoleToUser(String groupIdn, String roleIdn,
			String userIdn) throws UserManagementSystemException, GroupRetrievalFault, RoleRetrievalFault, UserRetrievalFault{
		long[] roleIds;
		long roleId = this.convertType(roleIdn);
		long userId = this.convertType(userIdn);
		long groupId = this.convertType(groupIdn);
		int i = 0;
		String orgName = null;
		Role role = null;
		Organization org;
		List<Role> roles;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupId);
			roles = RoleLocalServiceUtil.getUserRoles(userId);
		}catch (PortalException e){
			throw new GroupRetrievalFault("No group exists with groupId ",groupIdn, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error assigning role to user ",  e);
		}

		roleIds = new long[roles.size()+1];
		for(Role userRole : roles){
			roleIds[i++] = userRole.getRoleId();
		}
		roleIds[i]= this.convertType(roleIdn);
		orgName = org.getName();
		try {
			role = RoleLocalServiceUtil.getRole(roleId);
		} catch (PortalException e) {
			throw new RoleRetrievalFault("No Role exists with roleId",roleIdn,e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error assigning role to user ",  e);
		}
		String roleName = role.getName();
		if(!roleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
			if(roleName.contains(orgName)){
				try {
					if(UserLocalServiceUtil.hasOrganizationUser(groupId, userId)){
						RoleLocalServiceUtil.setUserRoles(userId, roleIds);
					}
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error assigning role to user ",  e);
				} catch (PortalException e) {
					throw new UserRetrievalFault("No User exists with userId ",userIdn,e);
				}
			}
		}else{
			try {
				RoleLocalServiceUtil.setUserRoles(userId, roleIds);
			} catch (PortalException e) {
				throw new UserRetrievalFault("Error assigning role to user with userId ",userIdn,e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning role to user ",  e);
			}
		}

	} 

	private RoleModel createRoleUtil(String roleName,String roleDesc) throws UserManagementSystemException, RoleRetrievalFault, UserManagementPortalException {
		Role role = null;
		RoleModel rm = null;
		LiferayGroupManager gm = new LiferayGroupManager();
		//List<String> allowedRoles = getAllowedRoles(groupName);
		boolean roleExists = false;

		long companyId = gm.getCompanyId();

		//roleName = roleName + "-" + groupName;
		List<Role> roles;
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error creating role ",  e);
		}
		for(Role existingRole : roles){
			if(existingRole.getName().equals(roleName)){
				roleExists = true;
				break;
			}
		}
		if(!roleExists){
			try {
				Locale english = new Locale("en");
				HashMap<Locale, String> roleNames = new HashMap<Locale, String>();
				roleNames.put(english, roleName);
				role = RoleLocalServiceUtil.addRole(0L, companyId, roleName, roleNames, roleDesc, 1);
			} catch (PortalException e) {
				throw new RoleRetrievalFault("Cannot create new role, check the roleName already exists",roleName,e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error creating role ",  e);
			}
			if(!roleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
				roleName = roleName.substring(0, roleName.lastIndexOf("-"));
			}

			rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
		}else{
			throw new RoleRetrievalFault("Cannot create new role, check the roleName already exists",roleName);
		}
		return rm;
	}

	public boolean createRole(String roleName,String roleDescription, String groupName) throws UserManagementSystemException, GroupRetrievalFault, RoleRetrievalFault, UserManagementPortalException {
		List<RoleModel> roleModels = new ArrayList<RoleModel>();
		//String roleDesc = validateRoleName(roleName, groupId);
		if(!roleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
			roleName = roleName +"-"+groupName;
		}

		roleModels.add(this.createRoleUtil(roleName, roleDescription));
		return true;
	}

	public void createRole(String groupName) throws UserManagementSystemException, GroupRetrievalFault, RoleRetrievalFault, UserManagementPortalException, UserManagementFileNotFoundException, UserManagementIOException{
		GroupManager gm = new LiferayGroupManager();
		Organization org;
		HashMap<String,String> allowedRolesConfig;
		try {
			String groupId = gm.getGroupId(groupName);
			allowedRolesConfig = listAllowedRoles(groupName);			
			org = OrganizationLocalServiceUtil.getOrganization(this.convertType(groupId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error creating roles for group ", groupName , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Name ", groupName, e);
		}
		String orgName = org.getName();
		Set<String> allowedRoles =  allowedRolesConfig.keySet();
		Collection<String> allowedRolesDesc = allowedRolesConfig.values();
		Iterator<String> rolesDescIter = allowedRolesDesc.iterator();

		for(String allowedRole : allowedRoles){
			if(!allowedRole.equalsIgnoreCase(PRODUCTION_SUPPORT)){
				allowedRole = allowedRole+ "-"+ orgName;
			}
			this.createRoleUtil(allowedRole, rolesDescIter.next());
		}
	}



	public void deleteRole(String roleName, String groupName)throws UserManagementSystemException, RoleRetrievalFault{
		try {
			String roleId = this.getRoleId(roleName, groupName);
			RoleLocalServiceUtil.deleteRole(this.convertType(roleId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error deleting role  ", roleName , e);
		} catch (PortalException e) {
			throw new RoleRetrievalFault("No role exists with role  name  ", roleName, e);
		}
	}

	public void dismissRoleFromUser(String groupIdn, String roleIdn,
			String userIdn) throws UserManagementSystemException, RoleRetrievalFault, GroupRetrievalFault, UserRetrievalFault{
		//boolean isMember = false;
		long roleId = this.convertType(roleIdn);
		long[] roleIds = {roleId}; 
		long userId = this.convertType(userIdn);
		long groupId = this.convertType(groupIdn);
		String orgName;
		Role role;
		try {
			role = RoleLocalServiceUtil.getRole(roleId);
		}catch (PortalException e) {
			throw new RoleRetrievalFault("No role exists with roleId ", roleIdn, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error dismissing roles from userc", userIdn, e);
		}
		Organization org;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupId);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Name ", groupIdn, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error dismissing roles from userc", userIdn, e);
		}
		orgName = org.getName();
		if(!role.getName().equalsIgnoreCase(PRODUCTION_SUPPORT)){
			if(role.getName().contains(orgName)){
				try {
					if(UserLocalServiceUtil.hasRoleUser(roleId, userId)){
						RoleLocalServiceUtil.unsetUserRoles(userId, roleIds);
					}
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error dismissing roles from user ", userIdn, e);
				} catch (PortalException e) {
					throw new UserRetrievalFault("No User exists with userId ",userIdn,e);
				}
			}
		}else{
			try {
				if(UserLocalServiceUtil.hasRoleUser(roleId, userId)){
					RoleLocalServiceUtil.unsetUserRoles(userId, roleIds);
				}
			}catch (SystemException e) {
				throw new UserManagementSystemException("Error dismissing roles from user ", userIdn, e);
			} catch (PortalException e) {
				throw new UserRetrievalFault("No User exists with userId ",userIdn,e);
			}
		}

		//remove user from VO if no roles relevant to the VO are present
		// removed as requested by massi as this removes the Organization-member role for the user
		/*	List<Role> userRoles = RoleLocalServiceUtil.getUserRoles(userId);
			for(Role userRole : userRoles){
				if(userRole.getName().contains(orgName)){
					isMember = true;
					break;
				}
			}*/
		// if the user does nt have other roles in the group, remove the user from the group
		/*	if(!isMember){
				long[] userIds = {userId};
				UserLocalServiceUtil.unsetOrganizationUsers(groupId, userIds);
			}*/

	}

	public RoleModel getRole(String roleId) throws UserManagementSystemException, RoleRetrievalFault{
		Role role = null;
		RoleModel rm = null;
		String roleName;
		String completeRoleName;
		try {
			role = RoleLocalServiceUtil.getRole(this.convertType(roleId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving role ", roleId , e);
		} catch (PortalException e) {
			throw new RoleRetrievalFault("No role exists with roleId ", roleId, e);
		}
		completeRoleName = role.getName();
		roleName = getRoleName(completeRoleName);
		rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
		rm.setCompleteName(completeRoleName);
		
		return rm;
	}

	@Deprecated
	public List<String> listRoles() throws UserManagementSystemException {
		List<Role> roles = null;
		String roleName;
		List<String> roleNames = new ArrayList<String>();
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
			for(Role role:roles){
				roleName = role.getName();
				if(roleName.contains("-")){
					roleName = getRoleName(roleName);
					if(!roleNames.contains(roleName)){
						roleNames.add(roleName);
					}
				}
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles ", e);
		}
		return roleNames;
	}
	
	public List<RoleModel> listAllRoles() throws UserManagementSystemException {
		List<Role> roles = null;
		String roleName;
		String completeRoleName;
		List<RoleModel> roleModels = new ArrayList<RoleModel>();
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
			for(Role role:roles){
				completeRoleName = role.getName();
				if(!completeRoleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
					roleName = getRoleName(completeRoleName);
				}
				else roleName = completeRoleName;
				
				RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
				rm.setCompleteName(completeRoleName);
				roleModels.add(rm);
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles ", e);
		}
		return roleModels;
	}

	public List<RoleModel> listRolesByUser(String userId) throws UserManagementSystemException {
		List<Role> roles = null;
		List<RoleModel> roleModels = new ArrayList<RoleModel>();
		String roleName;
		String completeRoleName;
		try {
			roles = RoleLocalServiceUtil.getUserRoles(this.convertType(userId));
			for(Role role:roles){
				completeRoleName = role.getName();
				if(!completeRoleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
					roleName = getRoleName(completeRoleName);
				}
				else roleName = completeRoleName;
				
				RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
				rm.setCompleteName(completeRoleName);
				roleModels.add(rm);

			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles for user ",userId, e);
		}
		return roleModels;
	}

	public List<RoleModel> listRolesByUserAndGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalFault{
		List<Role> userRoles = new ArrayList<Role>();
		List<RoleModel> userRoleModels = new ArrayList<RoleModel>();
		List<Role> roles = null;
		String roleName;
		String completeRoleName;
		String orgName;
		Organization org = null;
		try {
			roles = RoleLocalServiceUtil.getUserRoles(this.convertType(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles by user and group ",  e);
		}

		try {
			org = OrganizationLocalServiceUtil.getOrganization(this.convertType(groupId));
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Id ", groupId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles by user and group ",  e);
		}
		orgName = org.getName();
		for(Role role : roles){
			if(role.getName().contains(orgName)){
				userRoles.add(role);
				completeRoleName = role.getName();
				roleName = getRoleName(completeRoleName);
				RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
				rm.setCompleteName(completeRoleName);
				userRoleModels.add(rm);

			}
		}

		return userRoleModels;
	}

	public List<RoleModel> listRolesByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserManagementFileNotFoundException, UserManagementIOException{
	
		List<Role> groupRoles = new ArrayList<Role>();
		List<RoleModel> groupRoleModels = new ArrayList<RoleModel>();
		GroupManager gm = new LiferayGroupManager();
		List<Role> roles = null;
		String orgName;
		String roleName;
		String completeRoleName;
		String groupType = null;
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());

		} catch (SystemException e) {

			throw new UserManagementSystemException("Error listing roles for group ", groupId , e);
		}
		Organization org;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(this.convertType(groupId));

		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Id ", groupId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles for group ", groupId , e);
		}
		orgName = org.getName();
		if(gm.isRootVO(groupId)){
			groupType = ROOT_VO;
		}else if(gm.isVO(groupId)){
			groupType = VO;
		}else if(gm.isVRE(groupId)){
			groupType = VRE;
		}
		for(Role role : roles){
			if(role.getName().endsWith(orgName)){
				groupRoles.add(role);
				completeRoleName = role.getName();
				roleName = getRoleName(completeRoleName);
				if(ParseXML.getRoles(groupType).containsKey(roleName)){
					String roleDesc;
					if(role.getDescription()==""){
						roleDesc = ParseXML.getRoles(groupType).get(roleName);
						role.setDescription(roleDesc);
						try {
							RoleLocalServiceUtil.updateRole(role);
						}catch (SystemException e) {
							e.printStackTrace();
							throw new UserManagementSystemException("Error updating role ",role.getName(),  e);
						}
					}else{
						roleDesc = role.getDescription();
					}
					RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),roleDesc);
					rm.setCompleteName(completeRoleName);
					groupRoleModels.add(rm);
				}else{
					
					RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
					groupRoleModels.add(rm);
				}
				
				
			}
		}
		Role role = this.getCommonRole();
		if(role!=null){
			String roleDesc;
			try {
				if(role.getDescription()==""){
					roleDesc = ParseXML.getRoles(VO).get(role.getName());
					role.setDescription(roleDesc);
					RoleLocalServiceUtil.updateRole(role);
				}else{
					roleDesc = role.getDescription();
				}
				
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error updating role Production-Support",  e);
			}
			groupRoleModels.add(new RoleModel(role.getName(),String.valueOf(role.getRoleId()),roleDesc));
		}
		
		return groupRoleModels;
	}
	
	protected Role getCommonRole() throws UserManagementSystemException{
		List<Role> roles;
		try {
			roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error listing roles  ",  e);
		}
		
		for(Role role : roles){
			if(role.getName().equalsIgnoreCase(PRODUCTION_SUPPORT)){
				return role;
			}
		}
		return null;
	}

	private List<GroupModel> getGroupsByType(String groupType) throws UserManagementSystemException, GroupRetrievalFault{
		LiferayGroupManager gm = new LiferayGroupManager();
		List<GroupModel> groups = gm.listGroups();
		List<GroupModel> groupsByType = new ArrayList<GroupModel>();

		if(groupType.equals(VO)){
			for(GroupModel group:groups){
				if(gm.isVO(group.getGroupId())){
					groupsByType.add(group);
				}
			}
		}else if(groupType.equals(VRE)){
			for(GroupModel group:groups){
				if(gm.isVRE(group.getGroupId())){
					groupsByType.add(group);
				}
			}
		}else if(groupType.equals(ROOT_VO)){
			for(GroupModel group:groups){
				if(gm.isRootVO(group.getGroupId())){
					groupsByType.add(group);
				}
			}

		}
		return groupsByType;
	}

	

	public void updateRole(String initialRoleName, String newRoleName, String roleDescription, String groupName) throws UserManagementSystemException, RoleRetrievalFault, NumberFormatException,  UserManagementFileNotFoundException, UserManagementIOException, GroupRetrievalFault, UserManagementPortalException {

		Role role = null;
		boolean isSystemRole = false;
		String roleId;
		String[] groupTypes = {VO,VRE,ROOT_VO};
		if(!initialRoleName.equalsIgnoreCase(PRODUCTION_SUPPORT)){
			 roleId = this.getRoleId(initialRoleName, groupName);
		}else{
			 roleId =  String.valueOf(this.getCommonRole().getRoleId());
			 for(String groupType : groupTypes){
				 HashMap<String,String> hMap = ParseXML.getRoles(groupType);
				 hMap.put(initialRoleName, roleDescription);
 				ParseXML.updateRoles(hMap, groupType);
			 }
			 Role prod_supp_role;
			try {
				prod_supp_role = RoleLocalServiceUtil.getRole(Long.parseLong(roleId));
				prod_supp_role.setDescription(roleDescription);
				RoleLocalServiceUtil.updateRole(prod_supp_role);
			} catch (PortalException e1) {
				throw new UserManagementPortalException("Error retrieving role  ", initialRoleName , e1);
			} catch (SystemException e1) {
				throw new UserManagementSystemException("Error updating role ", initialRoleName , e1);
			}

			 
			 return;
			 
		}
		
		try {
			role = RoleLocalServiceUtil.getRole(Long.parseLong(roleId));
		} catch (PortalException e2) {
			throw new UserManagementPortalException("Error retrieving role  ", initialRoleName , e2);
		} catch (SystemException e2) {
			throw new UserManagementSystemException("Error updating role ", initialRoleName , e2);
		}
		

		if(ParseXML.getRoles(VO).containsKey(initialRoleName)){
			isSystemRole = true;
			// update the predefined config file
			HashMap<String,String> hMap = ParseXML.getRoles(VO);
			if(hMap.containsKey(initialRoleName)){
				hMap.put(initialRoleName, roleDescription);

				ParseXML.updateRoles(hMap, VO);
				// update the actual roles
				List<GroupModel> voGroups = this.getGroupsByType(VO);

				for(GroupModel group:voGroups){
					List<RoleModel> rm = this.listRolesByGroup(group.getGroupId());
					for(RoleModel roleModel : rm){
					if(roleModel.getRoleName().equals(initialRoleName)){
						String voroleId = this.getRoleId(initialRoleName, group.getGroupName());
						Role affectedRole;
						try {
							try {
								affectedRole = RoleLocalServiceUtil.getRole(Long.parseLong(voroleId));
							} catch (SystemException e) {
								throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
							}
						} catch (PortalException e) {
							throw new RoleRetrievalFault("Error retrieving role with name ", initialRoleName, e);
						}
						affectedRole.setDescription(roleDescription);
						try {
							RoleLocalServiceUtil.updateRole(affectedRole);
						} catch (SystemException e) {
							throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
						//break;
					}
				}
				}
				}
			}
		} if(ParseXML.getRoles(VRE).containsKey(initialRoleName)){
			isSystemRole = true;
			HashMap<String,String> hMap = ParseXML.getRoles(VRE);
			if(hMap.containsKey(initialRoleName)){
				hMap.put(initialRoleName, roleDescription);
				ParseXML.updateRoles(hMap, VRE);

				List<GroupModel> vreGroups = this.getGroupsByType(VRE);

				for(GroupModel group:vreGroups){
					List<RoleModel> rm = this.listRolesByGroup(group.getGroupId());
					for(RoleModel roleModel : rm){
						if(roleModel.getRoleName().equals(initialRoleName)){
					if(this.listRolesByGroup(group.getGroupId()).contains(initialRoleName)){
						String vreroleId = this.getRoleId(initialRoleName, group.getGroupName());
						Role affectedRole;
						try {
							try {
								affectedRole = RoleLocalServiceUtil.getRole(Long.parseLong(vreroleId));
							} catch (SystemException e) {
								throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
							}
						} catch (PortalException e) {
							throw new RoleRetrievalFault("Error retrieving role with name ", initialRoleName, e);
						}
						affectedRole.setDescription(roleDescription);
						try {
							RoleLocalServiceUtil.updateRole(affectedRole);
						} catch (SystemException e) {
							throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
						}
					}
				}
				}
				}
			}
		} if(ParseXML.getRoles(ROOT_VO).containsKey(initialRoleName)){
			isSystemRole = true;
			HashMap<String,String> hMap = ParseXML.getRoles(ROOT_VO);
			if(hMap.containsKey(initialRoleName)){
				ParseXML.updateRoles(hMap, ROOT_VO);

				List<GroupModel> rootvoGroups = this.getGroupsByType(ROOT_VO);
				for(GroupModel group:rootvoGroups){
					List<RoleModel> rm = this.listRolesByGroup(group.getGroupId());
					for(RoleModel roleModel : rm){
						if(roleModel.getRoleName().equals(initialRoleName)){
						String rootVoroleId = this.getRoleId(initialRoleName, group.getGroupName());
						Role affectedRole;
						try {
							try {
								affectedRole = RoleLocalServiceUtil.getRole(Long.parseLong(rootVoroleId));
							} catch (SystemException e) {
								throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
							}
						} catch (PortalException e) {
							throw new RoleRetrievalFault("Error retrieving role with name ", initialRoleName, e);
						}
						affectedRole.setDescription(roleDescription);
						try {
							RoleLocalServiceUtil.updateRole(affectedRole);
						} catch (SystemException e) {
							throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
						}
					}
					}
				}
			}
			}if(!isSystemRole){
				role.setDescription(roleDescription);
				newRoleName = newRoleName + "-" + groupName;
				role.setName(newRoleName);
				try {
					RoleLocalServiceUtil.updateRole(role);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error updating role ", initialRoleName , e);
				}
			}

		/*	role.setName(roleModel.getRoleName());
			role.setDescription(roleModel.getDescription());*/


	}



	public void updatePredefinedRoles(HashMap<String, String> rolesMap, String groupType)
	throws UserManagementIOException, UserManagementFileNotFoundException {
		if(groupType.equals(VRE)){
			ParseXML.updateRoles(rolesMap, VRE);
		}else if (groupType.equals(VO)){
			ParseXML.updateRoles(rolesMap, VO);
		}else if(groupType.equals(ROOT_VO)){
			ParseXML.updateRoles(rolesMap, ROOT_VO);
		}

	}

}
