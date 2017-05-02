package org.gcube.vomanagement.usermanagement.impl.liferay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementNameException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;



import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.MembershipRequest;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;

import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;


/**
 * Liferay plugin for the GroupManager interface, this implementation interacts with Liferay through a LocalService.
 * 
 * @author Giulio Galiero
 *
 */
public class LiferayGroupManager implements GroupManager {
	
	private static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";
	private final String REG_EX_MATCH = "^[A-Za-z0-9]+([-\\.\\_\\-ε]([A-Za-z0-9]+))*";
	
	private long getLongId(String groupId) throws NumberFormatException{

		long groupIdL = Long.parseLong(groupId);
		return groupIdL;
	}
	/**
	 * return the companyId
	 * @param webId .
	 * @return the company bean
	 * @throws PortalException .
	 * @throws SystemException .
	 */
	public static Company getCompany() throws PortalException, SystemException {
		return CompanyLocalServiceUtil.getCompanyByWebId(getDefaultCompanyWebId());
	}
	/**
	 * 
	 * @return the default company web-id (e.g. iMarine.eu)
	 */
	public static String getDefaultCompanyWebId() {
		String defaultWebId = "";
		try {
			defaultWebId = GetterUtil.getString(PropsUtil.get("company.default.web.id"));
		}
		catch (NullPointerException e) {
			System.out.println("Cound not find property company.default.web.id in portal.ext file returning default web id: " + DEFAULT_COMPANY_WEB_ID);
			return DEFAULT_COMPANY_WEB_ID;
		}
		return defaultWebId;
	}
	

	
	protected long getCompanyId() throws UserManagementSystemException, UserManagementPortalException{
		Company company = null;
		try {
			company = getCompany();
		} catch (PortalException e) {
			throw new UserManagementPortalException("Error retrieving companyId  ",  e);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving companyId  ",  e);
		}
		return company.getCompanyId();
	}

	/*	public GroupModel.GROUP_TYPE getGroupType(GroupModel gm){
		if(gm.getGroupName().startsWith("VO")){
			return GroupModel.GROUP_TYPE.VO;
		}else if(gm.getGroupName().contains("ROOT_VO")){
			return GroupModel.GROUP_TYPE.ROOT_VO;
		}else if(gm.getGroupName().contains("VRE")){
			return GroupModel.GROUP_TYPE.ROOT_VO;
		}
		return GroupModel.GROUP_TYPE.VO;
	}*/

