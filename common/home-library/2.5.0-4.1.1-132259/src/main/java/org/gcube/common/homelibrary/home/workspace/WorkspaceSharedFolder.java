/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.util.List;

import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;


/**
 * @author gioia
 *
 */

public interface WorkspaceSharedFolder extends WorkspaceFolder{

	/**
	 * Get displayName
	 * @return diplay name
	 * @throws InternalErrorException
	 */
	String getDisplayName() throws InternalErrorException;

	/**
	 * Get Administrators
	 * @return Administrators
	 * @throws InternalErrorException
	 */
	public List<String> getAdministrators() throws InternalErrorException;


	/**
	 * Add administrators to a shared folder
	 * @throws InsufficientPrivilegesException
	 * @throws InternalErrorException
	 */
	boolean addAdmin(String username) throws InsufficientPrivilegesException,
	InternalErrorException;

	/**
	 * Set administrators
	 * @throws InsufficientPrivilegesException
	 * @throws InternalErrorException
	 */
	boolean setAdmins(List<String> logins) throws InsufficientPrivilegesException,
	InternalErrorException;

	/**
	 * @return the list of users. 
	 * @throws InternalErrorException
	 */
	public List<String> getUsers() throws InternalErrorException;

	/**
	 * @param user
	 * @throws InsufficientPrivilegesException
	 * @throws InternalErrorException
	 */
	public void addUser(String user) throws InsufficientPrivilegesException,
	InternalErrorException;

	/**
	 * @return a new {@link WorkspaceFolder}
	 * @throws InternalErrorException
	 */
	public WorkspaceFolder unShare() throws InternalErrorException;


	/**
	 * Unshare a single user
	 * 
	 * @param user
	 * @return a new {@link WorkspaceFolder}
	 * @throws InternalErrorException
	 */
	public WorkspaceFolder unShare(String user) throws InternalErrorException;

	/**
	 * @param user
	 * @return the shared folder name set by the user, null if the user doen't exist.
	 * @throws InternalErrorException
	 */
	public String getName(String user) throws InternalErrorException;

	/**
	 * Set ACLs on shared folder
	 * @param users
	 * @param privilege
	 * @throws InternalErrorException
	 */
	void setACL(List<String> users, ACLType privilege)
			throws InternalErrorException;

	/**
	 * @return true if the shared folder is a VRE folder
	 */
	boolean isVreFolder();

	void setVREFolder(boolean isVREFolder) throws InternalErrorException;
//	void setDisplayName(String displayName) throws InternalErrorException;

	/**
	 * Get the privilege set on a WorkspaceSharedFolder
	 * @return
	 * @throws InternalErrorException
	 */
	ACLType getPrivilege() throws InternalErrorException;

	/**
	 * Delete privileges for a list of users
	 * @param users
	 * @throws InternalErrorException
	 */
	void deleteACL(List<String> users) throws InternalErrorException;

	/**
	 * Get members: users and groups of a shared folder
	 * @return
	 * @throws InternalErrorException
	 */
	public List<String> getMembers() throws InternalErrorException;

	/**
	 * Get groups in a shared folder
	 * @return
	 * @throws InternalErrorException
	 */
	@Deprecated
	public List<String> getGroups() throws InternalErrorException;


	public WorkspaceSharedFolder share(List<String> users) throws InsufficientPrivilegesException,
	WrongDestinationException, InternalErrorException;




}
