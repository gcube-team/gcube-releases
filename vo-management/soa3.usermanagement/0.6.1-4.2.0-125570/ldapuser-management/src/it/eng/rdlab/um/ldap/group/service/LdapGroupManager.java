package it.eng.rdlab.um.ldap.group.service;

import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModelWrapper;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapGroupManager extends LdapCollectionManager
{

	private Log log;

	public LdapGroupManager (String baseDn) throws NamingException
	{
		super (baseDn);
		this.log = LogFactory.getLog(this.getClass());
	}
	

	@Override
	public boolean createGroup(GroupModel groupModel) throws UserManagementSystemException
	{
		this.log.debug("Creating group with dn "+groupModel.getGroupId());
		boolean response = false;
		try 
		{
			response = this.ldapManager.createDataElement(new LdapGroupModelWrapper(groupModel));
		} 
		catch (LdapManagerException e) 
		{
			throw new UserManagementSystemException("unable to create the group",e);
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
			return (LdapGroupModel) generateGroup(groupId, new LdapGroupModelGenerator());
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
			this.log.error("No groups found");
			return false;
		}
		else
		{
			this.log.debug("Performing update operation...");
			boolean response = false;
			try {
				response = this.ldapManager.updateData(new LdapGroupModelWrapper(oldModel), new LdapGroupModelWrapper(groupModel));
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
		GroupModel dummyFilter = new LdapGroupModel();
		dummyFilter.setGroupId(this.baseDn);
		return listGroups(dummyFilter);
	}

	@Override
	public List<GroupModel> listGroups(GroupModel filter) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Filtered search");
		
		if (filter.getGroupId() == null || filter.getGroupId().length() == 0) filter.setGroupId(this.baseDn);
		
		return (List<GroupModel>) this.internalListGroups(new LdapGroupModelWrapper(filter), new LdapGroupModelGenerator());
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<GroupModel> listSubGroupsByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Listing subgroups for group "+groupId);
		GroupModel group = getGroup(groupId);
		
		if (group == null)
		{
			log.error("Main group not found");
			throw new GroupRetrievalException("Main group not found");
		}
		else
		{
			log.debug("Getting members...");
			GenericModelWrapper wrapper = new GenericModelWrapper(group);
			List<String> members = (List<String>)wrapper.getObjectParameter(LdapGroupModel.MEMBERS_DN);
			
			List<GroupModel> response = new ArrayList<GroupModel>();
			
			if (members != null)
			{
				log.debug("Loading all groups");
				List<GroupModel> completeList = listGroups();
				
				for (GroupModel candidate : completeList)
				{
					log.debug("Checking if "+candidate.getGroupId()+ " is a subgroup");
					
					if (members.contains(candidate.getGroupId()))
					{
						this.log.debug("Is a subgroup!");
						response.add(candidate);
					}
					
					
				}
			}
			
			this.log.debug("List completed");
			return response;

			
		}

	}

	@Override
	public boolean createSubGroup(String parentGroupId, GroupModel groupModel) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Creating new Sub Group...");
		GroupModel parentModel = getGroup(parentGroupId);
		
		if (parentModel == null)
		{
			log.error("Parent group not found");
			throw new GroupRetrievalException("Parent group not found");
		}
		else
		{
			String subGroupDN = groupModel.getGroupId();
			log.debug("Sub group DN = "+subGroupDN);
			
			if (subGroupDN == null) 
			{
				log.error("Subgroup DN not set");
				throw new GroupRetrievalException("Subgroup DN not set");
			}
			else
			{
				GroupModel subGroupModel = null;
				
				try 
				{
					subGroupModel = getGroup(subGroupDN);
				
				} 
				catch (Exception e)
				{
					this.log.error("An error occurred in finding the group");
				}
				
				if (subGroupModel == null && createGroup(groupModel))
				{
					log.debug("Subgroup not found: created");
					subGroupModel = groupModel;
					
				}
				else if (subGroupModel == null) // unable to create the group
				{
					log.error("Unable to create the sub group");
					return false;
				}
				
				this.log.debug("Updating sub group reference ");

				LdapGroupModel padGroupModel = new LdapGroupModel(parentModel);
				padGroupModel.addMemberDN(subGroupDN);
				boolean response = updateGroup(padGroupModel);
				log.debug("Operation completed with result "+response);
				return response;

				
			}
			
		}
	}

}
