package it.eng.rdlab.um.ldap.group.service;

import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModel;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModelWrapper;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapOrganizationManager extends LdapCollectionManager
{

	private Log log;
	
	public LdapOrganizationManager (String baseDn) throws NamingException
	{
		super (baseDn);
		this.log = LogFactory.getLog(this.getClass());
		this.ldapManager = LdapManager.getInstance();
		this.baseDn = baseDn != null ? baseDn : "";
	}
	
	@Override
	public boolean createGroup(GroupModel groupModel) throws UserManagementSystemException 
	{
		this.log.debug("Creating group with dn "+groupModel.getGroupId());
		boolean response = false;
		try 
		{
			response = this.ldapManager.createDataElement(new LdapOrganizationModelWrapper(groupModel));
		} 
		catch (LdapManagerException e) 
		{
			throw new UserManagementSystemException("unable to create the user",e);
		}
		this.log.debug("Operation completed with response "+response);
		return response;
	}
	
	@Override
	public GroupModel getGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Getting group with dn "+groupId);
		
		try
		{
			return (GroupModel) generateGroup(groupId, new LdapOrganizationModelGenerator());
		} 
			catch (LdapManagerException e)
		{
			this.log.error("Unable to contact the Ldap server",e);
			throw new GroupRetrievalException("Unable to contact the Ldap server",e);
		} 
		catch (Exception e)
		{
				this.log.error("Generic LDAP error",e);
				throw new UserManagementSystemException("Unable to contact the Ldap server",e);
		}

	}
	

	
	@Override
	public boolean updateGroup(GroupModel groupModel) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Updating user");
		String groupDn = groupModel.getGroupId();
		GroupModel oldModel = getGroup(groupDn);
		
		if (oldModel == null)
		{
			this.log.error("No user found");
			return false;
		}
		else
		{
			this.log.debug("Performing update operation...");
			boolean response = false;
			try {
				response = this.ldapManager.updateData(new LdapOrganizationModelWrapper(oldModel), new LdapOrganizationModelWrapper(groupModel));
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
	public List<GroupModel> listGroups() throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Generic search");
		GroupModel dummyFilter = new LdapOrganizationModel();
		dummyFilter.setGroupId(this.baseDn);
		return listGroups(dummyFilter);
	}

	@Override
	public List<GroupModel> listGroups(GroupModel filter) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Filtered search");
		
		if (filter.getGroupId() == null || filter.getGroupId().length() == 0) filter.setGroupId(this.baseDn);
		
		return this.internalListGroups(new LdapOrganizationModelWrapper(filter), new LdapOrganizationModelGenerator());
	}



	@Override
	public List<GroupModel> listSubGroupsByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Listing subgroups for organization "+groupId);
		
		try 
		{
			this.log.debug("Using default group manager to find subgroups");
			LdapGroupManager groupManager = new LdapGroupManager(groupId);
			return groupManager.listGroups();
		} catch (NamingException e)
		{
			this.log.error("Generic LDAP error",e);
			throw new UserManagementSystemException("Unable to contact the Ldap server",e);
		}
			
	}

	@Override
	public boolean createSubGroup(String parentGroupId, GroupModel groupModel) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Creating new Sub Group...");
		
		this.log.debug("Listing subgroups for organization "+parentGroupId);
		
		try 
		{
			this.log.debug("Using default group manager to find subgroups");
			LdapGroupManager groupManager = new LdapGroupManager(parentGroupId);
			return groupManager.createGroup(groupModel);
		} catch (NamingException e)
		{
			this.log.error("Generic LDAP error",e);
			throw new UserManagementSystemException("Unable to contact the Ldap server",e);
		}

	}

}
