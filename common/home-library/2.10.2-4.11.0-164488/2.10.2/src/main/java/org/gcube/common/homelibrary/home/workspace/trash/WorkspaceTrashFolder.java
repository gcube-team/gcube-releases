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
	 * @deprecated use {@link org.gcube.common.homelibrary.home.workspace.Workspace#getItem(String itemId)} instead 
	 */
	@Deprecated
	public  WorkspaceTrashItem getTrashItemById(String id) throws InternalErrorException, RepositoryException;



}
