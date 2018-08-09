/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.trash;

import java.util.List;

import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;


/**
 * The Interface WorkspaceTrashFolder.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 26, 2018
 */
public interface WorkspaceTrashFolder extends WorkspaceFolder {

	/**
	 * Empty Trash.
	 *
	 * @return a list of WorkspaceItem ids not removed; the list is null if every item has been removed
	 * @throws InternalErrorException the internal error exception
	 */
	public List<String> emptyTrash() throws InternalErrorException;

	/**
	 * Delete permanently a specific item.
	 *
	 * @param id item to delete
	 * @throws InternalErrorException the internal error exception
	 */
	public void deletePermanentlyById(String id) throws InternalErrorException;

	/**
	 * Restore all item in Trash.
	 *
	 * @return a list of WorkspaceItem ids not restored; the list is null if every item has been restored
	 * @throws InternalErrorException the internal error exception
	 */
	public List<String> restoreAll() throws InternalErrorException;

	/**
	 * Restore a specific item.
	 *
	 * @param id item to restore
	 * @throws InternalErrorException the internal error exception
	 */
	public void restoreById(String id) throws InternalErrorException;


	/**
	 * Get trashed items.
	 *
	 * @return the list of trashed items
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemNotFoundException the item not found exception
	 * @deprecated use {@link #getChildren()} instead
	 */
	@Deprecated
	public List<WorkspaceTrashItem> listTrashItems() throws InternalErrorException, ItemNotFoundException;

	/**
	 * Get Trash Item by Id.
	 *
	 * @param id of a trashed item
	 * @return a trashed item
	 * @throws InternalErrorException the internal error exception
	 * @deprecated use {@link org.gcube.portal.storagehubwrapper.server.tohl.homelibrary.home.workspace.Workspace#getItem(String itemId)} instead
	 */
	@Deprecated
	public  WorkspaceTrashItem getTrashItemById(String id) throws InternalErrorException;



}
