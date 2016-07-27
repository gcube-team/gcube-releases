package org.gcube.vomanagement.vomsapi;

import java.rmi.RemoteException;

import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;

/**
 * <p>
 * This interface contains operations to manage a VOMS VO.
 * </p>
 * <p>
 * In the following, unless different noted, the user DN and CA must be provided
 * in the format with slashes, e.g.
 * <code>/O=Organization Name/OU=Organization Unit/CN=Name Surname</code>.
 * </p>
 * <p>
 * Unless differently noted, group names are absolute to the VO, e.g.
 * <code>/testVO/testGroup1/testGroup2</code>. In addition, roleName
 * arguments does not requires the <code>"Role="</code> prefix, as well as
 * role returned from {@link VOMSAdmin} methods are already cleaned from the
 * <code>"Role="</code> prefix.
 * </p>
 * <p>
 * Lastly, in all methods returning an array, if the return contains no
 * elements, an empty array is returned, instead of the null value.
 * </p>
 * 
 * @author Paolo Roccetti
 */
public interface VOMSAdmin {

	/**
	 * Add the given user as member of the given group
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param userDN
	 *            the user DN
	 * 
	 * @param userCA
	 *            the user CA
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 * 
	 */
	public void addMember(String groupName, String userDN, String userCA)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Assign the given role in the given group to the given user
	 * 
	 * @param groupName
	 *            the group name
	 * @param roleName
	 *            the role name
	 * @param userDN
	 *            the user DN
	 * @param userCA
	 *            the user CA
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void assignRole(String groupName, String roleName, String userDN,
			String userCA) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Create a new group as a child of a given group
	 * 
	 * @param groupName
	 *            the new group name (e.g. /voName/existingGroupName/newGroupName)
	 * 
	 * @param parentGroup
	 *            the parent group name (e.g. /voName/existingGroupName)
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call 
	 */
	public void createGroup(String parentGroup, String groupName)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Create a new role with the given name
	 * 
	 * @param roleName
	 *            the role name
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createRole(String roleName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Creates a new user given the {@link User} object
	 * 
	 * @param user
	 *            the {@link User} object describing the user to create
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call 
	 */
	public void createUser(User user) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Delete the given group from the VO
	 * 
	 * @param groupName
	 *            the group name
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteGroup(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Delete the given role from the VO
	 * 
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
	 *             
	 */
	public void deleteRole(String roleName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Delete the given user from the VO
	 * 
	 * @param userDN
	 *            the user DN
	 * 
	 * @param userCA
	 *            the user CA
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 */
	public void deleteUser(String userDN, String userCA)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Dismiss the given role in the given group to the given user
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param roleName
	 *            the role name
	 * 
	 * @param userDN
	 *            the user DN
	 * 
	 * @param userCA
	 *            the user CA
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 */
	public void dismissRole(String groupName, String roleName, String userDN,
			String userCA) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Split the path from the root group to the given group. If the
	 * <code>/testVO/testGroup1/testGroup2</code> is given as argument the
	 * method will return the following array of strings:</br>
	 * <code>["/testVO/testGroup1/testGroup2", "/testVO", "/testVO/testGroup1"]</code>.
	 * 
	 * @param groupName
	 *            the group name to split
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return a {@link String}[] contaning the path to the given group
	 */
	public String[] getGroupPath(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	public int getMajorVersionNumber() throws RemoteException,
			VOMSAPIConfigurationException;

	public int getMinorVersionNumber() throws RemoteException,
			VOMSAPIConfigurationException;

	public int getPatchVersionNumber() throws RemoteException,
			VOMSAPIConfigurationException;

	/**
	 * Get information about a user
	 * 
	 * @param userDN
	 *            the user DN
	 * 
	 * @param userCA
	 *            the user CA
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return the {@link User} object containing information about the given
	 *         user
	 */
	public User getUser(String userDN, String userCA) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Get the VO name (with the initial slash)
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return the VO name (with the initial slash)
	 * 
	 */
	public String getVOName() throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Get the list of CA names acknowledged by this VO
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return a {@link String}[] of CA acknowledged by this VO
	 */
	public String[] listCAs() throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * List the set of groups the user is member of
	 * 
	 * @param userDN
	 *            the user DN
	 * @param userCA
	 *            the user CA
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return a {@link String}[] containing groups the user is member of
	 */
	public String[] listGroups(String userDN, String userCA)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * List members of the given group
	 * 
	 * @param groupName
	 *            the group name
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return a {@link User}[] contanining users of the given group
	 */
	public User[] listMembers(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * List roles defined in the VO
	 * 
	 * @return a {@link String}[] containing role defined in the VO
	 */
	public String[] listRoles() throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Get the list of FQAN roles assigned to the given user in the VOMS VO.
	 * 
	 * @param userDN
	 *            the DN of the user
	 * 
	 * @param userCA
	 *            the CA of the user
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return the list of FQAN roles assigned to the user in the VOMS VO. If
	 *         the user does not have any role assigned, an empty array is
	 *         returned.
	 * 
	 */
	public String[] listRoles(String userDN, String userCA)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Get the list of childs of the given group in the VOMS VO.
	 * 
	 * @param groupName
	 *            the parent group
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @return the list of childs of the given group in the VOMS VO. If the
	 *         group does not have any child, an empty array is returned.
	 */
	public String[] listSubGroups(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Get the list of users assigned to the given role in the given group
	 * 
	 * @param groupName
	 *            the group name
	 * 
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
	 *             
	 * 
	 * @return the list of Users assigned to the given role in the given group
	 *         of the VOMS VO. If no users are assigned to the role in the
	 *         group, an empty array is returned.
	 */
	public User[] listUsersWithRole(String groupName, String roleName)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Remove the given user as a member of the given group
	 * 
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param userDN
	 *            the DN of the user
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 * @param userCA
	 *            the CA of the use
	 */
	public void removeMember(String groupName, String userDN, String userCA)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Set the information about a user.
	 * 
	 * @param user
	 *            the {@link User} object containing the new user information to
	 *            set
	 *            
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 *             
	 * 
	 */
	public void setUser(User user) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

}