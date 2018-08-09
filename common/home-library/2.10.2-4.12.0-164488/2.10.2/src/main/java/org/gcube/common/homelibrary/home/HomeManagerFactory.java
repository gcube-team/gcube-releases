/**
 * 
 */
package org.gcube.common.homelibrary.home;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.manager.HomeLibraryManager;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;

/**
 * Manage the HomeManagers.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface HomeManagerFactory {
	
	/**
	 * Initialize the HomeManagerFactory instance.
	 * @param persistenceFolder the persistence folder.
	 * @throws InternalErrorException if an error occurs during the initialization.
	 */
	public void initialize(String persistenceFolder) throws InternalErrorException;
	
	/**
	 * Retrieves an HomeManager for the given GCube's scope.
	 * @param scope the HomeManager's scope.
	 * @return the HomeManager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public HomeManager getHomeManager() throws InternalErrorException;
	
	public HomeManager getHomeManager(String scope) throws InternalErrorException;
	
	/**
	 * List the actuals scopes.
	 * @return the scopes list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public List<String> listScopes() throws InternalErrorException;
	
	
	/**
	 * Check if the specified scope exists.
	 * @param scope the scope to check.
	 * @return <code>true</code> if the scope exists, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public boolean exists(String scope) throws InternalErrorException;
	
	/**
	 * Remove the HomeManager for the specified scope.
	 * @param scope the scope to remove.
	 * @throws InternalErrorException if an error occurs. 
	 */
	public void removeHomeManager() throws InternalErrorException;
	
	/**
	 * Return the HomeLibraryManager instance.
	 * @return the HomeLibraryManager.
	 * @throws InternalErrorException if an error occurs.
	 */
	public HomeLibraryManager getHomeLibraryManager() throws InternalErrorException;
	
	
	/**
	 * List the User scopes. 
	 * @param portalLogin the user portal login.
	 * @return the scope list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public List<String> listUserScopes(String portalLogin) throws InternalErrorException;
	
	
	/**
	 * Returns the scopes list from the infrastructure.
	 * @return the scopes list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public List<String> listInfrastructureScopes() throws InternalErrorException;
	
	/**
	 * Return the users list from the infrastructure for the specified scope.
	 * @param scope the users scope.
	 * @return the users list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public List<String> listInfrastructureScopeUsers(String scope) throws InternalErrorException;
	
	/**
	 * Shutdown the HomeManagerFactory.
	 * @throws InternalErrorException if an error occurs.
	 */
	public void shutdown() throws InternalErrorException;
	
	public UserManager getUserManager() throws InternalErrorException;
	
	public AccessManager getAccessManager() throws InternalErrorException;
	
	public PrivilegeManager getPrivilegeManager() throws InternalErrorException;

}
