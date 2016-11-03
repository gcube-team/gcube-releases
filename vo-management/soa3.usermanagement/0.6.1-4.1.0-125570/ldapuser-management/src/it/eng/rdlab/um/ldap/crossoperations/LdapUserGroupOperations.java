package it.eng.rdlab.um.ldap.crossoperations;

import it.eng.rdlab.um.crossoperations.UserGroupOperations;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.service.LdapGroupManager;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserGroupOperations implements UserGroupOperations 
{
	private Log log;
	private LdapUserManager userManager;
	private LdapGroupManager groupManager;
	
	public LdapUserGroupOperations(LdapUserManager userManager, LdapGroupManager groupManager) throws ConfigurationException 
	{
		if (userManager == null || groupManager == null) throw new ConfigurationException("At least one of the default managers is null");
		
		this.userManager = userManager;
		this.groupManager = groupManager;
		this.log = LogFactory.getLog(this.getClass());
	}
	
	@Override
	public boolean assignUserToGroup(String userId, String groupId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException 
	{
		this.log.debug("Assign user to group");
		this.log.debug("Forcing to find the user in order to verify if exists");
		this.userManager.getUser(userId);
		this.log.debug("User exists");
		LdapGroupModel groupModel = (LdapGroupModel) this.groupManager.getGroup(groupId);
		
		if (groupModel == null)
		{
			log.error("Group "+groupId+" not found");
			throw new GroupRetrievalException("Group "+groupId+" not found");
		}
		else
		{
			groupModel.addMemberDN(userId);
			boolean response = this.groupManager.updateGroup(groupModel);
			this.log.debug("Operation complete with response "+response);
			return response;
		}

		
	}

	@Override
	public boolean dismissUserFromGroup(String userId, String groupId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException 
	{
		this.log.debug("Removing user from group");
		LdapGroupModel groupModel = (LdapGroupModel) this.groupManager.getGroup(groupId);
		boolean response = false;
		
		if (groupModel.getMemberDNS().remove(userId)) response = this.groupManager.updateGroup(groupModel);
		else
		{
			log.warn("Element "+userId+" not found");
			response = true;
		}

		this.log.debug("Operation complete with response "+response);
		return response;
	}

	@Override
	public List<GroupModel> listGroupsByUser(String userId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException 
	{
		this.log.debug("Listing all groups that contains the user "+userId);
		LdapGroupModel filter = new LdapGroupModel();
		filter.addMemberDN(userId);
		List<GroupModel> groupModels = this.groupManager.listGroups(filter);
		this.log.debug("response size = "+groupModels.size());
		return groupModels;
	}

	@Override
	public List<UserModel> listUsersByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException 
	{
		return listUsersByGroup(groupId, null);
	}
	
	
public List<UserModel> listUsersByGroup(String groupId, List<String> excludedDn) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException 
	{
		this.log.debug("Listing all the users contained in the group "+groupId);
		LdapGroupModel groupModel = (LdapGroupModel) this.groupManager.getGroup(groupId);
		List<String> userIds = groupModel.getMemberDNS();
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
	public void close() 
	{
		this.userManager.close();
		this.groupManager.close();
		
	}

}
