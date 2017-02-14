package it.eng.rdlab.um.ldap.client;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.configuration.LdapConfiguration;
import it.eng.rdlab.um.ldap.crossoperations.LdapGroupRoleOperations;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserGroupOperations;
import it.eng.rdlab.um.ldap.crossoperations.LdapUserRoleOperations;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModel;
import it.eng.rdlab.um.ldap.group.service.LdapGroupManager;
import it.eng.rdlab.um.ldap.group.service.LdapOrganizationManager;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.service.LdapRoleManager;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.service.LdapUserManager;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.user.beans.UserModel;

public class Client 
{

	private static final String adminRN = "cn=Directory Manager";
	private static final String adminPwd = "secret";
	private static final String rootDN = "dc=vision,dc=eu";

	private static LdapConfiguration ldapConfiguration(){
		LdapConfiguration configuration = new LdapConfiguration();
		configuration.setUrl("ldap://localhost:1389");
		configuration.setUserDn(adminRN+","+rootDN);
		configuration.setPassword(adminPwd);
		return configuration;
	}

	public void listUsers () throws Exception
	{

		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserManager manager = new LdapUserManager(rootDN);
		List<UserModel> users = manager.listUsers();

		for (UserModel user : users)
		{
			LdapUserModel ldapUser = (LdapUserModel) user;
			System.out.println("*** User Full Name: "+ldapUser.getFullname());
			System.out.println("*** User CN: "+ldapUser.getCN());
			System.out.println("*** User Id: "+ldapUser.getUserId());
			System.out.println("*** User SN: "+ldapUser.getSN());
			System.out.println();
		}
		manager.close();
	}
	
	public void listGroups () throws Exception
	{

		LdapManager.initInstance(Client.ldapConfiguration());
		LdapGroupManager manager = new LdapGroupManager(rootDN);
		List<GroupModel> groups = manager.listGroups();

		for (GroupModel group : groups)
		{
			LdapGroupModel ldapGroup = (LdapGroupModel) group;
			System.out.println("*** Group Name: "+ldapGroup.getGroupName());
			System.out.println("*** Group Description: "+ldapGroup.getDescription());
			System.out.println();
		}
		manager.close();
	}






