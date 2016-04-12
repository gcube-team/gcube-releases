package it.eng.rdlab.soa3.um.rest.impl;

import it.eng.rdlab.soa3.um.rest.IUserManagementService.GroupManager;
import it.eng.rdlab.soa3.um.rest.bean.GroupModel;
import it.eng.rdlab.soa3.um.rest.utils.Utils;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserGroupOperations;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.service.LdapGroupManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class is a layer between the RESTFul WS and the LDAPUserManagement for operations on groups
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class GroupManagerImpl implements GroupManager{

	static Logger logger = Logger.getLogger(GroupManagerImpl.class.getName());
	private String ldapUrl = null;


	public GroupManagerImpl(String ldapUrl) 
	{
		this.ldapUrl = ldapUrl;
	}

	@Override
	public String createGroup(String groupName, String organizationName,String description, String adminUserId, String password) 
	{
		logger.debug("Creating group: "+groupName);
		LdapGroupManager manager = null;
		String response = null;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}

		try 
		{
			LdapGroupModel groupModel = new LdapGroupModel();
			String dn = Utils.groupDNBuilder(groupName, organizationName);
			groupModel.setGroupId(dn);
			groupModel.setGroupName(groupName);
			groupModel.setDescription(description);
			groupModel.addMemberDN("");

			response = manager.createGroup(groupModel) ? dn : null;
		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;

	}

	@Override
	public boolean deleteGroup(String groupName, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Deleting group: "+groupName);
		LdapGroupManager manager = null;
		boolean response = false;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}


		try 
		{
			String dn = Utils.groupDNBuilder(groupName, organizationName);
			response = manager.deleteGroup(dn,false);

		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}

	@Override
	public List<GroupModel> listGroupsByOrganization(String organizationName, String adminUserId, String password) 
	{
		logger.debug("Listing all groups for organization "+organizationName);
		LdapGroupManager manager = null;
		List<GroupModel> response = null;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}


		try 
		{
			response = new ArrayList<GroupModel>();
			List<it.eng.rdlab.um.group.beans.GroupModel> modelList = manager.listGroups();

			for (it.eng.rdlab.um.group.beans.GroupModel currentGroup : modelList)
			{
				String groupDn = currentGroup.getGroupId();
				logger.debug("Generating group model for group "+groupDn);
				GroupModel group = new GroupModel();
				group.setGroupId(groupDn);
				group.setGroupName(currentGroup.getGroupName());
				group.setDescription(currentGroup.getDescription());
				logger.debug("Model generated");
				response.add(group);
			}


		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}


	public List<GroupModel> listGroupsByUser(String userId,String organizationName, String adminUserId, String password) 
	{
		logger.debug("Getting all user groups for "+ userId+ " from: "+organizationName);
		LdapUserManager manager = null;
		String organizationDn;
		List<GroupModel> groupList = new ArrayList<GroupModel>();
		List<it.eng.rdlab.um.group.beans.GroupModel> ldapGroup = new ArrayList<it.eng.rdlab.um.group.beans.GroupModel>();
		try 
		{
			organizationDn = Utils.organizationDNBuilder(organizationName);
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(organizationDn);
			LdapUserModel user = (LdapUserModel) manager.getUser(Utils.userDNBuilder(userId, organizationName));
			LdapUserGroupOperations userGroupManager = new LdapUserGroupOperations(manager, new LdapGroupManager(Utils.organizationDNBuilder(organizationName)));
			ldapGroup = userGroupManager.listGroupsByUser(user.getFullname());

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
		} catch (UserManagementSystemException e) {
			logger.debug("System error ");
		} catch (UserRetrievalException e) {
			logger.debug("No users found");
		} catch (GroupRetrievalException e) {
			e.printStackTrace();
		}


		for (it.eng.rdlab.um.group.beans.GroupModel gm : ldapGroup)
		{
			GroupModel group = new GroupModel();
			group.setGroupId(gm.getGroupId());
			group.setGroupName(gm.getGroupName());
			group.setDescription(gm.getDescription());
			logger.debug("Group model created");
			groupList.add(group);

		}
		return groupList;

	}


	@Override
	public boolean updateGroup(GroupModel group, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Updating group: "+group.getGroupName());
		LdapGroupManager manager = null;
		boolean response = false;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}

		try 
		{
			String dn = Utils.groupDNBuilder(group.getGroupName(), organizationName);
			LdapGroupModel ldapGroupModel = new LdapGroupModel();
			ldapGroupModel.setDN(dn);
			ldapGroupModel.setGroupName(group.getGroupName());
			ldapGroupModel.setDescription(group.getDescription());
			response = manager.updateGroup(ldapGroupModel);
		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;

	}

	public boolean updateGroupsOfOrganization(List<String> groupsNameList, String organizationName,String adminUserId, String password) 
	{
		//logger.debug("Creating group: "+roleName);
		LdapGroupManager manager = null;
		String response = null;
		String finalGroupName;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}

		try 
		{
			List<it.eng.rdlab.um.group.beans.GroupModel> groups = manager.listGroups();
			List<String> groupNames = new ArrayList<String>();
			Iterator<it.eng.rdlab.um.group.beans.GroupModel> groupsIter = groups.iterator();
			while (groupsIter.hasNext()) {
				it.eng.rdlab.um.group.beans.GroupModel roleModel2 = (it.eng.rdlab.um.group.beans.GroupModel) groupsIter.next();
				finalGroupName = roleModel2.getGroupName();
				if(groupsNameList.contains(finalGroupName)){
					groupNames.add(finalGroupName);
				}else{
					boolean isDeleted = deleteGroup(finalGroupName, organizationName, adminUserId, password);
					if(!isDeleted){
						logger.error("An old group: "+finalGroupName+" is not deleted for organization "+ organizationName);
					}
				}


			}
			LdapGroupModel ldapGroupModel = new LdapGroupModel();
			for(String groupName:groupsNameList){
				String dn = Utils.groupDNBuilder(groupName, organizationName);
				ldapGroupModel.setGroupId(dn);
				ldapGroupModel.setGroupName(groupName);
				//ldapGroupModel.addMemberDN(Constants.ROOT_TENANT);

				if(!groupNames.contains(groupName)){
					logger.debug("Groupname does not exist already, hence creating one: "+ groupName);
					finalGroupName = manager.createGroup(ldapGroupModel) ? dn : null;
					if(finalGroupName == null){
						logger.debug("An error occourred during the operation");
						return false;
					}
				}else{
					logger.debug("Group already present, so leaving it as it is: "+ groupName);
				}

			}

		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
			return false;
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return true;

	}


	@Override
	public boolean deleteGroups(String organizationName, String adminUserId, String password) 
	{
		logger.debug("Removing all groups for organization "+organizationName);
		LdapGroupManager manager = null;
		boolean response = true;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}


		try 
		{

			List<it.eng.rdlab.um.group.beans.GroupModel> modelList = manager.listGroups();

			for (it.eng.rdlab.um.group.beans.GroupModel group : modelList)
			{
				String groupDn = group.getGroupId();
				logger.debug("Deleting group model for group "+groupDn);
				boolean partialResponse = manager.deleteGroup(groupDn,false);
				logger.debug("Operation result "+partialResponse);
				response = response & partialResponse;
			}


		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}



	@Override
	public boolean removeAllUsers(String groupName, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Removing all users from the group "+groupName);
		LdapGroupManager manager = null;
		boolean response = false;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}


		try 
		{
			LdapGroupModel group = (LdapGroupModel)manager.getGroup(Utils.groupDNBuilder(groupName, organizationName));
			group.getMemberDNS().clear();
			logger.debug("Updating LDAP");
			response = manager.updateGroup(group);
			logger.debug("Update completed");
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;


	}

	@Override
	public GroupModel getGroup(String groupName, String organizationName, String adminUserId, String password)
	{
		logger.debug("Getting the group "+groupName);
		LdapGroupManager manager = null;
		GroupModel response = null;

		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}

		try 
		{
			LdapGroupModel ldapGroupModel = (LdapGroupModel)manager.getGroup(Utils.groupDNBuilder(groupName, organizationName));
			
			if (ldapGroupModel != null)
			{
				String groupDn = ldapGroupModel.getGroupId();
				logger.debug("Generating group model for group "+groupDn);
				response = new GroupModel();
				response.setGroupId(groupDn);
				response.setGroupName(ldapGroupModel.getGroupName());
				response.setDescription(ldapGroupModel.getDescription());
				logger.debug("Model generated");
			}


		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response != null);
		return response;

	}






}
