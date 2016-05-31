/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.trash;

import java.util.List;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

/**
 * @author Valentina Marioli
 *
 */
public interface WorkspaceTrashFolder extends WorkspaceFolder {

//	/**
//	 * Get trash id
//	 * @return the user trash id
//	 * @throws InternalErrorException
//	 */
//	public String getId() throws InternalErrorException;	
//
//	/**
//	 * Get trash path
//	 * @return the user trash path
//	 * @throws InternalErrorException
//	 */
//	public String getPath() throws InternalErrorException;

	/**
	 * Empty Trash 
	 * @return a list of WorkspaceItem ids not removed; the list is null if every item has been removed
	 * @throws InternalErrorException
	 */
	public List<String> emptyTrash() throws InternalErrorException;

	/**
	 * Delete permanently a specific item
	 * @param id item to delete
	 * @throws InternalErrorException
	 */
	public void deletePermanentlyById(String id) throws InternalErrorException;

	/**
	 * Restore all item in Trash 
	 * @return a list of WorkspaceItem ids not restored; the list is null if every item has been restored
	 * @throws InternalErrorException
	 */
	public List<String> restoreAll() throws InternalErrorException;

	/**
	 * Restore a specific item
	 * @param id item to restore
	 * @throws InternalErrorException
	 */
	public void restoreById(String id) throws InternalErrorException;


	/**
	 * Get trashed items
	 * @return the list of trashed items
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException 
	 * @deprecated use {@link #getChildren()} instead  
	 */
	@Deprecated
	public List<WorkspaceTrashItem> listTrashItems() throws InternalErrorException, ItemNotFoundException;

	/**
	 * Get Trash Item by Id
	 * @param id of a trashed item
	 * @return a trashed item
	 * @throws InternalErrorException
	 * @throws RepositoryException 
	 * @deprecated use {@link Workspace#getItem()} instead 
	 */
	@Deprecated
	public  WorkspaceTrashItem getTrashItemById(String id) throws InternalErrorException, RepositoryException;



}