	public void createOrganization2 ()throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapOrganizationManager manager = new LdapOrganizationManager(rootDN);
		LdapOrganizationModel model = new LdapOrganizationModel();
		model.setOrganizationDN("dc=ciro,dc=vision,dc=eu");
		//		model.setDC("Infrastructure");
		//		model.setO("Infra");
		//		model.setDescription("Area infrastrutture");
		//		model.addExtraAttribute("domainComponent", "ciro");
		System.out.println(manager.createGroup(model));
		System.out.println();
		manager.close();
	}



	public void deleteGroup(String groupDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapGroupManager manager = new LdapGroupManager(rootDN);
		System.out.println(manager.deleteGroup(groupDN, false));
		System.out.println();
		manager.close();
	}



	public void removeUserFromGroup(String userDN, String groupDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserGroupOperations manager = new LdapUserGroupOperations(new LdapUserManager(rootDN), new LdapGroupManager(rootDN));
		System.out.println(manager.dismissUserFromGroup(userDN, groupDN));
		System.out.println();
		manager.close();
	}



	public void listUsersInGroup(String groupDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserGroupOperations manager = new LdapUserGroupOperations(new LdapUserManager(rootDN), new LdapGroupManager(rootDN));
		ArrayList<String> excludedDN = new ArrayList<String>();
		excludedDN.add(rootDN);
		List<UserModel> users = manager.listUsersByGroup(groupDN,excludedDN);

		for (UserModel user : users)
		{
			LdapUserModel ldapUser = (LdapUserModel) user;
			System.out.println("*** User Full Name: "+ldapUser.getFullname());
			System.out.println("*** User SN: "+ldapUser.getSN());
			System.out.println();
		}
		manager.close();
	}

	public void listGroupsFromUser (String memberDN) throws Exception
	{ 
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserGroupOperations manager = new LdapUserGroupOperations(new LdapUserManager(rootDN), new LdapGroupManager(rootDN));
		List<GroupModel> groups = manager.listGroupsByUser(memberDN);

		for (GroupModel group : groups)
		{
			LdapGroupModel ldapGroup = (LdapGroupModel) group;
			System.out.println("*** Group DN: "+ldapGroup.getDN());
			System.out.println("*** Group Description: "+ldapGroup.getDescription());
			System.out.println();
		}
		manager.close();
	}


	public void createUser(String userCN, String userSurname, String userPwd) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserManager manager = new LdapUserManager(rootDN);
		LdapUserModel ldapUserModel = new LdapUserModel();
		ldapUserModel.setUserId(userCN);
		ldapUserModel.setFullname("cn="+userCN+","+rootDN);
		ldapUserModel.setSN(userSurname);
		ldapUserModel.setPassword(userPwd.toCharArray());
		System.out.println(manager.createUser(ldapUserModel));
		manager.close();
	}

	public void createGroup(String groupDN, String groupName, String groupDesc, String memberDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapGroupManager manager = new LdapGroupManager(rootDN);
		LdapGroupModel model = new LdapGroupModel();
		model.setDN(groupDN);
		model.setGroupName(groupName);
		model.setDescription(groupDesc);
		model.addMemberDN(memberDN);
		System.out.println(manager.createGroup(model));
		manager.close();
	}


	public void addUserToGroup (String userDN, String groupDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserGroupOperations manager = new LdapUserGroupOperations(new LdapUserManager(rootDN), new LdapGroupManager(rootDN));
		System.out.println(manager.assignUserToGroup(userDN, groupDN));
		manager.close();
	}

	public void addRoleToUser (String userDN, String roleDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserRoleOperations manager = new LdapUserRoleOperations(new LdapUserManager(rootDN), new LdapRoleManager(rootDN));

		System.out.println(manager.assignRoleToUser(roleDN, userDN));
		manager.close();
	}

	public void createRole(String roleDN, String roleName, String roleDesc, String roleOccupantDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapRoleManager manager = new LdapRoleManager(rootDN);
		LdapRoleModel model = new LdapRoleModel();
		model.setDN(roleDN);
		model.setRoleName(roleName);
		model.setDescription(roleDesc);
		model.addRoleOccupantDN(roleOccupantDN);

		System.out.println(manager.createRole(model));
		manager.close();
	}

	public void listRoles(String rolesDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapRoleManager manager = new LdapRoleManager(rolesDN);
		List<RoleModel> roles = manager.listRoles();

		for (RoleModel role : roles)
		{
			LdapRoleModel ldapRole = (LdapRoleModel) role;
			System.out.println("*** Role DN: "+ldapRole.getDN());
			System.out.println("*** Role Description: "+ldapRole.getDescription());
		}
		manager.close();
	}

	public void listRolesFromUser(String userDN) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserRoleOperations manager = new LdapUserRoleOperations(new LdapUserManager(rootDN), new LdapRoleManager(rootDN));
		List<RoleModel> roles = manager.listRolesByUser(userDN);

		for (RoleModel role : roles)
		{
			LdapRoleModel ldapRole = (LdapRoleModel) role;
			System.out.println("*** Role DN: "+ldapRole.getDN());
			System.out.println("*** Role Description: "+ldapRole.getDescription());
		}
		manager.close();
	}

	public void listGroupByRole(String roleId) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapGroupRoleOperations manager = new LdapGroupRoleOperations(new LdapGroupManager(rootDN), new LdapRoleManager(rootDN));
		List<GroupModel> groups = manager.listGroupsByRole(roleId);

		for (GroupModel group : groups)
		{
			LdapGroupModel ldapGroup = (LdapGroupModel) group;
			System.out.println("*** Group DN: "+ldapGroup.getDN());
			System.out.println("*** Group Description: "+ldapGroup.getDescription());
		}
		manager.close();
	}

	public void listUsersByRole(String roleId) throws Exception
	{
		LdapManager.initInstance(Client.ldapConfiguration());
		LdapUserRoleOperations manager = new LdapUserRoleOperations(new LdapUserManager(rootDN), new LdapRoleManager(rootDN));
		List<UserModel> users = manager.listUserByRole(roleId);

		for (UserModel user : users)
		{
			LdapUserModel ldapUser = (LdapUserModel) user;
			System.out.println("*** User SN: "+ldapUser.getSN());
			System.out.println("*** User Id: "+ldapUser.getUserId());
			System.out.println("*** User Full Name: "+ldapUser.getFullname());
		}
		manager.close();
	}


	public static void main(String[] args) throws Exception
	{
		//uncomment line below to enable debugging
		BasicConfigurator.configure();

		String userCN1 = "travaglino";
		String userDN1 = "cn=travaglino,dc=vision,dc=eu";
		String userSurname1 = "Travaglino";
		String userPwd1 = "secret";

		String userCN2 = "formisano";
		String userDN2 = "cn=formisano,dc=vision,dc=eu";
		String userSurname2 = "Formisano";
		String userPwd2 = "secret";

		String userCN3 = "fabriani";
		String userDN3 = "cn=fabriani,dc=vision,dc=eu";
		String userSurname3 = "Fabriani";
		String userPwd3 = "secret";

		String userCN4 = "hremployee";
		String userDN4 = "cn=hremployee,dc=vision,dc=eu";
		String userSurname4 = "Employee Surname";
		String userPwd4 = "secret";

		String rootGroupDN = "ou=groups,dc=vision,dc=eu";
		String rootGroupName = "groups";
		String rootGroupDesc = "Root groups";

		String itGroupDN = "cn=itpeople,ou=groups,dc=vision,dc=eu";
		String itGroupName = "itpeople";
		String itGroupDesc = "IT People Group";

		String hrGroupDN = "cn=hrpeople,ou=groups,dc=vision,dc=eu";
		String hrGroupName = "hrpeople";
		String hrGroupDesc = "HR People Group";

		String rolesDN = "ou=roles,"+rootDN;
		String rolesName = "roles";
		String rolesDesc = "Root for the roles";
		String roleDN = "";//"cn=travaglino,dc=vision,dc=eu";

		String roleAdminDN = "cn=admins,ou=roles,dc=vision,dc=eu";
		String roleAdminName = "admins";
		String roleAdminDesc = "Admin Role";

		String roleDeveloperDN = "cn=dev,ou=roles,dc=vision,dc=eu";
		String roleDeveloperName = "dev";
		String roleDeveloperDesc = "Developer Role";

		Client client = new Client();

				//create users
				client.createUser(userCN1,userSurname1,userPwd1);
				client.createUser(userCN2,userSurname2,userPwd2);
				client.createUser(userCN3,userSurname3,userPwd3);
				client.createUser(userCN4,userSurname4,userPwd4);
				
				//create root for all groups
				client.createGroup(rootGroupDN,rootGroupName,rootGroupDesc,"");
				
				//create group for IT people and add some users
				client.createGroup(itGroupDN,itGroupName,itGroupDesc,"");
				client.addUserToGroup(userDN1, itGroupDN);
				client.addUserToGroup(userDN2, itGroupDN);
				client.addUserToGroup(userDN3, itGroupDN);
				
				//create group for HR people and add some users 
				client.createGroup(hrGroupDN,hrGroupName,hrGroupDesc,"");
				client.addUserToGroup(userDN4, hrGroupDN);
				
				
				//create root for all roles
				client.createGroup(rolesDN,rolesName,rolesDesc,"");
				
				//create role for admins and add one user
				client.createRole(roleAdminDN, roleAdminName, roleAdminDesc, userDN3);
				
				//create role for developers and add some users
				client.createRole(roleDeveloperDN, roleDeveloperName, roleDeveloperDesc, userDN1);
				client.addRoleToUser(userDN2, roleDeveloperDN);

		client.createRole("cn=testrole,ou=roles,dc=vision,dc=eu", "testrole", "test role without users", "");
		System.out.println("************** Users List **************");
	//	client.listUsers();
		System.out.println("************** Groups List **************");
		//client.listGroups();
		System.out.println("************** Users in IT Group **************");
		
		
//		client.listUsersInGroup(itGroupDN);
		//client.listGroupByRole(roleAdminDN);
		//client.listGroupByRole(roleDeveloperDN);
		//client.listUsersByRole(roleDeveloperDN);
		//client.listUsersByRole(roleAdminDN);


	}

}
