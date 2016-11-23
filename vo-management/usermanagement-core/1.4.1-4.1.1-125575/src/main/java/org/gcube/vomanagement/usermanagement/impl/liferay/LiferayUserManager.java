package org.gcube.vomanagement.usermanagement.impl.liferay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.utils.ExpandoClassCodeManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.utils.ExpandoDefaultTableManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.MembershipRequest;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

/**
 * Liferay plugin for the UserManager interface, this implementation interacts with Liferay through a LocalService.
 * 
 * @author Giulio Galiero
 *
 */


public class LiferayUserManager implements UserManager {

	private final String DEFAULT_STRING = "_";
	
	private long convertStringToLong(String id){
		return Long.parseLong(id);
	}

	
	public List<UserModel> getAllUsers () throws UserManagementSystemException, UserRetrievalFault
	{
		List<User> users = null;
		List<UserModel> response = new ArrayList<UserModel>();

		try {
			users = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error getting the user list",e);
		}
		for(User user: users)
		{

			response.add(new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(), user.getCreateDate().getTime() ,this.getUserCustomAttributes(String.valueOf(user.getUserId()))));

		}

		return response;
	}
	
	public String getUserId(String userName) throws UserManagementSystemException{
		List<User> users = null;
		long userId = 0;
		try {
			users = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error getting the User Id of user ",userName, e);
		}
		for(User user: users){
			if(user.getScreenName().equalsIgnoreCase(userName)){
				userId = user.getUserId();
				break;
			}
		}
		return String.valueOf(userId);	
	}

	public void assignUserToGroup(String groupIdn, String userIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault, UserManagementPortalException {
		long userId = this.convertStringToLong(userIdn);
		List<MembershipRequest> memberRequests = null;
		long groupId = this.convertStringToLong(groupIdn);
		long[] userIds = {userId};
		GroupManager groupManager = new LiferayGroupManager();
		Organization org = null;

		// Add the user to Parent Organization if they are not already added
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupId);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with groupId ",groupIdn, e);
		}
		User user = null;
		List<Organization> orgs = null;
		try {
			user = UserLocalServiceUtil.getUser(userId);
			orgs = user.getOrganizations();
		} catch (PortalException e) {
			throw new UserRetrievalFault("No user exists with userId ",userIdn, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
		}

		if(groupManager.isRootVO(groupIdn)){
			//RootVO 
			try {
				UserLocalServiceUtil.addOrganizationUsers(groupId, userIds);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			} catch (PortalException e) {
				throw new UserManagementPortalException("Check if user already exists in group ", groupIdn , e);
			}

		}else if (groupManager.isVO(groupIdn)){
			//VO
			try {
				UserLocalServiceUtil.addOrganizationUsers(groupId, userIds);
			} catch (PortalException e) {
				throw new UserManagementPortalException("Check if user already exists in group  ", groupIdn , e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			long parentOrgId = org.getParentOrganizationId();
			Organization parentOrg = null;
			try {
				parentOrg = OrganizationLocalServiceUtil.getOrganization(parentOrgId);
			} catch (PortalException e) {
				throw new GroupRetrievalFault("No group exists with groupId ",String.valueOf(parentOrgId), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			if(!orgs.contains(parentOrg)){
				try {
					UserLocalServiceUtil.addOrganizationUsers(parentOrgId, userIds);
				} catch (PortalException e) {
					throw new UserManagementPortalException("Check if user already exists in group  ", String.valueOf(parentOrgId) , e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
				}
			}

		}else if (groupManager.isVRE(groupIdn)){
			//VRE
			try {
				UserLocalServiceUtil.addOrganizationUsers(groupId, userIds);
			} catch (PortalException e) {
				throw new UserManagementPortalException("Check if user already exists in group  ", String.valueOf(groupId) , e);					
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			long parentOrgId = org.getParentOrganizationId();
			Organization parentOrg = null;
			try {
				parentOrg = OrganizationLocalServiceUtil.getOrganization(parentOrgId);
			} catch (PortalException e) {
				throw new GroupRetrievalFault("No group exists with groupId ",String.valueOf(parentOrgId), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			if(!orgs.contains(parentOrg)){
				try {
					UserLocalServiceUtil.addOrganizationUsers(parentOrgId, userIds);
				} catch (PortalException e) {
					throw new GroupRetrievalFault("Check if user already exists in group  ",String.valueOf(parentOrgId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
				}
			}

			try {
				parentOrgId = org.getParentOrganization().getParentOrganizationId();
			} catch (PortalException e) {
				throw new GroupRetrievalFault("No group exists with groupId ",String.valueOf(parentOrgId), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			try {
				parentOrg = OrganizationLocalServiceUtil.getOrganization(parentOrgId);
			} catch (PortalException e) {
				throw new GroupRetrievalFault("No group exists with groupId ",String.valueOf(parentOrgId), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
			}
			if(!orgs.contains(parentOrg)){
				try {
					UserLocalServiceUtil.addOrganizationUsers(parentOrgId, userIds);
				} catch (PortalException e) {
					throw new GroupRetrievalFault("Check if user already exists in group  ",String.valueOf(parentOrgId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error assigning user to Group ", groupIdn , e);
				}
			}


		}
		//delete the corresponding member request from the MembershipRequest table
		try {
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error getting membership request ",  e);
		}
		for(MembershipRequest memberRequest: memberRequests){
			if(memberRequest.getUserId()==userId && memberRequest.getGroupId()==org.getGroup().getGroupId()){
				try {
					MembershipRequestLocalServiceUtil.deleteMembershipRequest(memberRequest);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error deleting membership request ", String.valueOf(memberRequest.getMembershipRequestId()) , e);
				}
			}
		}


	}

	public void requestMembership(String userIdn, String groupIdn,String comment) throws UserManagementSystemException, GroupRetrievalFault{
		long userId = this.convertStringToLong(userIdn);
		long organizationId = this.convertStringToLong(groupIdn);
		Organization org = null;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(organizationId);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error adding membership request to group ", groupIdn , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with groupId ",groupIdn, e);
		}
		try {
			if (!checkString(comment)) comment = DEFAULT_STRING;
			MembershipRequest mr = MembershipRequestLocalServiceUtil.addMembershipRequest(userId, org.getGroup().getGroupId(), comment);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("error adding membership request for user id ",userIdn, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error adding membership request to group ", groupIdn , e);
		}

	}

	/**
	 * 
	 * If the string is null, empty or a digit returns false
	 * 
	 * @param string
	 * @return false if the string is empty, null or a digit, true otherwise
	 */
	private boolean checkString (String string)
	{
		boolean response = true;
		
		try 
		{
			Double.parseDouble(string);
			response = false;
			
		} catch (NullPointerException e)
		{
			response = false;
		} catch (NumberFormatException e)
		{
			if (string.trim().length() ==0) return false;
		}
		
		return response;
	}

	
	public List<UserModel> getMembershipRequests(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{
		List<MembershipRequest> memberRequests = null;
		List<UserModel> users = new ArrayList<UserModel>();
		Organization org = null;
		UserModel um = null;
		try {
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving Membership requests of group ",groupId, e);
		} 
		try {
			org = OrganizationLocalServiceUtil.getOrganization(convertStringToLong(groupId));
		} catch (PortalException e) {
			throw new GroupRetrievalFault("error retrieving group for group Id ",groupId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving Membership requests of group ",groupId, e);
		}
		for(MembershipRequest memberRequest: memberRequests){
			if(memberRequest.getGroupId() == org.getGroup().getGroupId()){
				long userId = memberRequest.getUserId();
				User user = null;
				try {
					user = UserLocalServiceUtil.getUser(userId);
				} catch (PortalException e) {
					throw new UserRetrievalFault("Error retrieving user for user Id ",String.valueOf(userId),e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error retrieving Membership requests of group ",groupId, e);
				}
				um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				users.add(um);
				break;
			}
		}

		return users;

	}


	public void createUser(UserModel userModel) throws UserManagementSystemException, UserRetrievalFault {
		String password1 = null;
		Locale locale = new Locale("en_US");
		ServiceContext serviceContext = new ServiceContext();
		String password2 = null;
		try {
			UserLocalServiceUtil.addUser(0L, 1L, true, password1,password2, false, userModel.getScreenName(),userModel.getEmail(), 0L,"",
					locale, userModel.getFirstname(), "mn", userModel.getLastname(), 0, 0, true, 1, 1, 1940, "", null, null, null, null, true, serviceContext);
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error creating user ",userModel.getScreenName(), e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error adding user with screen name ",userModel.getScreenName(),e);
		}
	}

	public void deleteUser(String userId)throws UserManagementSystemException, UserRetrievalFault {
		try {
			UserLocalServiceUtil.deleteUser(this.convertStringToLong(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error deleting user ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error deleting user with user Id ",userId,e);
		}

	}


	public void dismissUserFromGroup(String groupId, String userId) throws UserManagementSystemException, NumberFormatException, GroupRetrievalFault, UserRetrievalFault {
		long[] userIds = {this.convertStringToLong(userId)};
		GroupManager groupManager = new LiferayGroupManager();
		long groupIdL = this.convertStringToLong(groupId);
		List<Organization> dismissedGroups = new ArrayList<Organization>();
		List<Organization> userOrgs = null;
		try{

			userOrgs = OrganizationLocalServiceUtil.getUserOrganizations(this.convertStringToLong(userId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// Remove the user from sub organizations
		if(groupManager.isRootVO(groupId)){
			//RootVO
			// Remove the user from all groups
			for(Organization userOrg : userOrgs){
				try {
					UserLocalServiceUtil.unsetOrganizationUsers(userOrg.getOrganizationId(), userIds);
				} catch (PortalException e) {
					throw new UserRetrievalFault("Check the validity userId /Group Id  ",e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
				}
				dismissedGroups.add(userOrg);
			}

		}else if (groupManager.isVO(groupId)){
			//VO
			//Remove user from VREs under the VO
			//Remove user from VO
			try {
				UserLocalServiceUtil.unsetOrganizationUsers(groupIdL, userIds);
			} catch (PortalException e) {
				throw new UserRetrievalFault("Check the validity of userId /Group Id  ",e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
			}
			try {
				dismissedGroups.add(OrganizationLocalServiceUtil.getOrganization(groupIdL));
			} catch (PortalException e) {
				throw new GroupRetrievalFault("Error retrieving group with group Id   ",String.valueOf(groupIdL), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
			}
			//Remove user from subgroups
			List<GroupModel> VREGroups = groupManager.listSubGroupsByGroup(groupId);
			for(GroupModel VREGroup : VREGroups){
				try {
					UserLocalServiceUtil.unsetOrganizationUsers(Long.parseLong(VREGroup.getGroupId()), userIds);
				} catch (PortalException e) {
					throw new UserRetrievalFault("Check the validity of userId /Group Id  ",e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
				}
				try {
					dismissedGroups.add(OrganizationLocalServiceUtil.getOrganization(Long.parseLong(VREGroup.getGroupId())));
				} catch (PortalException e) {
					throw new GroupRetrievalFault("Error retrieving group with group Id   ",VREGroup.getGroupId(), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
				}
			}				
		}else if(groupManager.isVRE(groupId)){
			//VRE
			//Remove user from the VRE
			try {
				UserLocalServiceUtil.unsetOrganizationUsers(groupIdL, userIds);
			} catch (PortalException e) {
				throw new UserRetrievalFault("Check the validity of userId /Group Id  ",e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
			}
			try {
				dismissedGroups.add(OrganizationLocalServiceUtil.getOrganization(groupIdL));
			} catch (PortalException e) {
				throw new GroupRetrievalFault("Error retrieving group with group Id   ",String.valueOf(groupIdL), e);
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
			}
		}

		// Remove all roles from the user that is relevant to the group
		List<Role> userRoles = null;
		try {
			userRoles = RoleLocalServiceUtil.getUserRoles(this.convertStringToLong(userId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
		}
		for(Organization dismissedGroup : dismissedGroups){
			String dismissedGroupName = dismissedGroup.getName();
			for(Role userRole : userRoles){
				if(userRole.getName().contains(dismissedGroupName)){
					long[] roleIds ={userRole.getRoleId()};
					try {
						RoleLocalServiceUtil.unsetUserRoles(this.convertStringToLong(userId), roleIds);
					} catch (PortalException e) {
						throw new UserRetrievalFault("Error unsetting user roles for user  ",userId, e);
					} catch (SystemException e) {
						throw new UserManagementSystemException("Error dismissing user from group ",groupId, e);
					}
				}
			}
		}
	}


	public UserModel getUser(String userId) throws UserManagementSystemException, UserRetrievalFault  {
		User user = null;
		UserModel um = new UserModel();
		try {
			user = UserLocalServiceUtil.getUser(this.convertStringToLong(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving user ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error retrieving user for user Id  ",userId, e);
		}
		um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
		return um;
	}


	public List<UserModel> listUsers() throws UserManagementSystemException, UserRetrievalFault {
		List<User> users = null;
		List<UserModel> userModels = new ArrayList<UserModel>();
		try {
			users = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
			for(User user : users){
				UserModel um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				userModels.add(um);
			}
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of users ", e);
		}
		return userModels;
	}


	public HashMap<UserModel, List<GroupModel>> listUsersAndGroupsByRole(String roleIdn) throws UserManagementSystemException, RoleRetrievalFault, UserRetrievalFault {
		List<User> users;
		long userId;
		HashMap<User, List<Organization>> hMap = new HashMap<User, List<Organization>>();
		HashMap<UserModel, List<GroupModel>> hMapCustom = new HashMap<UserModel, List<GroupModel>>();
		UserModel um = null;
		long roleId = this.convertStringToLong(roleIdn);
		try {
			users = UserLocalServiceUtil.getRoleUsers(roleId);
			for(User user : users){
				userId = user.getUserId();
				um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				Role role = null;
				List<Organization> group = new ArrayList<Organization>();
				List<GroupModel> groupModel = new ArrayList<GroupModel>();
				List<Organization> orgs = null;
				try {
					role = RoleLocalServiceUtil.getRole(roleId);
					orgs = OrganizationLocalServiceUtil.getUserOrganizations(userId);
				}  catch (PortalException e) {
					throw new RoleRetrievalFault("Error retrieving role for role Id",String.valueOf(roleId),e);
				}
				String roleName = role.getName();
				for(Organization org : orgs){
					if(roleName.contains(org.getName())){
						group.add(org);
						GroupModel gm = new GroupModel(String.valueOf(org.getOrganizationId()),String.valueOf(org.getParentOrganizationId()),org.getName(),org.getComments(),org.getLogoId());
						groupModel.add(gm);
						hMap.put(user, group);

					}
				}
				hMapCustom.put(um, groupModel);
			}
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of users and groups for role ",roleIdn, e);
		}
		return hMapCustom;
	}


	public HashMap<UserModel, List<RoleModel>> listUsersAndRolesByGroup(String orgIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault  {
		List<User> users = null;
		List<Role> roles = null;
		UserModel um = null;
		String orgName = null;
		String roleName = null;
		String completeRoleName = null;
		HashMap<UserModel, List<RoleModel>> hMap = new HashMap<UserModel, List<RoleModel>>();
		long orgId = this.convertStringToLong(orgIdn);
		LiferayRoleManager roleMan = new LiferayRoleManager();
		try {
			users = UserLocalServiceUtil.getOrganizationUsers(orgId);
			Organization org = OrganizationLocalServiceUtil.getOrganization(orgId);
			orgName = org.getName();
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of users for group Id ",orgIdn, e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Error retrieving group with group Id   ",orgIdn, e);
		}
		for(User user : users){
			List<RoleModel> userRoles = new ArrayList<RoleModel>();
			um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
			try {
				roles = user.getRoles();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Role Prod_Supp_role = roleMan.getCommonRole();
			if(Prod_Supp_role!=null){
				if(roles.contains(Prod_Supp_role)){				
					RoleModel rm = new RoleModel(Prod_Supp_role.getName(),String.valueOf(Prod_Supp_role.getRoleId()),Prod_Supp_role.getDescription());
					userRoles.add(rm);
				}
			}


			for(Role role : roles){
				if(role.getName().contains(orgName)){
					completeRoleName = role.getName();
					roleName = roleMan.getRoleName(completeRoleName);
					RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
					rm.setCompleteName(completeRoleName);
					userRoles.add(rm);				
				}
			}
			hMap.put(um,userRoles);
		}
		return hMap;
	}


	public List<UserModel> listUsersByGroup(String groupId) throws UserManagementSystemException, UserRetrievalFault {
		List<User> users = null;
		List<UserModel> userModels = new ArrayList<UserModel>();
		try {
			users = UserLocalServiceUtil.getOrganizationUsers(this.convertStringToLong(groupId));
			for(User user : users){
				UserModel um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				userModels.add(um);
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of group users for group Id ",groupId, e);
		}
		return userModels;
	}


	public List<UserModel> listUsersByGroupAndRole(String groupIdn, String roleIdn) throws UserManagementSystemException, UserRetrievalFault {
		List<User> users = new ArrayList<User>();
		List<UserModel> userModels = new ArrayList<UserModel>();
		List<User> groupUsers = null;
		long userId;
		long roleId = this.convertStringToLong(roleIdn);
		long groupId = this.convertStringToLong(groupIdn);
		try {
			groupUsers = UserLocalServiceUtil.getOrganizationUsers(groupId);
			for(User user : groupUsers){
				userId = user.getUserId();
				if(RoleLocalServiceUtil.hasUserRole(userId, roleId)){
					if(OrganizationLocalServiceUtil.hasUserOrganization(userId, groupId)){
						users.add(user);
						UserModel um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
						userModels.add(um);
					}
				}
			}
		}  catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of users by group and role", e);
		}
		return userModels;
	}


	public List<UserModel> listPendingUsersByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		List<UserModel> userModels = new ArrayList<UserModel>();
		List<MembershipRequest> memberRequests = null;
		//List<UserModel>  usermodels = new HashMap<UserModel, String> ();
		long groupId = this.convertStringToLong(groupIdn);
		Organization org;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupId);
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving pending users for group ",groupIdn, e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Error retrieving group with group Id   ",groupIdn, e);
		}

		for(MembershipRequest memberRequest: memberRequests){
			if(memberRequest.getGroupId()==org.getGroup().getGroupId()){
				long userId = memberRequest.getUserId();
				User user = null;
				try {
					user = UserLocalServiceUtil.getUser(userId);
				} catch (PortalException e) {
					throw new UserRetrievalFault("Error retrieving user for user Id  ",String.valueOf(userId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error retrieving pending users for group ",groupIdn, e);
				}
				UserModel um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				userModels.add(um);
			}
		}
		return userModels;
	}


	public void updateUser(UserModel usermodel) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException {
		try {
			User user = null;
			LiferayGroupManager gm = new LiferayGroupManager();
			long companyId = gm.getCompanyId();
			try {
				user = UserLocalServiceUtil.getUserByScreenName(companyId, usermodel.getScreenName());
				user.setEmailAddress(usermodel.getEmail());
				user.setFirstName(usermodel.getFirstname());
				user.setLastName(usermodel.getLastname());
			}catch (PortalException e) {
				throw new UserRetrievalFault("Error retrieving user by screen name ",usermodel.getScreenName(), e);
			}
			UserLocalServiceUtil.updateUser(user);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error updating user  ",usermodel.getScreenName(), e);
		}

	}


	public List<UserModel> listUnregisteredUsersByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{
		List<User> users = new ArrayList<User>();
		List<User> unregisteredUsers = new ArrayList<User>();
		List<UserModel> unregisteredUserModels = new ArrayList<UserModel>();

		long groupId = this.convertStringToLong(groupIdn);
		Organization org;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupId);
			users = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving unregistered users for group ",groupIdn, e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Error retrieving group with group Id   ",groupIdn, e);
		}

		for(User user : users){
			boolean isRegistered = false;
			//System.out.println(user.getFirstName());
			List<Organization> userOrgs = null;
			try {
				userOrgs = user.getOrganizations();
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			for(Organization userOrg : userOrgs){
				if(userOrg.getOrganizationId()==org.getOrganizationId()){
					isRegistered = true;
				}
			}
			if(!isRegistered){
				unregisteredUsers.add(user);
				UserModel um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
				unregisteredUserModels.add(um);
			}
		}
		return unregisteredUserModels;
	}

	public String getMembershipRequestComment(String userId, String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		long userIdL = convertStringToLong(userId);
		long groupIdL = convertStringToLong(groupId);
		String comment = null;
		List<MembershipRequest> memberRequests = null;
		try {
			Organization org = OrganizationLocalServiceUtil.getOrganization(groupIdL);
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
			for(MembershipRequest memberRequest: memberRequests){
				if(memberRequest.getGroupId()==org.getGroup().getGroupId() && memberRequest.getUserId() == userIdL){
					comment = memberRequest.getComments();
					break;
				}				
			}
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving membership request comment  ", e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Error retrieving group with group Id   ",groupId, e);
		}
		return comment;
	}

	public UserModel getUserByScreenName(String screenName) throws UserManagementSystemException, UserManagementPortalException, UserRetrievalFault {
		User user = null;
		UserModel um = null;
		LiferayGroupManager gm = new LiferayGroupManager();
		long companyId = gm.getCompanyId();
		try {
			user = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
			um = new UserModel(String.valueOf(user.getUserId()),user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName(),user.getCreateDate().getTime(),this.getUserCustomAttributes(String.valueOf(user.getUserId())));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving user by screename  ",screenName, e);
		} catch (PortalException e) {
			throw new UserManagementPortalException("Error retrieving user by screename  ",screenName, e);
		}
		return um;
	}


	
	public HashMap<String,String> getUserCustomAttributes(String userId)  throws UserManagementSystemException, UserRetrievalFault{

		User user = null;
		HashMap<String,String> hMap = new HashMap<String,String>();
		try {
			user = UserLocalServiceUtil.getUser(Long.parseLong(userId));
		}  catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving custom attributes of user  ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error retrieving user for user Id  ",userId, e);
		}

		try {
			
			long id = ExpandoClassCodeManager.getInstance().getClassCode(User.class);
			ExpandoTable table = ExpandoDefaultTableManager.getInstance().getExpandoDefaultTable(id);
			
			if (table != null)
			{
				long tableID = table.getTableId();
				List<ExpandoColumn> columns = ExpandoColumnLocalServiceUtil.getColumns(tableID);
				
				for (ExpandoColumn column : columns)
				{
					ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(tableID, column.getColumnId(), user.getUserId());
					String valueString = "";
					
					if (value != null && value.getString() != null) valueString = value.getString();
					
					//System.out.println(valueString);
					
					hMap.put(column.getName(), valueString);
				}
			
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return hMap;
	}
	

	public String getUserCustomAttributeByName(String userId, String attrName) throws UserManagementSystemException, UserRetrievalFault{
		User user = null;
		try {
			user = UserLocalServiceUtil.getUser(this.convertStringToLong(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving custom attributes of user  ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error retrieving user for user Id  ",userId, e);
		}
		
		String response = null;
		
		long id = ExpandoClassCodeManager.getInstance().getClassCode(User.class);
		ExpandoTable table = ExpandoDefaultTableManager.getInstance().getExpandoDefaultTable(id);
		
	
		if (table != null)
		{
			long tableID = table.getTableId();
			try {
			
				ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(tableID, attrName);
				
				if (column != null)
				{
					ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(tableID, column.getColumnId(), user.getUserId());
					
					if (value != null) response = value.getString();

				}
			} catch (SystemException e)
			{
				throw new UserManagementSystemException("Error retriving custom attributes",userId, e);
			} catch (PortalException e)
			{
				throw new UserRetrievalFault("Error retriving custom attributes  ",userId, e);
			}
		}


		return response;
	}

	public void setUserCustomAttributeByName(String userId, String attrName, String attrValue) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException{
		User user = null;
		boolean attrPresent = false;
		try {
			user = UserLocalServiceUtil.getUser(this.convertStringToLong(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error setting custom attributes of user  ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error retrieving user for user Id  ",userId, e);
		}
		Enumeration<String> attrNamesExist = user.getExpandoBridge().getAttributeNames(); 
		while(attrNamesExist.hasMoreElements()){
			if(attrNamesExist.nextElement().equals(attrName)){
				attrPresent = true;
				break;
			}
		}
		if(attrPresent){
			user.getExpandoBridge().setAttribute(attrName, attrValue);
		}else{
			try {
				user.getExpandoBridge().addAttribute(attrName);
				user.getExpandoBridge().setAttribute(attrName, attrValue);
			}  catch (PortalException e) {
				throw new UserManagementPortalException("Error setting custom attributes of user  ",userId, e);
			}

		}

	}

	public void setUserCustomAttributes(String userId, HashMap<String, String> hMap) throws UserManagementSystemException, UserRetrievalFault, UserManagementPortalException{
		User user = null;
		String attrName = null ;
		boolean attrPresent = false;
		try {
			user = UserLocalServiceUtil.getUser(this.convertStringToLong(userId));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error setting custom attributes of user  ",userId, e);
		} catch (PortalException e) {
			throw new UserRetrievalFault("Error retrieving user for user Id  ",userId, e);
		}

		Iterator<String> customAttributesIterNew = hMap.keySet().iterator();
		Enumeration<String> attrNamesExist = user.getExpandoBridge().getAttributeNames();
		while(customAttributesIterNew.hasNext()){
			attrPresent = false;
			String customAttribute = customAttributesIterNew.next();
			while(attrNamesExist.hasMoreElements()){
				attrName = attrNamesExist.nextElement();
				if(attrName.equals(customAttribute)){
					attrPresent = true;
					break;
				}
			}
			if(attrPresent){
				user.getExpandoBridge().setAttribute(attrName ,hMap.get(attrName) );
			}else{
				try {
					user.getExpandoBridge().addAttribute(customAttribute);
					user.getExpandoBridge().setAttribute(customAttribute, hMap.get(customAttribute));
				} catch (PortalException e) {
					throw new UserManagementPortalException("Error setting custom attributes of user  ",userId, e);
				}
			}

		}

	}

	public void denyMembershipRequest(String userId, String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException {
		List<MembershipRequest> memberRequests;
		Organization org;
		try {
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving Membership requests ", e);
		} 
		try {
			org = OrganizationLocalServiceUtil.getOrganization(this.convertStringToLong(groupId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error deleting membership request of user ", userId , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with groupId ",groupId, e);
		}
		for(MembershipRequest memberRequest:memberRequests){
			if(memberRequest.getGroupId()==org.getGroup().getGroupId() && memberRequest.getUserId() == this.convertStringToLong(userId)){
				try {
					MembershipRequestLocalServiceUtil.deleteMembershipRequest(memberRequest.getMembershipRequestId());
				} catch (PortalException e) {
					throw new UserManagementPortalException("Error deleting membership request of user ", userId , e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error deleting membership request of user ", userId , e);
				}
				break;
			}
		}
		//MembershipRequestLocalServiceUtil.updateStatus(replierUserId, membershipRequestId, replyComments, statusId)
	}

//	public List<CustomFieldModel> getUserCustomAttributes(String userId)
//			throws UserManagementSystemException, UserRetrievalFault 
//			{
//		HashMap<String, String> map = internalGetUserCustomAttributes(userId);
//		List<CustomFieldModel> response = new ArrayList<CustomFieldModel>();
//
//		if (map != null)
//		{
//			Iterator<String> keys = map.keySet().iterator();
//			
//			while (keys.hasNext())
//			{
//				String key = keys.next();
//				response.add(new CustomFieldModel(key, map.get(key)));
//			}
//		}
//		
//		return response;
//	}


}