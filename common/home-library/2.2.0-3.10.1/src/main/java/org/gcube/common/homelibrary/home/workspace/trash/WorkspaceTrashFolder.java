/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.trash;

import java.util.List;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

/**
 * @author valentina
 *
 */
public interface WorkspaceTrashFolder{
	
	/**
	 * Get trash id
	 * @return the user trash id
	 * @throws InternalErrorException
	 */
	public String getId() throws InternalErrorException;	

	/**
	 * Get trash path
	 * @return the user trash path
	 * @throws InternalErrorException
	 */
	public String getPath() throws InternalErrorException;
	
	/**
	 * Empty Trash 
	 * @return a list of WorkspaceItem ids not removed; the list is null if every item has been removed
	 * @throws InternalErrorException
	 */
	public List<String> emptyTrash() throws InternalErrorException;

	/**
	 * Restore all item in Trash 
	 * @return a list of WorkspaceItem ids not restored; the list is null if every item has been restored
	 * @throws InternalErrorException
	 */
	public List<String> restoreAll() throws InternalErrorException;

	/**
	 * Get Children of Trash Folder
	 * @return
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException 
	 */
	public List<WorkspaceTrashItem> listTrashItems() throws InternalErrorException, ItemNotFoundException;
	
	/**
	 * Get Trash Item by Id
	 * @param id
	 * @return
	 * @throws InternalErrorException
	 * @throws RepositoryException 
	 */
	public  WorkspaceTrashItem getTrashItemById(String id) throws InternalErrorException, RepositoryException;
}
