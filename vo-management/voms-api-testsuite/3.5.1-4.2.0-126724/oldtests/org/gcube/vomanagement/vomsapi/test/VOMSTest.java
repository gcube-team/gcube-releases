package org.gcube.vomanagement.vomsapi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.vomanagement.vomsAdmin.impl.VOMSAdminImpl;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSException;

/**
 * Test class to check interaction with VOMS server
 * 
 * @author Andrea Turli
 * 
 */
public class VOMSTest {

    /**
     * 
     * @param args arguments
     * @throws Exception generic exception
     */
    public static void main(String[] args) throws Exception {

	//RootCategory.getRoot().setLevel(Level.INFO);

	/*
	 * if(args.length != 7) { throw new Exception("Please check
	 * parameters.\n * UserName = arg[0], like testUser; Pwd = arg[1], like
	 * test;" + "mail = arg[2], like testUser@mail.it;" + "groupName =
	 * arg[3], like /gcbe/devsec/test;" + "parentName = arg[4], like
	 * /gcbe/devsec/; " + "roleName = arg[5], like testNewRole;" + "dlName =
	 * arg[6], like dlName;"); }
	 */
	String userName = "test";
	String mail = "test@eng.it";
	String groupName = "/grifin/testMio";
	String groupName1 = "/grifin/test1";
	String parentName = "grifin";
	String roleName = "Role=testNewRole";


	String userCA = "/O=Engineering/OU=gridUnit/CN=GriFin CA";
	String DN = userName;

	try {

	    /*
	    VOMSAdminGT4 vomsAdmin = new VOMSAdminGT4();
	    ExtendedGSSCredential credentials = null;
	    try {
		credentials = ProxyUtil.loadProxyCredentials(vomsAdmin.getClientProxy());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    } catch (GSSException e1) {
		e1.printStackTrace();
	    }	    

	    // credentials
	    ((Stub) vomsAdmin)._setProperty(GSIConstants.GSI_CREDENTIALS, credentials);

	    // Authentication method
	    ((Stub) vomsAdmin)._setProperty(org.globus.wsrf.impl.security.authentication.Constants.GSI_TRANSPORT,
		    org.globus.wsrf.impl.security.authentication.Constants.ENCRYPTION);
	    // delegation
	    ((Stub) vomsAdmin)._setProperty(GSIConstants.GSI_MODE, GSIConstants.GSI_MODE_NO_DELEG);
	    // set Context lifetime
	    ((Stub) vomsAdmin)._setProperty(org.globus.wsrf.impl.security.authentication.Constants.CONTEXT_LIFETIME, 300);
	    */ 

	    /*
	    ExtendedGSSCredential credentials = null;
	    try {
		credentials = ProxyUtil.loadProxyCredentials("/home/turli/certs/proxy");
	    } catch (IOException e1) {
		e1.printStackTrace();
	    } catch (GSSException e1) {
		e1.printStackTrace();
	    }
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl(credentials, "/home/turli/vomsAPI.properties");    
	    */
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl();
	    
	    System.out.println("VO: " + vomsAdmin.getPortType().getVOName() + "\n");

	    System.out.println("Roles: ");
	    String[] roles = vomsAdmin.getPortType().listRoles();
	    for (int i = 0; i < roles.length; i++) {
		System.out.println("\t" + roles[i]);
	    }
	    System.out.println();
	    
	    String[] groups = null;

	    System.out.println("List groups:");
	    groups = vomsAdmin.getExtendedPortType().listGroups();
	    for (int i = 0; i < groups.length; i++) {
		System.out.println("\t" + groups[i]);
	    }
	    System.out.println();


	    System.out.println("Create group with groupName: " + groupName);
	    vomsAdmin.getPortType().createGroup(parentName, groupName);
	    System.out.println();

	    System.out.println("List groups:");
	    groups = vomsAdmin.getExtendedPortType().listGroups();
	    for (int i = 0; i < groups.length; i++) {
		System.out.println("\t" + groups[i]);
	    }
	    System.out.println(); 

	    System.out.println("List roles:");
	    roles = vomsAdmin.getPortType().listRoles();
	    for (int i = 0; i < roles.length; i++) {
		System.out.println("\t" + roles[i]);
	    }
	    System.out.println();

	    System.out.println("Create role: " + roleName);
	    vomsAdmin.getPortType().createRole(roleName);
	    System.out.println();

	    System.out.println("List roles:");
	    roles = vomsAdmin.getPortType().listRoles();
	    for (int i = 0; i < roles.length; i++) {
		System.out.println("\t" + roles[i]);
	    }
	    System.out.println();

	    System.out.println("Create OnLineCA user with username: " + userName + ", mail: " + mail);
	    vomsAdmin.getExtendedPortType().createOnlineCAUser(userName, mail);
	    System.out.println("");


	    System.out.println("Get OnLineCA user with username: " + userName);
	    User u = vomsAdmin.getExtendedPortType().getOnlineCAUser(userName);
	    System.out.print("User: ");
	    System.out.println("\tCN: " + u.getCN());
	    System.out.println("\tDN: " + u.getDN());
	    System.out.println("\tCA: " + u.getCA());
	    System.out.println("\tMail: " + u.getMail());
	    System.out.println("");

	    System.out.println("List OnLineCA groups for username: "+ userName);
	    String[] uGroups = vomsAdmin.getExtendedPortType().listOnlineCAGroups(userName);
	    if (uGroups != null) {
		for (int i = 0; i < uGroups.length; i++) {
		    System.out.println("\t" + uGroups[i]);
		}
	    }
	    System.out.println("");

	    System.out.println("Add Membership: " + userName + " to " + groupName);
	    vomsAdmin.getExtendedPortType().addOnlineCAMember(groupName, userName);
	    System.out.println("");

	    System.out.println("List users of the group " + groupName);
	    User[] usersG = vomsAdmin.getExtendedPortType().listUsers(groupName);
	    if (usersG != null) {
		for (int i = 0; i < usersG.length; i++) {
		    System.out.println("\tDN " + usersG[i].getDN());
		    System.out.println("\tCA " + usersG[i].getCA());
		    System.out.println("\tCA URI " + usersG[i].getCertUri());
		    System.out.println("\tCN " + usersG[i].getCN());
		    System.out.println("\tMail " + usersG[i].getMail());
		}
	    }
	    System.out.println("");

	    System.out.println("List users and roles for group " + groupName);
	    HashMap<User, ArrayList<String>> usersAndRoles = vomsAdmin.getExtendedPortType().listUsersAndRoles(groupName);
	    if (usersAndRoles != null && !usersAndRoles.isEmpty()) {
		Iterator<User> iterator = usersAndRoles.keySet().iterator();
		while (iterator.hasNext()) {
		    User key = (User) iterator.next();
		    System.out.println("\tUser fetched: ");
		    System.out.println("\t\tCN: " + key.getCN());
		    System.out.println("\t\tDN: " + key.getDN());
		    System.out.println("\t\tCA: " + key.getCA());
		    System.out.println("\t\tMail: " + key.getMail());
		    ArrayList<String> values = usersAndRoles.get(key);
		    for (Iterator iter = values.iterator(); iter.hasNext();) {
			System.out.println("\tgroup: " + iter.next());
		    }
		}
	    } else 
		System.out.println("\tThere are no user with roles in this groupName: " + groupName);
	    System.out.println("");	    

	    System.out.println("Assign OnLineCA role with group: " + groupName + ", role: " + roleName + ", username: "+ userName);
	    vomsAdmin.getExtendedPortType().assignOnlineCARole(groupName, roleName, userName);
	    System.out.println("");

	    System.out.println("List OnLineCA roles for groupName: " + groupName + ", username: " + userName);
	    ArrayList<String> result = vomsAdmin.getExtendedPortType().listOnlineCARoles(groupName,  userName);
	    for (Iterator iter = result.iterator(); iter.hasNext();) {
		System.out.println("\t" + (String) iter.next());
	    }
	    System.out.println("");

	    System.out.println("List users with role " + roleName + " in groupName " + groupName);
	    User[] usersA = vomsAdmin.getPortType().listUsersWithRole(groupName, roleName);
	    if (usersA != null) {
		for (int i = 0; i < usersA.length; i++) {
		    System.out.println("\tUser fetched: ");
		    System.out.println("\t\tCN: " + usersA[i].getCN());
		    System.out.println("\t\tDN: " + usersA[i].getDN());
		    System.out.println("\t\tCA: " + usersA[i].getCA());
		    System.out.println("\t\tMail: " + usersA[i].getMail());
		}
	    }
	    System.out.println("");

	    System.out.println("List users and groups for role " + roleName);
	    HashMap<User, ArrayList<String>> usersAndGroups = vomsAdmin.getExtendedPortType().listUsersAndGroups(roleName);
	    Iterator<User> it = usersAndGroups.keySet().iterator();
	    while (it.hasNext()) {
		User key = (User) it.next();
		System.out.println("\tUser fetched: ");
		System.out.println("\t\tCN: " + key.getCN());
		System.out.println("\t\tDN: " + key.getDN());
		System.out.println("\t\tCA: " + key.getCA());
		System.out.println("\t\tMail: " + key.getMail());
		ArrayList<String> values = usersAndGroups.get(key);
		for (Iterator iter = values.iterator(); iter.hasNext();) {
		    System.out.println("\tgroup: " + iter.next());
		}
	    }
	    System.out.println("");

	    System.out.println("List users and roles for group " + groupName);
	    usersAndRoles = vomsAdmin.getExtendedPortType().listUsersAndRoles(groupName);
	    if (usersAndRoles != null) {
		Iterator<User> iterator = usersAndRoles.keySet().iterator();
		while (iterator.hasNext()) {
		    User key = (User) iterator.next();
		    System.out.println("\tUser fetched: ");
		    System.out.println("\t\tCN: " + key.getCN());
		    System.out.println("\t\tDN: " + key.getDN());
		    System.out.println("\t\tCA: " + key.getCA());
		    System.out.println("\t\tMail: " + key.getMail());
		    ArrayList<String> values = usersAndRoles.get(key);
		    for (Iterator iter = values.iterator(); iter.hasNext();) {
			System.out.println("\tgroup: " + iter.next());
		    }
		}
	    }
	    System.out.println("");    

	    System.out.println("Dismiss OnLineCA role for groupName " + groupName + ", roleName: " + roleName + ", username: " + userName);
	    vomsAdmin.getExtendedPortType().dismissOnlineCARole(groupName, roleName, userName);
	    System.out.println("");

	    System.out.println("Remove OnLineCA member " + userName + " from groupName: " + groupName);
	    vomsAdmin.getExtendedPortType().removeOnlineCAMember(groupName, userName);
	    System.out.println("");

	    System.out.println("Delete OnLineCA user " + userName);
	    vomsAdmin.getExtendedPortType().deleteOnlineCAUser(userName);
	    System.out.print("");

	    System.out.println("Delete role named: " + roleName);
	    vomsAdmin.getPortType().deleteRole(roleName);
	    System.out.println("");
	    System.out.println("Delete group named " + groupName);
	    vomsAdmin.getPortType().deleteGroup(groupName);
	    System.out.println("");

	    System.out.println("**********************************************************************");
	    
	    System.out.println("Create a user with DN: " + DN + ", CA: " + userCA + ", mail: " + mail);
	    vomsAdmin.getExtendedPortType().createUser(userName, DN, userCA, mail);
	    System.out.println();
	    System.out.println("Create a group with parentName: " + parentName + ", groupName: " + groupName1);
	    vomsAdmin.getPortType().createGroup(parentName, groupName1);
	    System.out.println();
	    System.out.println("Create a role with name: " + roleName);
	    vomsAdmin.getPortType().createRole(roleName);
	    System.out.println();
	    System.out.println("Get a user with DN: " + DN + ", CA: " + userCA);
	    u = vomsAdmin.getPortType().getUser(DN, userCA); 
	    System.out.println("\tUser fetched: ");
	    System.out.println("\t\tCN: " + u.getCN());
	    System.out.println("\t\tDN: " + u.getDN());
	    System.out.println("\t\tCA: " + u.getCA());
	    System.out.println("\t\tMail: " + u.getMail());
	    System.out.println("");

	    System.out.println("Add a member with DN: " + DN + ", CA: " + userCA + " to group " + groupName1);
	    vomsAdmin.getPortType().addMember(groupName1, DN, userCA); 
	    System.out.println();
	    System.out.println("Assign a role named " + roleName + " to a user with DN: " + DN + " ,and  CA: " + userCA + " in group " + groupName1);
	    vomsAdmin.getPortType().assignRole(groupName1, roleName, DN, userCA);
	    System.out.println();

	    System.out.println("List roles for a user with DN: " + DN + " ,and  CA: " + userCA);
	    HashMap<String, ArrayList<String>> uRoles = vomsAdmin.getExtendedPortType().listGroupedRoles(DN, userCA); 

	    if(uRoles.isEmpty())
		System.out.println("\tList roles for a user with DN: " + DN + " ,and  CA: " + userCA + " is EMPTY!");
	    Iterator<String> iteratore = uRoles.keySet().iterator(); 
	    while (iteratore.hasNext()) {
		String key = (String) iteratore.next();
		System.out.println("\tgroup: " + key); 
		ArrayList<String> values =  uRoles.get(key); 
		for (Iterator iter = values.iterator(); iter.hasNext();) { 
		    System.out.println("\trole: " + iter.next()); 
		} 
	    }
	    System.out.println();
	    System.out.println("List roles for a user with DN: " + DN + " ,and  CA: " + userCA + " in group " + groupName);
	    ArrayList<String> res = vomsAdmin.getExtendedPortType().listRoles(groupName, DN, userCA); 
	    if(res!= null) { 
		for (Iterator iter = res.iterator(); iter.hasNext();) { 
		    System.out.println("\trole: " + iter.next()); 
		} 
	    }
	    System.out.println("");

	    System.out.println("List groups for user with DN:  " + DN + ", CA: " + userCA); 
	    groups = vomsAdmin.getPortType().listGroups(DN, userCA); 
	    for (int i = 0; i < groups.length; i++) { 
		System.out.println("\t" + groups[i]);
	    }
	    System.out.println("");


	    System.out.println("Dismiss role for user with DN: " + DN + " and CA: " + userCA);
	    vomsAdmin.getPortType().dismissRole(groupName1, roleName, DN, userCA);
	    System.out.println("");


	    System.out.println("Remove a member with DN " + DN + " and CA " + userCA + " from group: " + groupName1);
	    vomsAdmin.getPortType().removeMember(groupName1, DN, userCA); 
	    System.out.println("");	    

	    System.out.println("Delete user with DN " + DN + " and CA " + userCA);
	    vomsAdmin.getPortType().deleteUser(DN, userCA);	    
	    System.out.println("");

	    System.out.println("Delete role named: " + roleName);		    
	    vomsAdmin.getPortType().deleteRole(roleName);		    
	    System.out.println("");

	    System.out.println("Delete group named: " + groupName1);		    
	    vomsAdmin.getPortType().deleteGroup(groupName1);		    
	    System.out.println("");

	    System.out.println("Finished");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
