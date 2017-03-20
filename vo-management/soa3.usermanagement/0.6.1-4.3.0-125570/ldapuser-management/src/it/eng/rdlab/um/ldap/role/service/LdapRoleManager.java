package it.eng.rdlab.um.ldap.role.service;

import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModelWrapper;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModelWrapper;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapRoleManager extends LdapRoleCollectionManager
{

	private Log log;

	public LdapRoleManager (String baseDn) throws NamingException
	{
		super (baseDn);
		this.log = LogFactory.getLog(this.getClass());
	}


	@Override
	public boolean createRole(RoleModel roleModel) throws UserManagementSystemException
	{
		this.log.debug("Creating role with dn "+roleModel.getRoleId());
		boolean response = false;
		try 
		{
			response = this.ldapManager.createDataElement(new LdapRoleModelWrapper(roleModel));
		} 
		catch (LdapManagerException e) 
		{
			throw new UserManagementSystemException("unable to create the role",e);
		}
		this.log.debug("Operation completed with response "+response);
		return response;
	}

	@Override
	public RoleModel getRole(String roleId) throws UserManagementSystemException, RoleRetrievalException 
	{
		this.log.debug("Getting role with dn "+roleId);

		try
		{
			return (LdapRoleModel) generateRole(roleId, new LdapRoleModelGenerator());
		} 
		catch (LdapManagerException e)
		{
			this.log.error("Unable to contact the Ldap server",e);
			throw new RoleRetrievalException("Unable to contact the Ldap server",e);
		} 
		catch (Exception e)
		{
			this.log.error("Generic LDAP error",e);
			throw new UserManagementSystemException("Unable to contact the Ldap server",e);
		}

	}

	@Override
	public boolean updateRole(RoleModel roleModel) throws UserManagementSystemException, RoleRetrievalException 
	{
		this.log.debug("Updating role");
		String roleDn = roleModel.getRoleId();
		RoleModel oldModel = getRole(roleDn);
		
		if (oldModel == null)
		{
			this.log.error("No role found");
			return false;
		}
		else
		{
			this.log.debug("Performing update operation...");
			boolean response = false;
			try {
				response = this.ldapManager.updateData(new LdapRoleModelWrapper(oldModel), new LdapRoleModelWrapper(roleModel));
				this.log.debug("Operation completed with response "+response);
				return true;
			
			} 
			catch (Exception e) 
			{
				this.log.error("Operation not completed",e);
				throw new UserManagementSystemException("Operation not completed",e);
			}
			
		}
	
	}

	@Override
	public List<RoleModel> listRoles() throws RoleRetrievalException 
	{
		this.log.debug("Generic search");
		RoleModel dummyFilter = new LdapRoleModel();
		dummyFilter.setRoleId(this.baseDn);
		return listRoles(dummyFilter);
	}

	@Override
	public List<RoleModel> listRoles(RoleModel filter) throws RoleRetrievalException 
	{
		this.log.debug("Filtered search");

		if (filter.getRoleId() == null || filter.getRoleId().length() == 0) filter.setRoleId(this.baseDn);

		return (List<RoleModel>) this.internalListRoles(new LdapRoleModelWrapper(filter), new LdapRoleModelGenerator());
	}

}
