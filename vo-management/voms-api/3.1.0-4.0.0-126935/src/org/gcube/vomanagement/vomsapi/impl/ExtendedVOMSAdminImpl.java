package org.gcube.vomanagement.vomsapi.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
import org.gcube.vomanagement.vomsapi.VOMSACL;
import org.gcube.vomanagement.vomsapi.VOMSAdmin;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;

/**
 * This class implements the {@link ExtendedVOMSAdmin} interface. Method
 * implementation rely on the {@link VOMSAdmin} interface for interactions with
 * the VOMS service.
 * 
 * @author Paolo Roccetti
 */
class ExtendedVOMSAdminImpl extends VOMSAPIStub implements ExtendedVOMSAdmin {

	static Logger logger = Logger.getLogger(ExtendedVOMSAdminImpl.class
			.getName());

	private VOMSAdmin vomsAdmin;

	private VOMSACL vomsACL;

	/**
	 * Creates a new {@link ExtendedVOMSAdminImpl} object linked to the given
	 * {@link VOMSAPIFactory}.
	 * 
	 * @param configuration -
	 *            the {@link VOMSAPIConfiguration} object used to find
	 *            configuration properties.
	 * 
	 * @param VOMSAdmin -
	 *            the {@link VOMSAdmin} interface for VOMS Administration
	 *            service
	 * 
	 * @param vomsACL -
	 *            the {@link VOMSACL} interface for VOMS ACL management
	 */
	ExtendedVOMSAdminImpl(VOMSAdmin vomsAdmin, VOMSACL vomsACL,
			VOMSAPIFactory factory) {
		super(factory);
		this.vomsAdmin = vomsAdmin;
		this.vomsACL = vomsACL;

	}

	void configureVOMSAPIStubForCall() {
		// Stubs are configured in vomsAdmin and vomsACL directly
	}

	public void addOnlineCAMember(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {
		this.vomsAdmin.addMember(groupName, getDN(userName), getCA());

	}

	public void assignOnlineCARole(String groupName, String roleName,
			String userName) throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		this.vomsAdmin
				.assignRole(groupName, roleName, getDN(userName), getCA());
	}

