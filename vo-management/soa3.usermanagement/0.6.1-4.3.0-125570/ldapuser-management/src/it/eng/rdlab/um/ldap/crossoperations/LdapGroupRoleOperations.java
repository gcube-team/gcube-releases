package it.eng.rdlab.um.ldap.crossoperations;

import it.eng.rdlab.um.crossoperations.GroupRoleOperations;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.service.LdapGroupManager;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.service.LdapRoleManager;
import it.eng.rdlab.um.role.beans.RoleModel;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapGroupRoleOperations implements GroupRoleOperations 
{
	private Log log;
	private LdapGroupManager groupManager;
	private LdapRoleManager roleManager;

	public LdapGroupRoleOperations(LdapGroupManager groupManager, LdapRoleManager roleManager) throws ConfigurationException 
	{
		if (groupManager == null || roleManager == null) throw new ConfigurationException("At least one of the default managers is null");

		this.groupManager = groupManager;
		this.roleManager = roleManager;
		this.log = LogFactory.getLog(this.getClass());
	}

	@Override
	public void close() 
	{
		this.groupManager.close();
		this.roleManager.close();

	}
	
	@Override
	public List<RoleModel> listRolesByGroup(String groupId) throws RoleRetrievalException, GroupRetrievalException
	{
		this.log.debug("Listing all roles that contains the group "+groupId);
		LdapRoleModel filter = new LdapRoleModel();
		filter.addRoleOccupantDN(groupId);
		List<RoleModel> roleModels = this.roleManager.listRoles(filter);
		this.log.debug("response size = "+roleModels.size());
		return roleModels;
	}


	@Override
	public void assignRoleToGroup(String roleId, String groupId)
			throws UserManagementSystemException, GroupRetrievalException,
			RoleRetrievalException {
		this.log.debug("Assign role to group");
		this.log.debug("Forcing to find the group in order to verify if exists");
		this.groupManager.getGroup(groupId);
		this.log.debug("Group exists");
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);
		LdapGroupModel groupModel = (LdapGroupModel) this.groupManager.getGroup(groupId);

		if (roleModel == null)
		{
			log.error("Role "+roleId+" not found");
			throw new RoleRetrievalException("Role "+roleId+" not found");
		}
		else
			if (groupModel == null){
				log.error("Group "+groupId+" not found");
				throw new GroupRetrievalException("Group "+groupId+" not found");
			}
			else
			{
				roleModel.addRoleOccupantDN(groupId);
				this.roleManager.updateRole(roleModel);
				groupModel.setRole(roleId);
				this.groupManager.updateGroup(groupModel);
				
			}
		

	}

	@Override
	public void dismissRoleFromGroup(String roleId, String groupId)
			throws UserManagementSystemException, GroupRetrievalException,
			RoleRetrievalException {
		this.log.debug("Removing group from role");
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);
		boolean response = false;

		if (roleModel.getRoleOccupantDNS().remove(groupId)) response = this.roleManager.updateRole(roleModel);
		else
		{
			log.warn("Element "+groupId+" not found");
			response = true;
		}

		this.log.debug("Operation complete with response "+response);
	}

	@Override
	public List<GroupModel> listGroupsByRole(String roleId)
			throws UserManagementSystemException, RoleRetrievalException, GroupRetrievalException {
		this.log.debug("Listing all the groups contained in the role "+roleId);
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);
		List<String> groupIds = roleModel.getRoleOccupantDNS();
		List<GroupModel> response = new ArrayList<GroupModel>();

		for (String dn : groupIds)
		{

			log.debug("Getting group with DN "+dn);

			try 
			{

				GroupModel groupModel = this.groupManager.getGroup(dn);

				if (groupModel == null)
				{
					this.log.error("Group with dn "+dn+" not found!");
				}
				else
				{
					response.add(groupModel);
				}
			} catch (Exception e)
			{
				this.log.warn("Group "+dn+" not found");
			}
		}



		this.log.debug("Response size = "+response.size());
		return response;
	}





}