	public String getScope(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		StringBuilder scope = new StringBuilder("/");
		Organization org;
		
		try{
			long groupIdL = this.getLongId(groupId);
			org = OrganizationLocalServiceUtil.getOrganization(groupIdL);
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving scope for group ", groupId , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group name ",groupId, e);
		}
			if (isRootVO(groupId))
				scope.append(org.getName());
			else if (isVO(groupId)){
				long rootVOId = getGroupParentId(groupId);
				Organization rootVO = null;
				try {
					rootVO = OrganizationLocalServiceUtil.getOrganization(rootVOId);
				} catch (PortalException e) {
					throw new GroupRetrievalFault("No group exists with group name ",String.valueOf(rootVOId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error retrieving scope for group ", groupId , e);
				}
				scope.append(rootVO.getName()).append("/").append(org.getName());
			}
			else if (isVRE(groupId)){
				long voId = getGroupParentId(groupId);
				Organization vo = null;
				try {
					vo = OrganizationLocalServiceUtil.getOrganization(voId);
				} catch (PortalException e) {
					throw new GroupRetrievalFault("No group exists with group name ",String.valueOf(voId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error retrieving scope for group ", groupId , e);
				}
				long rootVOId = getGroupParentId(String.valueOf(voId));
				Organization rootVO = null;
				try {
					rootVO = OrganizationLocalServiceUtil.getOrganization(rootVOId);
				} catch (PortalException e) {
					throw new GroupRetrievalFault("No group exists with group name ",String.valueOf(rootVOId), e);
				} catch (SystemException e) {
					throw new UserManagementSystemException("Error retrieving scope for group ", groupId , e);
				}
				scope.append(rootVO.getName()).append("/").append(vo.getName()).append("/").append(org.getName());
			}

		return scope.toString();

	}

	public Boolean isRootVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		if(this.getLongId(groupId)!=0){
			if (getGroupParentId(groupId) == 0)
				return true;
		}
		return false;		
	}

	public Boolean isVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		if(this.getLongId(groupId)!=0){
			long parentId = getGroupParentId(groupId);
			if (isRootVO(String.valueOf(parentId)))
				return true;
		}
			return false;
	}

	public Boolean isVRE(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		long parentId = getGroupParentId(groupId);
		if (isVO(String.valueOf(parentId)))
			return true;
		else
			return false;
	}

	/*
	  Already available in UserManager as listUsersByGroup!
	  
	public List<UserModel> getGroupUsers(long groupId){
		List<User> orgUsers = null;
		try {
			orgUsers = UserLocalServiceUtil.getOrganizationUsers(groupId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<UserModel> userModels = new ArrayList<UserModel>();
		for(User user : orgUsers){
			UserModel um = new UserModel(user.getFirstName(),user.getLastName(),user.getFullName(),user.getEmailAddress(),user.getScreenName());
			userModels.add(um);
		}
		return userModels;
	}
	 */

	public String getGroupId(String groupName) throws UserManagementSystemException, GroupRetrievalFault{
		List<Organization> organizations = null;
		long orgId = 0;
		try {
			organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving Id for group ", groupName , e);
		}
		for(Organization organization: organizations){
			if(organization.getName().equalsIgnoreCase(groupName)){
				orgId = organization.getOrganizationId();
				break;
			}
		}
		if(orgId == 0){
			throw new GroupRetrievalFault("No group exists with group name ",groupName);
		}
		return String.valueOf(orgId);	
	}

	public GroupModel getGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		Organization group = null;
		GroupModel gm = null;
		try {
			group = OrganizationLocalServiceUtil.getOrganization(this.getLongId(groupId));
			if(group!=null){
				gm = new GroupModel(String.valueOf(group.getOrganizationId()),String.valueOf(group.getParentOrganizationId()),group.getName(),group.getComments(),group.getLogoId());
			}else{
				//				throw new UserManagementException("No Group Found with Id " + groupId);
			}

		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving group  ", groupId , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group name ",groupId, e);
		}
		return gm;
	}

	public List<GroupModel> listGroups() throws UserManagementSystemException {
		List<Organization> groups = null;
		List<GroupModel> orgModels = new ArrayList<GroupModel>();
		try {
			groups = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			if(groups!=null){
				for(Organization group:groups){
					GroupModel gm = new GroupModel(String.valueOf(group.getOrganizationId()),String.valueOf(group.getParentOrganizationId()),group.getName(),group.getComments(),group.getLogoId());
					orgModels.add(gm);
				}
			}else{
				//				throw new UserManagementException("No Groups found ");
			}

		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving list of groups ",  e);
		}
		return orgModels;
	}

	public HashMap<String, List<RoleModel>> listGroupsAndRolesByUser(String userId) throws UserManagementSystemException {
		long userIdn = Long.parseLong(userId);
		HashMap<String, List<RoleModel>> hMap = new HashMap<String, List<RoleModel>>();
		List<Organization> groups = null;
		List<Role> roles = null;
		String roleName;
		String completeRoleName;
		LiferayRoleManager roleMan = new LiferayRoleManager();
		try {
			groups = OrganizationLocalServiceUtil.getUserOrganizations(userIdn);
			roles = RoleLocalServiceUtil.getUserRoles(userIdn);
		} catch (Exception e) {
			throw new UserManagementSystemException("Error listing groups and roles of user ", userId , e);
		}
		Iterator<Organization> groupIter = groups.iterator();
		while(groupIter.hasNext()){
			Organization group = (Organization)groupIter.next();
			List<RoleModel> userGroupRoles = new ArrayList<RoleModel>();
			for(Role role : roles){
				if(role.getName().contains(group.getName())){
					completeRoleName = role.getName();
					roleName = roleMan.getRoleName(completeRoleName);
					RoleModel rm = new RoleModel(roleName,String.valueOf(role.getRoleId()),role.getDescription());
					rm.setCompleteName(completeRoleName);
					userGroupRoles.add(rm);					
				}
			}
			hMap.put(group.getName(), userGroupRoles);
		}
		return hMap;
	}

	public List<GroupModel> listGroupsByUser(String userId) throws UserManagementSystemException {
		List<Organization> groups = null;
		List<GroupModel> orgModels = new ArrayList<GroupModel>();
		try {
			groups = OrganizationLocalServiceUtil.getUserOrganizations(getLongId(userId));
			for(Organization group:groups){
				GroupModel gm = new GroupModel(String.valueOf(group.getOrganizationId()),String.valueOf(group.getParentOrganizationId()),group.getName(),group.getComments(),group.getLogoId());
				orgModels.add(gm);
			}
		}catch (Exception e) {
			throw new UserManagementSystemException("Error retrieving groups of user ", userId , e);
		}
		return orgModels;
	}

	public List<GroupModel> listSubGroupsByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault{
		long groupId = getLongId(groupIdn);
		List<Organization> org = new ArrayList<Organization>();
		List<Organization> subOrgs = null;
		List<GroupModel> subOrgModels = new ArrayList<GroupModel>();
		try {
			org.add(OrganizationLocalServiceUtil.getOrganization(groupId));
			subOrgs = OrganizationLocalServiceUtil.getSuborganizations(org);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving sub groups of group ", groupIdn , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group name ",groupIdn, e);
		}
			
			if(subOrgs!=null){
				for(Organization suborg : subOrgs){
					GroupModel gm = new GroupModel(String.valueOf(suborg.getOrganizationId()),String.valueOf(suborg.getParentOrganizationId()),suborg.getName(),suborg.getComments(),suborg.getLogoId());
					subOrgModels.add(gm);
				}
			}else{
				//				throw new UserManagementException("No SubGroups present for the group " + groupIdn);
			}
	
		return subOrgModels;
	}

	public void assignSubGrouptoParentGroup(String subGroupId,
			String parentGroupId)  throws UserManagementSystemException, GroupRetrievalFault{
		Organization subOrg = null;
		try {
			subOrg = OrganizationLocalServiceUtil.getOrganization(this.getLongId(subGroupId));
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error assigning subGroup to Parent group ", e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Id  ", subGroupId, e);
		}
		subOrg.setParentOrganizationId(this.getLongId(parentGroupId));
		this.updateOrganization(subOrg);

	}

	private GroupModel createGroup(String groupName, String parentGroupId , String userId, String description) throws UserManagementNameException, UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException{
		/*Status Id is hard coded - ATTENTION*/
		ServiceContext serviceContext = new ServiceContext();
		Organization org = null;
		GroupModel gm = null;
		// Check for character, -, _, . which are valid characters for a group Name - requirement by Massi
		Pattern p = Pattern.compile(REG_EX_MATCH);
	    Matcher  m = p.matcher(groupName);
	    if(m.matches()){
		try {
			org = OrganizationLocalServiceUtil.addOrganization(getLongId(userId), this.getLongId(parentGroupId), groupName, "regular-organization", true, 0L, 0L, 12017, description, serviceContext);
			org.setCompanyId(getCompanyId());
			gm = new GroupModel(String.valueOf(org.getOrganizationId()),String.valueOf(org.getParentOrganizationId()),org.getName(),org.getComments(),org.getLogoId());
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error creating group ", groupName , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Check the groupName and the parent groupId ", groupName, e);
		}
	    }else{
	    	throw new UserManagementNameException("Not a valid group Name. The valid characters are only A-Z a-z - _ .");
	    }
	    return gm;
	}

	public void deleteGroup(String groupName) throws UserManagementSystemException, GroupRetrievalFault{
		long organizationId = this.getLongId(groupName);
		Organization organization;
		try {
			organization = OrganizationLocalServiceUtil.getOrganization(organizationId);
			OrganizationLocalServiceUtil.deleteOrganization(organization);
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error deleting group ", groupName , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group name ",groupName, e);
		}
			
			List<Role> roles;
			try {
				roles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
				for(Role role:roles){
					if(role.getName().contains(groupName)){
						RoleLocalServiceUtil.deleteRole(role);
					}
				}
			} catch (SystemException e) {
				throw new UserManagementSystemException("Error deleting group ", groupName , e);
			}
	
	
	}

	public void dismissSubGroupFromParentGroup(String subGroupId,
			String parentGroupId) throws UserManagementSystemException, GroupRetrievalFault{
		long subOrgId = this.getLongId(subGroupId);
		Organization org = null;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(subOrgId);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error dismissing subGroup from Parent Group ", e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with group Id ",subGroupId, e);
		}
			org.setParentOrganizationId(0);
			this.updateOrganization(org);
	}

	public GroupModel createRootVO(String rootVOName, String userId, String description) throws UserManagementNameException, UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException {
		return createGroup(rootVOName,"0",userId, description);
	}

	public GroupModel createVO(String VOName, String rootVOGroupId , String userId, String description) throws UserManagementNameException, UserManagementSystemException, UserManagementPortalException, GroupRetrievalFault {
		return createGroup(VOName,rootVOGroupId,userId,description);
	}

	public GroupModel createVRE(String VREName, String VOGroupId, String userId, String description) throws UserManagementNameException, UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException {
			return createGroup(VREName,VOGroupId,userId,description);
	}
	
	public GroupModel getRootVO() throws UserManagementSystemException, GroupRetrievalFault {
		
		List<Organization> orgs = null;
		GroupModel gm = null;
		try {
			orgs = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for(Organization org : orgs){
			if(this.isRootVO(String.valueOf(org.getOrganizationId()))){
				gm = new GroupModel(String.valueOf(org.getOrganizationId()),String.valueOf(org.getParentOrganizationId()),org.getName(),org.getComments(),org.getLogoId());
				break;
			}
		}
		return gm;
	}

	public String getRootVOName() throws UserManagementSystemException, GroupRetrievalFault {
		GroupModel gm = getRootVO();
		return gm.getGroupName();
	
	}

	public List<GroupModel> listPendingGroupsByUser(String userId) throws UserManagementSystemException {
		List<MembershipRequest> memberRequests = null;
		List<GroupModel> groupModels = new ArrayList<GroupModel>();
		long userIdL = this.getLongId(userId);
		try {
			memberRequests = MembershipRequestLocalServiceUtil.getMembershipRequests(0, MembershipRequestLocalServiceUtil.getMembershipRequestsCount());
			for(MembershipRequest memberRequest: memberRequests){
				if(memberRequest.getUserId() == userIdL){
					long groupId = memberRequest.getGroupId();
					List<Organization> orgs = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
					for(Organization org : orgs){
						if(org.getGroup().getGroupId()==groupId){		
							GroupModel gm = new GroupModel(String.valueOf(org.getOrganizationId()),String.valueOf(org.getParentOrganizationId()),org.getName(),org.getComments(),org.getLogoId());
							groupModels.add(gm);
							break;
						}
					}
				}
			}
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving pending groups for user ", userId , e);
		}
		return groupModels;
	}

	public void updateGroup(GroupModel group) throws UserManagementSystemException, GroupRetrievalFault{
		Organization org = null;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(Long.parseLong(group.getGroupId()));
		}catch (SystemException e) {
			throw new UserManagementSystemException("Error updating group ", group.getGroupName() , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with groupId ",group.getGroupId(), e);
		}
		this.updateOrganization(org);
	}

	private void updateOrganization(Organization org) throws UserManagementSystemException{
		try {
			OrganizationLocalServiceUtil.updateOrganization(org);
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error updating group " , e);
		}
	}

	public long getGroupParentId(String groupId) throws UserManagementSystemException, GroupRetrievalFault{
		long groupIdL = this.getLongId(groupId);
		Organization org = null;
		long parentOrgId = 0;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(groupIdL);

		} catch (SystemException e) {
			throw new UserManagementSystemException("Error retrieving parent Group Id for group ", groupId , e);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("No group exists with groupId ",groupId, e);
		}
		parentOrgId = org.getParentOrganizationId();
		return parentOrgId;
	}
}
