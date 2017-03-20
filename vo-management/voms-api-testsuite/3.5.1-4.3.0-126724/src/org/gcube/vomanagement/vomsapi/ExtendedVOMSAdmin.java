package org.gcube.vomanagement.vomsapi;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationProperty;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;

/**
 * This interface provides additional methods to administer a VOMS VO.<br>
 * 
 * <p>
 * In this documentation, the term "Online CA user" indicates a user certified
 * by the gCube Online Certification Authority. Short-term credentials for
 * Online CA users are created dynamically by a MyProxy service configured to
 * act as Online Certification Authority
 * </p>
 * 
 * @author Andrea Turli, Paolo Roccetti
 */
public interface ExtendedVOMSAdmin {

	/**
	 * 
	 * Creates a new VO user, given its Common Name, Distinguished Name,
	 * Certification Authority and email.
	 * 
	 * @param CN
	 *            common name of the user
	 * @param DN
	 *            distinguished name of the user
	 * @param CA
	 *            certification authority of the user
	 * @param mail
	 *            user's email address
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createUser(String CN, String DN, String CA, String mail)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * List VO users
	 * 
	 * @return String[] the VO members
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public User[] listUsers() throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * 
	 * List members of a group
	 * 
	 * @param groupName
	 *            the group name
	 * @return members of the given group
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public User[] listUsers(String groupName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * List members of a group along with their roles in the group.
	 * 
	 * @param groupName
	 *            the group name
	 * @return a {@link HashMap} containing, the users as keys, and, as values,
	 *         a {@link String} array of their roles in the given group.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public HashMap<User, String[]> listUsersAndRoles(String groupName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * List members with a given role along with the groups they have the given
	 * role.
	 * 
	 * @param roleName
	 *            role name
	 * @return a {@link HashMap} containing the users as keys, and, as values, a
	 *         {@link String} array of groups in which they have the given role.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public HashMap<User, String[]> listUsersAndGroups(String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Recursively list all subgroups of the given group, in the FQAN format.
	 * E.g <code>/testVO/testGroup1/testGroup2</code>.</br> The given group
	 * is NOT included in the list.
	 * 
	 * @return a {@link String} array containing the subgroups of the given
	 *         group, in the FQAN format.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listSubGroupsRecursively(String group)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Recursively list all VO subgroups in the FQAN format. E.g
	 * <code>/testVO/testGroup1/testGroup2</code>.</br> The root group is
	 * NOT included in the list.
	 * 
	 * @return a {@link String} array containing the subgroups names in the FQAN
	 *         format.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listSubGroupsRecursively() throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * Recursively list all VO groups in the FQAN format. E.g
	 * <code>/testVO/testGroup1/testGroup2</code>.</br> The root group IS
	 * included in the list.
	 * 
	 * @return a {@link String} array containing the VO groups names in the FQAN
	 *         format.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listGroupsRecursively() throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * Recursively list all groups in the part of the group hierarchy starting
	 * from the given group, in the FQAN format. E.g
	 * <code>/testVO/testGroup1/testGroup2</code>.</br> The given group IS
	 * included in the list.
	 * 
	 * @return a {@link String} array containing the VO groups names in the FQAN
	 *         format.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listGroupsRecursively(String group) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * List the roles of a given OnlineCA user in the VO, grouped by VO groups.
	 * 
	 * @param userDN
	 *            the Distinguished Name of the user
	 * @param userCA
	 *            the Distinguished Name of the user's CA
	 * @return a {@link HashMap} containing the groups as keys and, as values, a
	 *         {@link String} array of roles held by the user in each group.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public HashMap<String, String[]> listGroupedRoles(String userDN,
			String userCA) throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * List the roles held by the given user in the given group.
	 * 
	 * @param groupName
	 *            the group name
	 * @param userDN
	 *            the Distinguished Name of the user
	 * @param userCA
	 *            the Distinguished Name of the user's CA
	 * @return a {@link String} array of the roles held by the given user in the
	 *         given group.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listRoles(String groupName, String userDN, String userCA)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Creates a new VO user that is certified by the Online CA. The
	 * Distinguished Name (DN) of the CA, as well as the prefix of the DN of the
	 * used are kept from the VOMS-API configuration, see the
	 * {@link VOMSAPIConfiguration} class.</br> The username supplied will be
	 * chained to the {@link VOMSAPIConfigurationProperty}.DN_PREFIX to create
	 * the user Distinguished Name.
	 * 
	 * @param userName
	 *            the userName of the OnlineCA user to create
	 * @param mail
	 *            email of the user to create
	 * 
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createOnlineCAUser(String userName, String mail)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Get an OnlineCA user from its username
	 * 
	 * @param userName
	 *            the username of the OnlineCA user to get
	 * @return the {@link User} object containing the user fields in VOMS.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public User getOnlineCAUser(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * Deletes an OnlineCA user from the VO.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user to delete
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteOnlineCAUser(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * List roles held by the given OnlineCA user in all VO groups.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @return a {@link HashMap} containing the groups as keys, and, as values,
	 *         a {@link String} array of roles held by the user in each group.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public HashMap<String, String[]> listOnlineCARoles(String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * List roles held by the given OnlineCA user in the given group
	 * 
	 * @param groupName
	 *            the group name
	 * @param userName
	 *            the username of the OnlineCA user
	 * @return the {@link String} array of roles the user held in the given
	 *         group
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listOnlineCARoles(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Assign the given role to the given OnlineCA user in the given group
	 * 
	 * @param groupName
	 *            the group name
	 * @param roleName
	 *            the role name
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void assignOnlineCARole(String groupName, String roleName,
			String userName) throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * 
	 * Dismiss the given role for the given OnlineCA user in the given group
	 * 
	 * @param groupName
	 *            the group name
	 * @param roleName
	 *            the role name
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void dismissOnlineCARole(String groupName, String roleName,
			String userName) throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * List groups the OnlineCA user is member of.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @return a {@link String} array of groups the user is member of
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public String[] listOnlineCAGroups(String userName) throws VOMSException,
			RemoteException, VOMSAPIConfigurationException;

	/**
	 * 
	 * Add the OnlineCA user as member of the given group
	 * 
	 * @param groupName
	 *            the group name
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void addOnlineCAMember(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Remove the OnlineCA user as member of the given group
	 * 
	 * @param groupName
	 *            the group name
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void removeOnlineCAMember(String groupName, String userName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * This method gives administrative privileges over the given group to the
	 * given role
	 * 
	 * @param groupName
	 *            the group name
	 * @param roleName
	 *            the role name
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void configureAsAdmin(String groupName, String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * This method revokes administrative privileges over the given group to the
	 * given role
	 * 
	 * @param groupName
	 *            the group name
	 * @param roleName
	 *            the role name
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void revokeAsAdmin(String groupName, String roleName)
			throws VOMSException, RemoteException,
			VOMSAPIConfigurationException;

}
