package it.eng.rdlab.um.ldap.crossoperations;

import it.eng.rdlab.um.crossoperations.UserRoleOperations;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.service.LdapRoleManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserRoleOperations implements UserRoleOperations 
{
	private Log log;
	private LdapUserManager userManager;
	private LdapRoleManager roleManager;

	public LdapUserRoleOperations(LdapUserManager userManager, LdapRoleManager roleManager) throws ConfigurationException 
	{
		if (userManager == null || roleManager == null) throw new ConfigurationException("At least one of the default managers is null");

		this.userManager = userManager;
		this.roleManager = roleManager;
		this.log = LogFactory.getLog(this.getClass());
	}




	@Override
	public List<RoleModel> listRolesByUser(String userId) throws UserManagementSystemException, RoleRetrievalException, UserRetrievalException 
	{
		this.log.debug("Listing all roles that contains the user "+userId);
		LdapRoleModel filter = new LdapRoleModel();
		filter.addRoleOccupantDN(userId);
		List<RoleModel> roleModels = this.roleManager.listRoles(filter);
		this.log.debug("response size = "+roleModels.size());
		return roleModels;
	}

	@Override
	public void close() 
	{
		this.userManager.close();
		this.roleManager.close();

	}

	@Override
	public boolean assignRoleToUser(String roleId, String userId)
			throws UserManagementSystemException, UserRetrievalException,
			RoleRetrievalException {
		this.log.debug("Assign role to user");
		this.log.debug("Forcing to find the user in order to verify if exists");
		LdapUserModel userModel = (LdapUserModel) this.userManager.getUser(userId);
		this.log.debug("User exists");
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);


		if (roleModel == null)
		{
			log.error("Role "+roleId+" not found");
			throw new RoleRetrievalException("Role "+roleId+" not found");
		}
		else
			if (userModel == null){
				log.error("User "+userId+" not found");
				throw new UserRetrievalException("User "+userId+" not found");
			}
			else
			{
				roleModel.addRoleOccupantDN(userId);
				this.roleManager.updateRole(roleModel);
				return true;
			}
		

	}


	@Override
	public List<UserModel> listUserByRole(String roleId)
			throws UserManagementSystemException, UserRetrievalException,
			RoleRetrievalException {
		this.log.debug("Listing all the users contained in the role "+roleId);
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);
		List<String> userIds = roleModel.getRoleOccupantDNS();
		List<UserModel> response = new ArrayList<UserModel>();

		for (String dn : userIds)
		{

			log.debug("Getting user with DN "+dn);

			try 
			{

				UserModel userModel = this.userManager.getUser(dn);

				if (userModel == null)
				{
					this.log.error("User with dn "+dn+" not found!");
				}
				else
				{
					response.add(userModel);
				}
			} catch (Exception e)
			{
				this.log.warn("User "+dn+" not found");
			}
		}



		this.log.debug("Response size = "+response.size());
		return response;
	}




	@Override
	public boolean dismissRoleFromUser(String roleId, String userId)
			throws UserManagementSystemException, UserRetrievalException,
			RoleRetrievalException {
		this.log.debug("Removing user from role");
		LdapRoleModel roleModel = (LdapRoleModel) this.roleManager.getRole(roleId);
		boolean response = false;

		if (roleModel.getRoleOccupantDNS().remove(userId))
			response = this.roleManager.updateRole(roleModel);
		else
		{
			log.warn("Element "+userId+" not found");
			response = true;
		}

		this.log.debug("Operation complete with response "+response);
		return response;
	}



}