	public void createOnlineCAUser(String userName, String mail)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		this.createUser(userName, getDN(userName), getCA(), mail);
	}

	public void createUser(String CN, String DN, String CA, String mail)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		User user = new User();
		user.setCA(CA);
		user.setCN(CN);
		user.setDN(DN);
		user.setMail(mail);

		this.vomsAdmin.createUser(user);
	}

	public void deleteOnlineCAUser(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {

		this.vomsAdmin.deleteUser(getDN(userName), getCA());
	}

	public void dismissOnlineCARole(String groupName, String roleName,
			String userName) throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		this.vomsAdmin.dismissRole(groupName, roleName, getDN(userName),
			getCA());
	}

	public User getOnlineCAUser(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {

		return this.vomsAdmin.getUser(getDN(userName), getCA());
	}

	public HashMap<String, String[]> listGroupedRoles(String DN, String CA)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		HashMap<String, ArrayList<String>> groupedRoles = new HashMap<String, ArrayList<String>>();

		String[] fqanRoles = this.vomsAdmin.listRoles(DN, CA);

		// iterate over roles
		for (String fqanRole : fqanRoles) {

			// get group and role from the FQAN role
			String groupName = getGroupFromFQAN(fqanRole);
			String roleName = getRoleFromFQAN(fqanRole);

			// if the group already exist in the map
			if (groupedRoles.containsKey(groupName))
				// add the new role in the group
				groupedRoles.get(groupName).add(roleName);
			else {
				// create a new arrayList for the group
				ArrayList<String> values = new ArrayList<String>();

				// add the role to the list
				values.add(roleName);

				// add the list to the map
				groupedRoles.put(groupName, values);
			}
		}

		// convert ArrayLists to String[]
		HashMap<String, String[]> groupedRolesArray = new HashMap<String, String[]>();
		for (String group : groupedRoles.keySet()) {
			ArrayList<String> rolesInGroup = groupedRoles.get(group);
			groupedRolesArray.put(group, rolesInGroup
					.toArray(new String[rolesInGroup.size()]));
		}

		return groupedRolesArray;
	}

	public String[] listGroupsRecursively() throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {
		return listGroupsRecursively(this.vomsAdmin.getVOName());
	}

	public String[] listSubGroupsRecursively() throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {
		return listSubGroupsRecursively(this.vomsAdmin.getVOName());
	}

	public String[] listGroupsRecursively(String groupName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		// list subgroups recursively
		String[] subGroups = listSubGroupsRecursively(groupName);

		// add the current group
		String[] groups = new String[subGroups.length + 1];

		// copy subgroups elements
		groups[0] = groupName;
		for (int i = 0; i < subGroups.length; i++) {
			groups[i + 1] = subGroups[i];
		}

		return groups;
	}

	public String[] listSubGroupsRecursively(String groupName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		// create the ArrayList for groups
		ArrayList<String> groups = new ArrayList<String>();

		// get childs
		String[] subGroups = this.vomsAdmin.listSubGroups(groupName);

		// recursively call this method on childs
		for (String subGroup : subGroups) {

			// add the child group
			groups.add(subGroup);

			// add the childs of the child
			groups.addAll(Arrays.asList(listSubGroupsRecursively(subGroup)));
		}

		return groups.toArray(new String[groups.size()]);
	}

	public String[] listOnlineCAGroups(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {
		return this.vomsAdmin.listGroups(getDN(userName), getCA());
	}

	public HashMap<String, String[]> listOnlineCARoles(String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {
		return listGroupedRoles(getDN(userName), getCA());
	}

	public String[] listOnlineCARoles(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {
		return this.listRoles(groupName, getDN(userName), getCA());
	}

	public String[] listRoles(String groupName, String DN, String CA)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		// get user roles
		String[] fqanRoles = this.vomsAdmin.listRoles(DN, CA);

		ArrayList<String> rolesInGroup = new ArrayList<String>();

		// iterate over roles selecting the ones belonging to the given group
		for (String fqanRole : fqanRoles) {
			if (getGroupFromFQAN(fqanRole).equals(groupName)) {
				rolesInGroup.add(getRoleFromFQAN(fqanRole));
			}
		}

		// convert ArrayList to String[]
		return rolesInGroup.toArray(new String[rolesInGroup.size()]);

	}

	public User[] listUsers() throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		return this.vomsAdmin.listMembers(this.vomsAdmin.getVOName());

	}

	public User[] listUsers(String groupName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException {

		return this.vomsAdmin.listMembers(groupName);
	}

	public HashMap<User, String[]> listUsersAndGroups(String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		HashMap<User, ArrayList<String>> usersAndGroups = new HashMap<User, ArrayList<String>>();
		User[] users = null;

		String[] groups = this.listGroupsRecursively();

		for (int i = 0; i < groups.length; i++) {
			users = this.vomsAdmin.listUsersWithRole(groups[i], roleName);
			for (int j = 0; j < users.length; j++) {
				if (usersAndGroups.containsKey(users[j])) {
					ArrayList<String> g = usersAndGroups.get(users[j]);
					g.add(groups[i]);
				} else {
					ArrayList<String> inGroups = new ArrayList<String>();
					inGroups.add(groups[i]);
					usersAndGroups.put(users[j], inGroups);
				}
			}
		}

		// convert ArrayLists to String[]
		HashMap<User, String[]> usersAndGroupsArray = new HashMap<User, String[]>();
		for (User user : usersAndGroups.keySet()) {
			ArrayList<String> rolesInGroup = usersAndGroups.get(user);
			usersAndGroupsArray.put(user, rolesInGroup
					.toArray(new String[rolesInGroup.size()]));
		}

		return usersAndGroupsArray;
	}

	public HashMap<User, String[]> listUsersAndRoles(String groupName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		HashMap<User, ArrayList<String>> usersAndRoles = new HashMap<User, ArrayList<String>>();
		User[] users = null;
		String[] roles = this.vomsAdmin.listRoles();
		for (int i = 0; i < roles.length; i++) {
			users = this.vomsAdmin.listUsersWithRole(groupName, roles[i]);
			for (int j = 0; j < users.length; j++) {
				if (usersAndRoles.containsKey(users[j])) {
					ArrayList<String> g = usersAndRoles.get(users[j]);
					g.add(roles[i]);
				} else {
					ArrayList<String> inGroups = new ArrayList<String>();
					inGroups.add(roles[i]);
					usersAndRoles.put(users[j], inGroups);
				}
			}
		}

		// convert ArrayLists to String[]
		HashMap<User, String[]> usersAndRolesArray = new HashMap<User, String[]>();
		for (User user : usersAndRoles.keySet()) {
			ArrayList<String> rolesInGroup = usersAndRoles.get(user);
			usersAndRolesArray.put(user, rolesInGroup
					.toArray(new String[rolesInGroup.size()]));
		}

		return usersAndRolesArray;
	}

	public void removeOnlineCAMember(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {
		this.vomsAdmin.removeMember(groupName, getDN(userName), getCA());
	}

	public void configureAsAdmin(String groupName, String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		String rootGroup = this.vomsAdmin.getVOName();
		String voContext = rootGroup + "/" + roleName;
		String groupContext = groupName + "/" + roleName;

		// Define ACL for root group
		ACLEntry rootAdminEntry = new ACLEntry();
		rootAdminEntry.setAdminIssuer("/O=VOMS/O=System/CN=VOMS Role");
		rootAdminEntry.setAdminSubject(voContext);
		rootAdminEntry.setVomsPermissionBits(3);
		this.vomsACL.addACLEntry(rootGroup, rootAdminEntry, false);

		// Define ACL for groupname specified
		ACLEntry voAdminEntry = new ACLEntry();
		voAdminEntry.setAdminIssuer("/O=VOMS/O=System/CN=VOMS Role");
		voAdminEntry.setAdminSubject(groupContext);
		voAdminEntry.setVomsPermissionBits(15);
		this.vomsACL.addACLEntry(groupName, voAdminEntry, false);
	}

	public void revokeAsAdmin(String groupName, String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException {

		String voName = this.vomsAdmin.getVOName();
		String voContext = voName + "/" + roleName;
		String groupContext = groupName + "/" + roleName;

		// Define ACL for root group
		ACLEntry rootAdminEntry = new ACLEntry();
		rootAdminEntry.setAdminIssuer("/O=VOMS/O=System/CN=VOMS Role");
		rootAdminEntry.setAdminSubject(voContext);
		rootAdminEntry.setVomsPermissionBits(3);

		this.vomsACL.removeACLEntry(voName, rootAdminEntry, false);

		// Define ACL for groupname specified
		ACLEntry voAdminEntry = new ACLEntry();
		voAdminEntry.setAdminIssuer("/O=VOMS/O=System/CN=VOMS Role");
		voAdminEntry.setAdminSubject(groupContext);
		voAdminEntry.setVomsPermissionBits(15);
		this.vomsACL.removeACLEntry(groupName, voAdminEntry, false);
	}

}
