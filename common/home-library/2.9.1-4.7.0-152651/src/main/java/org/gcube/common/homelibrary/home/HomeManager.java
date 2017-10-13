/**
 * 
 */
package org.gcube.common.homelibrary.home;

import java.util.List;

import org.gcube.common.homelibary.model.util.MemoryCache;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;

/**
 * Homes manager for a single scope. Manage users and user's home presents in a scope.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface HomeManager {
	
	/**
	 * Return this HomeManager factory.
	 * @return the factory.
	 */
	public HomeManagerFactory getHomeManagerFactory();
	
	
	/**
	 * Retrieves all users managed by this manager. 
	 * @return all users.
	 */
	public List<User> getUsers();

	/**
	 * Get an user, if the user is not found a new one is created.
	 * @param portalLogin the user's portal login.
	 * @return the user.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public User getUser(String portalLogin) throws InternalErrorException;
	
	/**
	 * Check if the specified user exists.
	 * @param portalLogin the user's portal login.
	 * @return <code>true</code> if the user has been found, <code>false</code> otherwise.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public boolean existUser(String portalLogin) throws InternalErrorException;
	
	/**
	 * Create a new user.
	 * @param portalLogin the user's portal login.
	 * @return the new user.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public User createUser(String portalLogin) throws InternalErrorException;
	
	/**
	 * Retrieve the user's home.
	 * @param user the owner's home.
	 * @return the home.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws HomeNotFoundException if no home is found for the given user.
	 */
	@Deprecated
	public Home getHome(User user) throws InternalErrorException, HomeNotFoundException;
	
	
	/**
	 * Retrieve the user's home.
	 * @param portalLogin the user portal login.
	 * @return the home.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws HomeNotFoundException if no home is found for the given user.
	 * @throws UserNotFoundException if no user is found for the specified portalLogin.
	 */
	@Deprecated
	public Home getHome(String portalLogin) throws InternalErrorException, HomeNotFoundException, UserNotFoundException;
	
	
	/**
	 * Remove the specified user.
	 * @param user the user to remove.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void removeUser(User user) throws InternalErrorException;


	/**
	 * Get cache for user homes
	 * @return cache for user homes
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public MemoryCache<String, Home> getCache() throws InternalErrorException;


	/**
	 * Get user home using token
	 * @return the user home
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 * @throws UserNotFoundException 
	 */
	public Home getHome() throws InternalErrorException, HomeNotFoundException, UserNotFoundException;


	/**
	 * @return
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 * @throws UserNotFoundException
	 */
	public Home getGuestLogin() throws InternalErrorException, HomeNotFoundException, UserNotFoundException;

	
}
