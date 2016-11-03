package it.eng.rdlab.soa3.um.rest.impl;

import it.eng.rdlab.soa3.um.rest.IUserManagementService.RoleManager;
import it.eng.rdlab.soa3.um.rest.bean.RoleModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.utils.Utils;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserRoleOperations;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.service.LdapRoleManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class is a layer between the RESTFul WS and the LDAPUserManagement for operations on roles
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class RoleManagerImpl implements RoleManager{

	static Logger logger = Logger.getLogger(RoleManagerImpl.class.getName());
	private String ldapUrl = null;
	
	private String[] system_roles ={"TENANT_ADMIN","END_USER"};
	
	public RoleManagerImpl(String ldapUrl) 
	{
		this.ldapUrl = ldapUrl;
	}
	
	@Override
	public String createRole(String roleName, String organizationName,String adminUserId, String password) 
	{
		logger.debug("Creating role: "+roleName);
		LdapRoleManager manager = null;
		String response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		try 
		{
			LdapRoleModel roleModel = new LdapRoleModel();
			String dn = Utils.roleDNBuilder(roleName, organizationName);
			roleModel.setRoleId(dn);
			roleModel.setRoleName(roleName);
			
			
			
			
			//roleModel.addRoleOccupantDN(Constants.ROOT_TREE);
			roleModel.addRoleOccupantDN("");
			
			// END
			
			response = manager.createRole(roleModel) ? dn : null;
		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
		
	}
	
	public boolean updateRolesOfOrganization(List<String> roleNames, String organizationName,String adminUserId, String password) 
	{
		//logger.debug("Creating role: "+roleName);
		LdapRoleManager manager = null;
		String response = null;
		String finalRoleName;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			List<it.eng.rdlab.um.role.beans.RoleModel> roles = manager.listRoles();
			List<String> groupNames = new ArrayList<String>();
			Iterator<it.eng.rdlab.um.role.beans.RoleModel> rolesIter = roles.iterator();
			while (rolesIter.hasNext()) {
				it.eng.rdlab.um.role.beans.RoleModel roleModel2 = (it.eng.rdlab.um.role.beans.RoleModel) rolesIter.next();
				finalRoleName = roleModel2.getRoleName();
				if(roleNames.contains(finalRoleName)){
					groupNames.add(finalRoleName);
				}else{
					if(!finalRoleName.equals("TENANT_ADMIN")){
						boolean isDeleted = deleteRole(finalRoleName, organizationName, adminUserId, password);
						if(!isDeleted){
							logger.error("An old role: "+finalRoleName+" is not deleted for organization "+ organizationName);
						}
					}
					
					
				}
		
				
			}
			LdapRoleModel ldapRoleModel = new LdapRoleModel();
			for(String roleName:roleNames){
				String dn = Utils.roleDNBuilder(roleName, organizationName);
				ldapRoleModel.setRoleId(dn);
				ldapRoleModel.setRoleName(roleName);
				//ldapRoleModel.addRoleOccupantDN(Constants.ROOT_TREE);
				
				if(!groupNames.contains(roleName)){
					logger.debug("Rolename does not exist already, hence creating one: "+ roleName);
					finalRoleName = manager.createRole(ldapRoleModel) ? dn : null;
					if(finalRoleName == null){
						logger.debug("An error occourred during the operation");
						return false;
					}
				}else{
					logger.debug("Role already present, so leaving it as it is: "+ roleName);
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
	public boolean deleteRole(String roleName, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Deleting role: "+roleName);
		LdapRoleManager manager = null;
		boolean response = false;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		
		try 
		{
			String dn = Utils.roleDNBuilder(roleName, organizationName);
			response = manager.deleteRole(dn);
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}


	@Override
	public String getRoleIdByName(String roleName, String organizationName) 
	{
		return Utils.roleDNBuilder(roleName, organizationName);
	}

	 
	@Override
	public List<RoleModel> listRoles(String adminUserId, String password) 
	{
		
		return listRolesByOrganization("", adminUserId, password);
		
	}

	 
	@Override
	public List<RoleModel> listRolesByOrganization(String organizationName, String adminUserId, String password) 
	{
		logger.debug("Listing all roles for organization "+organizationName);
		LdapRoleManager manager = null;
		List<RoleModel> response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		
		try 
		{
			response = new ArrayList<RoleModel>();
			List<it.eng.rdlab.um.role.beans.RoleModel> modelList = manager.listRoles();
			
			for (it.eng.rdlab.um.role.beans.RoleModel currentRole : modelList)
			{
				String roleDn = currentRole.getRoleId();
				logger.debug("Generating role model for role "+roleDn);
				RoleModel role = new RoleModel();
				logger.debug("Role Id = "+roleDn);
				role.setRoleId(roleDn);
				role.setRoleName(currentRole.getRoleName());
				role.setDescription(currentRole.getDescription());
				logger.debug("Model generated");
				response.add(role);
			}
		
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}

	 
	public List<RoleModel> listRolesByUser(String userId,String organizationName, String adminUserId, String password) 
	{
		logger.debug("Getting all user roles for "+ userId+ " from: "+organizationName);
		LdapUserManager manager = null;
		String organizationDn;
		List<RoleModel> roleList = new ArrayList<RoleModel>();
		List<it.eng.rdlab.um.role.beans.RoleModel> ldapRole = new ArrayList<it.eng.rdlab.um.role.beans.RoleModel>();
		try 
		{
		   organizationDn = Utils.organizationDNBuilder(organizationName);
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapUserManager(organizationDn);
			LdapUserModel user = (LdapUserModel) manager.getUser(Utils.userDNBuilder(userId, organizationName));
			LdapUserRoleOperations userRoleManager = new LdapUserRoleOperations(manager, new LdapRoleManager(Utils.organizationDNBuilder(organizationName)));
			ldapRole = userRoleManager.listRolesByUser(user.getFullname());
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
		} catch (UserManagementSystemException e) {
			logger.debug("System error ");
		} catch (UserRetrievalException e) {
			logger.debug("No users found");
		} catch (RoleRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		for (it.eng.rdlab.um.role.beans.RoleModel gm : ldapRole)
		{
			RoleModel role = new RoleModel();
			role.setRoleId(gm.getRoleId());
			role.setRoleName(gm.getRoleName());
			role.setDescription(gm.getDescription());
			logger.debug("Role model created");
			roleList.add(role);
			
		}
		return roleList;
		
	}

	
	@Override
	public boolean updateRole(RoleModel role, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Updating role: "+role.getRoleName());
		LdapRoleManager manager = null;
		boolean response = false;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		try 
		{
			LdapRoleModel group = new LdapRoleModel();
			group.setDN(role.getRoleId());
			group.setRoleName(role.getRoleName());
			group.setDescription(role.getDescription());
			response = manager.updateRole(group);
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
	public boolean deleteRoles(String organizationName, String adminUserId, String password) 
	{
		logger.debug("Removing all roles for organization "+organizationName);
		LdapRoleManager manager = null;
		boolean response = true;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
	
		
		try 
		{

			List<it.eng.rdlab.um.role.beans.RoleModel> modelList = manager.listRoles();
			
			for (it.eng.rdlab.um.role.beans.RoleModel role : modelList)
			{
				String roleDn = role.getRoleId();
				logger.debug("Deleting role model for group "+roleDn);
				boolean partialResponse = manager.deleteRole(roleDn);
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
	public boolean removeAllUsers(String roleName, String organizationName, String adminUserId, String password) 
	{
		logger.debug("Removing all users from the role "+roleName);
		LdapRoleManager manager = null;
		boolean response = false;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		
		try 
		{
			LdapRoleModel group = (LdapRoleModel)manager.getRole(Utils.roleDNBuilder(roleName, organizationName));
			group.getRoleOccupantDNS().clear();
			group.addRoleOccupantDN(ConfigurationManager.getInstance().getLdapBase());
			logger.debug("Updating LDAP");
			response = manager.updateRole(group);
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
	public RoleModel getRole(String roleName, String organizationName, String adminUserId, String password)
	{
		logger.debug("Getting the role "+roleName);
		LdapRoleManager manager = null;
		RoleModel response = null;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}
		
		
		try 
		{
			LdapRoleModel ldapRoleModel = (LdapRoleModel)manager.getRole(Utils.roleDNBuilder(roleName, organizationName));
	
			String roleDn = ldapRoleModel.getRoleId();
			logger.debug("Generating role model for group "+roleDn);
			RoleModel roleModel = new RoleModel();
			roleModel.setRoleId(roleDn);
			roleModel.setRoleName(ldapRoleModel.getRoleName());
			roleModel.setDescription(ldapRoleModel.getDescription());
			logger.debug("Model generated");
		
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response != null);
		return response;
		
	}

	public boolean deleteCustomRoles(String organizationName, String adminUserId,
			String password) {
		logger.debug("Removing all roles for organization "+organizationName);
		LdapRoleManager manager = null;
		boolean response = true;
		
		try 
		{
			Utils.initLdap(adminUserId, password,this.ldapUrl);
			manager = new LdapRoleManager(Utils.organizationDNBuilder(organizationName));
			
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}
		
		
		try 
		{

			List<it.eng.rdlab.um.role.beans.RoleModel> modelList = manager.listRoles();
			
			for (it.eng.rdlab.um.role.beans.RoleModel role : modelList)
			{
				if(!(role.getRoleName().equals(system_roles[0]) || role.getRoleName().equals(system_roles[1]))){
					String roleDn = role.getRoleId();
					logger.debug("Deleting role model for role "+roleDn);
					boolean partialResponse = manager.deleteRole(roleDn);
					logger.debug("Operation result "+partialResponse);
					response = response & partialResponse;
				}
	
			}
		
			
		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}
		
		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}


	

}
