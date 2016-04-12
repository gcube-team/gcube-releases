/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;


/**
 * @author gioia
 *
 */

public interface WorkspaceSharedFolder extends WorkspaceFolder{

	/**
	 * Get Administrators
	 * @return Administrators
	 * @throws InternalErrorException
	 */
	List<String> getAdministrators() throws InternalErrorException;
	
	
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
	List<String> getUsers() throws InternalErrorException;
	
	/**
	 * @param user
	 * @throws InsufficientPrivilegesException
	 * @throws InternalErrorException
	 */
	void addUser(String user) throws InsufficientPrivilegesException,
	InternalErrorException;
	
	/**
	 * @return a new {@link WorkspaceFolder}
	 * @throws InternalErrorException
	 */
	WorkspaceFolder unShare() throws InternalErrorException;
	
	
	/**
	 * Unshare a single user
	 * 
	 * @param user
	 * @return a new {@link WorkspaceFolder}
	 * @throws InternalErrorException
	 */
	WorkspaceFolder unShare(String user) throws InternalErrorException;
	
	/**
	 * @param user
	 * @return the shared folder name set by the user, null if the user doen't exist.
	 * @throws InternalErrorException
	 */
	String getName(String user) throws InternalErrorException;

	/**
	 * Set ACLs on shared folder
	 * @param users
	 * @param privilege
	 * @throws InternalErrorException
	 */
	void setACL(List<String> users, ACLType privilege)
			throws InternalErrorException;

	/**
	 * @return the displayName for the VRE folder
	 */
	String getDisplayName();

	/**
	 * @return true if the shared folder is a VRE folder
	 */
	boolean isVreFolder();

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
	List<String> getMembers() throws InternalErrorException;

	/**
	 * Get groups in a shared folder
	 * @return
	 * @throws InternalErrorException
	 */
	List<String> getGroups() throws InternalErrorException;

	
	public WorkspaceSharedFolder share(List<String> users) throws InsufficientPrivilegesException,
	WrongDestinationException, InternalErrorException;

	
	
}
