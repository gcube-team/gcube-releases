/**
 * 
 */
package org.gcube.common.homelibrary.home;

import java.util.List;

import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

/**
 * Represent a user's home.
 * For each home we have an owner, a Data Area and a Workspace.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Home {
	
	/**
	 * Return this home manager.
	 * @return the home manager.
	 */
	public HomeManager getHomeManager();
	
	/**
	 * Retrieves the home's owner.
	 * @return the owner.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public User getOwner();
	
	/**
	 * Retrieves the user workspace.
	 * @return the workspace.
	 * @throws WorkspaceFolderNotFoundException when user the workspace is not found.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public Workspace getWorkspace() throws WorkspaceFolderNotFoundException, InternalErrorException;
	
	/**
	 * Retrieves the user data area.
	 * @return the data area.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public ApplicationsArea getDataArea() throws InternalErrorException;

	/**
	 * @throws InternalErrorException 
	 * 
	 */
	public List<String> listScopes() throws InternalErrorException;

	
	

}
