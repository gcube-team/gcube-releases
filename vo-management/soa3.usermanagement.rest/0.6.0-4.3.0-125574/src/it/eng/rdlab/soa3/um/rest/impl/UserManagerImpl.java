package it.eng.rdlab.soa3.um.rest.impl;

import it.eng.rdlab.soa3.um.rest.IUserManagementService.OrganizationManager;
import it.eng.rdlab.soa3.um.rest.IUserManagementService.UserManager;
import it.eng.rdlab.soa3.um.rest.bean.RoleModel;
import it.eng.rdlab.soa3.um.rest.bean.UserModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.utils.Utils;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserGroupOperations;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserRoleOperations;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.service.LdapGroupManager;
import it.eng.rdlab.um.ldap.role.service.LdapRoleManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class is a layer between the RESTFul WS and the LDAPUserManagement for operations on users
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class UserManagerImpl implements UserManager
{

	private Logger logger = null;
	private String ldapUrl = null;
	private OrganizationManager organizationManager;
	
	public UserManagerImpl(String ldapUrl) 
	{
		logger = Logger.getLogger(this.getClass());
		this.ldapUrl = ldapUrl;
		
		this.organizationManager = new OrganizationManagerImpl(ldapUrl);
	}
	
	@Override
	public boolean assignRoleToUser(String roleName, String userId,String organizationName,String adminUserId, String password) 
	{
		logger.debug("Assigning role "+roleName+ " to user "+userId);
		LdapUserRoleOperations manager = null;
		boolean response = false;
		String organizationId = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserRoleOperations(new LdapUserManager(organizationId), new LdapRoleManager(organizationId));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			String userDn = Utils.userDNBuilder(userId, organizationName);
			String roleDn = Utils.roleDNBuilder(roleName, organizationName);
			logger.debug("User DN "+userDn);
			logger.debug("Role DN "+roleDn);
			response = manager.assignRoleToUser(roleDn, userDn);

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
	public String createUser(UserModel userModel, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Creating user: "+userModel.getUserId());
		
		if (!this.organizationManager.existsOrganization(organizationName, adminUserId, password) && (this.organizationManager.createOrganization(organizationName, adminUserId, password)==null))
		{
			logger.debug("Organization "+organizationName + " doesn't exists and unable to create it");
			return null;
		}
		
		LdapUserManager manager = null;
		String response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try 
		{
			LdapUserModel user = new LdapUserModel();
			user.addObjectClass(LdapUserModel.OBJECT_CLASS_ORGANIZATIONALPERSON);
			String dn = Utils.userDNBuilder(userModel.getUserId(), organizationName);
			user.setFullname(dn);
			user.setUserId(userModel.getUserId());
			user.setCN(userModel.getFirstname());
			user.setSN(userModel.getLastname());
			String userPassword = userModel.getPassword();
			
			if (userPassword != null) user.setPassword(userPassword.toCharArray());
			
			user.addExtraAttribute(LdapUserModel.EMAIL, userModel.getEmail());
			
			if (userModel.getCertDN() != null) user.addExtraAttribute(LdapUserModel.CERTIFICATE,userModel.getCertDN());
			
			response = manager.createUser(user) ? userModel.getUserId() : null;
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
	public boolean deleteUser(String userId, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Deleting user: "+userId);
		LdapUserManager manager = null;
		boolean response = false;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		
		try 
		{
			String userDn = Utils.userDNBuilder(userId, organizationName);
			response = manager.deleteUser(userDn);
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;

	}

	

	
	@Override
	public HashMap<UserModel, List<RoleModel>> listAllUsersAndRoles(String organizationName, String adminUserId, String password) 
	{
		logger.debug("Getting all users and roles from: "+organizationName);
		LdapUserManager manager = null;
		HashMap<UserModel, List<RoleModel>> response = null;
		String organizationDn = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(organizationDn);
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try 
		{
			manager = new LdapUserManager(organizationDn);
			List<it.eng.rdlab.um.user.beans.UserModel> ldapUsers = manager.listUsers();
			LdapUserGroupOperations userGroupManager = new LdapUserGroupOperations(manager, new LdapGroupManager(Utils.organizationDNBuilder(organizationName)));
			response = new HashMap<UserModel, List<RoleModel>>();
			
			for (it.eng.rdlab.um.user.beans.UserModel ldapUser : ldapUsers)
			{
				LdapUserModel ldapUserModel = (LdapUserModel) ldapUser;
				logger.debug("Getting roles of the user "+ldapUser.getFullname());
				UserModel model = Utils.convertUserModel (ldapUserModel);
				logger.debug("User model built");
				List<RoleModel> roleList = new ArrayList<RoleModel>();
				
				try
				{
					List<GroupModel> ldapGroup = userGroupManager.listGroupsByUser(ldapUserModel.getFullname());
					
					for (GroupModel gm : ldapGroup)
					{
						RoleModel role = new RoleModel();
						role.setRoleId(gm.getGroupId());
						role.setRoleName(gm.getGroupName());
						role.setDescription(gm.getDescription());
						logger.debug("Role model created");
						roleList.add(role);
						
					}
				}
				catch (Exception e)
				{
					logger.debug("No roles found");
				}
				
				response.put(model, roleList);
					
					
			}
			

		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed");
		return response;

	}

	
	@Override
	public List<UserModel> listUsers(String adminUserId, String password) 
	{
		return listUsersByOrganization("", adminUserId, password);
	}

	
	@Override
	public List<UserModel> listUsersByRole(String roleName, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Listing all the users with role "+roleName);
		LdapUserRoleOperations manager = null;
		List<UserModel> response = null;
		String organizationDn = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserRoleOperations(new LdapUserManager(organizationDn), new LdapRoleManager(organizationDn));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try {
			response = new ArrayList<UserModel>();
			String roleDn = Utils.roleDNBuilder(roleName, organizationName);
			logger.debug("Role DN "+roleDn);
			// Excluding the root organization
//			ArrayList<String> rootOrganization = new ArrayList<String>();
//			rootOrganization.add(Constants.ROOT_TREE);
		
			List<it.eng.rdlab.um.user.beans.UserModel> ldapUserModelList = manager.listUserByRole(roleDn);
			
			for (it.eng.rdlab.um.user.beans.UserModel um : ldapUserModelList)
			{
				LdapUserModel ldapUserModel = (LdapUserModel) um;
				logger.debug("Getting roles of the user "+ldapUserModel.getFullname());
				UserModel model = Utils.convertUserModel (ldapUserModel);
				response.add(model);
			}

		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		
		manager.close();
		logger.debug("Operation completed with an array of "+response.size()+" elements");
		
		return response;
	}

	
	@Override
	public List<UserModel> listUsersByOrganization(String organizationName, String adminUserId, String password) 
	{
		return listUsersByOrganizationAndAttributes(organizationName, null, adminUserId, password);
	}

	
	@Override
	public boolean updateUser(UserModel user, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Updating user: "+user.getUserId());
		LdapUserManager manager = null;
		boolean response = false;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			LdapUserModel ldapUser = new LdapUserModel();
			ldapUser.addObjectClass(LdapUserModel.OBJECT_CLASS_ORGANIZATIONALPERSON);
			String dn = Utils.userDNBuilder(user.getUserId(), organizationName);
			ldapUser.setFullname(dn);
			ldapUser.setUserId(user.getUserId());
			ldapUser.setCN(user.getFirstname());
			ldapUser.setSN(user.getLastname());
			
			String userPassword = user.getPassword();
			
			if (userPassword != null) ldapUser.setPassword(userPassword.toCharArray());
			
			ldapUser.addExtraAttribute(LdapUserModel.EMAIL, user.getEmail());
			ldapUser.addExtraAttribute(LdapUserModel.CERTIFICATE, user.getCertDN());
			response = manager.updateUser(ldapUser);
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
	public int changePassword(String userId, String oldPassword, String newPassword, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Changing password to user "+userId);
		int response = CHANGE_PASSWORD_OK;
		
		if (newPassword == null)
		{
			logger.debug("new password null");
			response =  CHANGE_PASSWORD_INVALID_NEW_PASSWORD;
		}
		else if (oldPassword == null)
		{
			logger.debug("The old password is null;");
			response = CHANGE_PASSWORD_WRONG_USER_PASSWORD;
		}
		else 
		{
			newPassword = newPassword.trim();
			oldPassword = oldPassword.trim();
		
			if (newPassword.equals(oldPassword))
			{
				logger.debug("The two passwords are equal");
				response = CHANGE_PASSWORD_EQUAL_PASSWORDS;
			}
			
			else 
			{
			
				try 
				{
					Utils.initLdap(Utils.userDNBuilder(userId, organizationName), oldPassword,this.ldapUrl);
				} 
				catch (NamingException e) 
				{
					response = CHANGE_PASSWORD_WRONG_USER_PASSWORD;
					logger.debug("User password invalid..",e);
					return response;
				}
				
				logger.debug("Loading user...");
				UserModel currentModel = getUser(userId, organizationName, adminUserId, password);
				
				if (currentModel == null)
				{
					logger.debug("User not found");
					 response =  CHANGE_PASSWORD_USER_NOT_FOUND;
				}
				else
				{
					logger.debug("Start update operation...");
					currentModel.setPassword(newPassword);
					boolean updateRes = updateUser(currentModel, organizationName, adminUserId, password);
					logger.debug("Update operation result "+updateRes);
					response = updateRes ? CHANGE_PASSWORD_OK : CHANGE_PASSWORD_GENERIC_ERROR;

				}
			}
			
		}
		return response;
	}

	@Override
	public boolean deleteUsers(String organizationName, String adminUserId, String password) 
	{

		logger.debug("Removing all users for organization "+organizationName);
		logger.debug("Removing users from roles");
		boolean clearRolesResult = clearUsersFromRoles(organizationName, adminUserId, password);
		logger.debug("Clear role operation result = "+clearRolesResult);
		LdapUserManager manager = null;
		boolean response = true;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}

		try 
		{
			List<it.eng.rdlab.um.user.beans.UserModel> users = manager.listUsers();
			
			for (it.eng.rdlab.um.user.beans.UserModel um : users)
			{
				String userDn = um.getFullname();
				logger.debug("Removing user "+userDn);
				boolean partialResponse = manager.deleteUser(userDn);
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

	private boolean clearUsersFromRoles (String organizationName, String adminUserId, String password)
	{
		logger.debug("Clearing roles");
		LdapGroupManager manager = null;
		boolean response = true;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapGroupManager(Utils.organizationDNBuilder(organizationName));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try {
			List<GroupModel> ldapGroupModelList = manager.listGroups();
			
			for (GroupModel gm : ldapGroupModelList)
			{
				LdapGroupModel ldapGroupModel = (LdapGroupModel) gm;
				logger.debug("Deleting users from "+ldapGroupModel.getGroupId());
				ldapGroupModel.getMemberDNS().clear();
				ldapGroupModel.addMemberDN(ConfigurationManager.getInstance().getLdapBase());
				boolean partialResponse = manager.updateGroup(ldapGroupModel);
				logger.debug("Partial update operation result = "+partialResponse);
				response = response & partialResponse;

			}

		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
			response = false;
		}
		
		
		manager.close();
		logger.debug("Operation completed with response "+response);
		return response;
		
	}

	@Override
	public UserModel getUser(String userId, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Get user "+userId);
		LdapUserManager manager = null;
		UserModel response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}

		try 
		{
			LdapUserModel user = (LdapUserModel) manager.getUser(Utils.userDNBuilder(userId, organizationName));
			
			if (user == null) 
			{
				logger.debug("User Not found");
			}
			else
			{
				logger.debug("User Found");
				response = Utils.convertUserModel(user);
			}
			
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		return response;
	}

	@Override
	public boolean dismissRoleToUser(String roleName, String userId, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Dismissing role "+roleName+ " from user "+userId);
		LdapUserRoleOperations manager = null;
		boolean response = false;
		String organizationId = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserRoleOperations(new LdapUserManager(organizationId), new LdapRoleManager(organizationId));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			String userDn = Utils.userDNBuilder(userId, organizationName);
			String roleDn = Utils.roleDNBuilder(roleName, organizationName);
			logger.debug("User DN "+userDn);
			logger.debug("Role DN "+roleDn);
			response = manager.dismissRoleFromUser(roleDn, userDn);

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
	public boolean addUserToGroup(String userId, String groupId,
			String organizationName, String adminUserId, String password) {
		logger.debug("Adding user "+userId+ " to group user "+groupId);
		LdapUserGroupOperations manager = null;
		boolean response = false;
		String organizationId = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserGroupOperations(new LdapUserManager(organizationId), new LdapGroupManager(organizationId));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			String userDn = Utils.userDNBuilder(userId, organizationName);
			String groupDn = Utils.groupDNBuilder(groupId, organizationName);
			logger.debug("User DN "+userDn);
			logger.debug("Group DN "+groupDn);
			response = manager.assignUserToGroup(userDn,groupDn);

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
	public boolean removeUserFromGroup(String userId, String groupId,
			String organizationName, String adminUserId, String password) {
		logger.debug("Adding user "+userId+ " to group user "+groupId);
		LdapUserGroupOperations manager = null;
		boolean response = false;
		String organizationId = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserGroupOperations(new LdapUserManager(organizationId), new LdapGroupManager(organizationId));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			String userDn = Utils.userDNBuilder(userId, organizationName);
			String groupDn = Utils.groupDNBuilder(groupId, organizationName);
			logger.debug("User DN "+userDn);
			logger.debug("Group DN "+groupDn);
			response = manager.dismissUserFromGroup(userDn,groupDn);

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
	public List<UserModel> listUsersByGroup(String groupName,
			String organizationName, String adminUserId, String password) {
		logger.debug("Listing all the users in the group "+groupName);
		LdapUserGroupOperations manager = null;
		List<UserModel> response = null;
		String organizationDn = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserGroupOperations(new LdapUserManager(organizationDn), new LdapGroupManager(organizationDn));
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try {
			response = new ArrayList<UserModel>();
			String groupDN = Utils.groupDNBuilder(groupName, organizationName);
			logger.debug("Group DN "+groupDN);
			// Excluding the root organization					
				
			List<it.eng.rdlab.um.user.beans.UserModel> ldapUserModelList = manager.listUsersByGroup(groupDN);
			
			for (it.eng.rdlab.um.user.beans.UserModel um : ldapUserModelList)
			{
				LdapUserModel ldapUserModel = (LdapUserModel) um;
				logger.debug("Getting groups of the user "+ldapUserModel.getFullname());
				UserModel model = Utils.convertUserModel (ldapUserModel);
				response.add(model);
			}

		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		
		manager.close();
		logger.debug("Operation completed with an array of "+response.size()+" elements");
		
		return response;
	}

	@Override
	public HashMap<UserModel, List<it.eng.rdlab.soa3.um.rest.bean.GroupModel>> listAllUsersAndGroups(
			String organizationName, String adminUserId, String password) {
		logger.debug("Getting all users and groups from: "+organizationName);
		LdapUserManager manager = null;
		HashMap<UserModel, List<it.eng.rdlab.soa3.um.rest.bean.GroupModel>> response = null;
		String organizationDn = Utils.organizationDNBuilder(organizationName);
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(organizationDn);
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try 
		{
			manager = new LdapUserManager(organizationDn);
			List<it.eng.rdlab.um.user.beans.UserModel> ldapUsers = manager.listUsers();
			LdapUserGroupOperations userGroupManager = new LdapUserGroupOperations(manager, new LdapGroupManager(Utils.organizationDNBuilder(organizationName)));
			response = new HashMap<UserModel, List<it.eng.rdlab.soa3.um.rest.bean.GroupModel>>();
			
			for (it.eng.rdlab.um.user.beans.UserModel ldapUser : ldapUsers)
			{
				LdapUserModel ldapUserModel = (LdapUserModel) ldapUser;
				logger.debug("Getting groups of the user "+ldapUser.getFullname());
				UserModel model = Utils.convertUserModel (ldapUserModel);
				logger.debug("User model built");
				List<it.eng.rdlab.soa3.um.rest.bean.GroupModel> groupList = new ArrayList<it.eng.rdlab.soa3.um.rest.bean.GroupModel>();
				
				try
				{
					List<GroupModel> ldapGroup = userGroupManager.listGroupsByUser(ldapUserModel.getFullname());
					
					for (GroupModel gm : ldapGroup)
					{
						it.eng.rdlab.soa3.um.rest.bean.GroupModel group = new it.eng.rdlab.soa3.um.rest.bean.GroupModel();
						group.setGroupId(gm.getGroupId());
						group.setGroupName(gm.getGroupName());
						group.setDescription(gm.getDescription());
						logger.debug("Group model created");
						groupList.add(group);
						
					}
				}
				catch (Exception e)
				{
					logger.debug("No roles found");
				}
				
				response.put(model, groupList);
					
					
			}
			

		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed");
		return response;
	}

	private List<it.eng.rdlab.um.user.beans.UserModel> internalListUsers (Map<String, String> attributes, LdapUserManager manager) throws Exception
	{
		logger.debug("Getting users");
		if (attributes == null)
		{
			logger.debug("No attributes set");
			return manager.listUsers();
		}
		else
		{
			LdapUserModel filter = new LdapUserModel();
			
			Iterator<String> keyIterator = attributes.keySet().iterator();
			
			while (keyIterator.hasNext())
			{
				String key = keyIterator.next();
				String value = attributes.get(key);
				logger.debug("Adding attribute "+key+ " "+value);
				filter.addExtraAttribute(key, value);
			}
			
			logger.debug("Listing values");
			return manager.listUsers(filter);
		}
	}
	


	@Override
	public List<UserModel> listUsersByOrganizationAndAttributes(String organizationName, Map<String, String> attributes,String adminUserId, String adminPassword) 
	{
		logger.debug("Listing all the users");
		LdapUserManager manager = null;
		List<UserModel> response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			manager = new LdapUserManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}

		try 
		{
			response = new ArrayList<UserModel>();
			List<it.eng.rdlab.um.user.beans.UserModel> users = internalListUsers (attributes,manager);
			
			for (it.eng.rdlab.um.user.beans.UserModel um : users)
			{
				logger.debug("Generating user model");
				UserModel model = Utils.convertUserModel((LdapUserModel) um);
				logger.debug("User model generated");
				response.add(model);
			}
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		return response;
	}

	@Override
	public List<UserModel> listUsersByAttribute(Map<String, String> attributes,String adminUserId, String adminPassword) 
	{
		return listUsersByOrganizationAndAttributes("", attributes, adminUserId, adminPassword);
	}




}
