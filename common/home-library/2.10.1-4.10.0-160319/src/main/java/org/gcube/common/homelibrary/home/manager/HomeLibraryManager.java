/**
 * 
 */
package org.gcube.common.homelibrary.home.manager;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.ScopeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

/**
 * This class define a list of methods to manage users homes.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface HomeLibraryManager {
	
	/**
	 * Add the new vre to Home lists.
	 * @param scope the vre to add.
	 * @throws InternalErrorException if an error occurs.
	 */
	//public void addVRE(String scope) throws InternalErrorException;
	
	/**
	 * Move the specified vre homes to destination vre.
	 * @param sourceScope the source vre.
	 * @param destinationScope the destination vre.
	 * @param replaceDestination flag that indicate to replace the destination vre if already exists. 
	 * @throws InternalErrorException if an error occurs.
	 */
	//public void moveVRE(String sourceScope, String destinationScope, boolean replaceDestination) throws InternalErrorException;
	
	/**
	 * Copy the specified scope homes to destination scope.
	 * If the copied home already exists on destination scope the content is copied inside.
	 * @param sourceScope the source scope.
	 * @param destinationScope the destination scope.
	 * @param mode the copy mode.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ScopeNotFoundException if a specified scope don't exists.
	 * @throws WorkspaceFolderNotFoundException if a specified user workspace is not found.
	 * @throws HomeNotFoundException if a specified user home is not found.
	 */
	public void copyScopeHomes(String sourceScope, String destinationScope, ContentCopyMode mode) throws InternalErrorException, ScopeNotFoundException, HomeNotFoundException, WorkspaceFolderNotFoundException;
	
	
	/**
	 * Remove the specified scope with all contained homes.
	 * @param scope the scope to remove.
	 * @throws InternalErrorException if an error occurs removing the specified scope.
	 */
	public void removeScope(String scope) throws InternalErrorException;
	
	/**
	 * Delete all homes from specified vre.
	 * @param scope the target scope.
	 * @throws InternalErrorException if an error occurs.
	 */
	//public void deleteVRE(String scope) throws InternalErrorException;

	/**
	 * Copy the a user workspace content to another user workspace.
	 * @param sourceScope the source scope.
	 * @param sourceUser the source user.
	 * @param destinationScope the destination scope.
	 * @param destinationUser the destination user.
	 * @param mode the copy mode.
	 * @return <code>true</code> if the copy have terminated successfully.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ScopeNotFoundException if a specified scope don't exists.
	 * @throws UserNotFoundException if a specified user don't exists.
	 * @throws HomeNotFoundException if a specified user home is not found.
	 * @throws WorkspaceFolderNotFoundException if a specified user workspace is not found.
	 */
	public boolean copyWorkspaceContent(String sourceScope, String sourceUser, String destinationScope, String destinationUser, ContentCopyMode mode) throws InternalErrorException, ScopeNotFoundException, UserNotFoundException, HomeNotFoundException, WorkspaceFolderNotFoundException;

}
