/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.trash;

import java.util.Calendar;

import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;

/**
 * The Interface WorkspaceTrashItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface WorkspaceTrashItem extends WorkspaceFolder {

	/**
	 * Get original parent Id to restore the item.
	 *
	 * @return the original parent Id to restore the item
	 */
	String getOriginalParentId();

	/**
	 * Get the path where the item was deleted.
	 *
	 * @return the path where the item was deleted
	 */
	String getDeletedFrom();

	/**
	 * Get the user who deleted the item.
	 *
	 * @return the user who deleted the item
	 */
	String getDeletedBy();

	/**
	 * Get the date when the item was deleted.
	 *
	 * @return the date when the item was deleted
	 */
	Calendar getDeletedTime();

	/**
	 * Return true if the trash item was a folder.
	 *
	 * @return true if the trash item was a folder, false otherwise
	 */
	boolean isFolder();

	/**
	 * Get mime type.
	 *
	 * @return the mime type of the trashed item
	 * @throws InternalErrorException the internal error exception
	 */
	String getMimeType() throws InternalErrorException;

	/**
	 * Get the name of the trashed item.
	 *
	 * @return the name of the trashed item
	 * @throws InternalErrorException the internal error exception
	 */
	String getName() throws InternalErrorException;

	/**
	 * Return this item type.
	 * @return the type.
	 */
	WorkspaceItemType getType();

	/**
	 * Delete Permanently an item in the trash folder.
	 *
	 * @throws InternalErrorException the internal error exception
	 */
	void deletePermanently() throws InternalErrorException;

	/**
	 * Restore an item in the trash folder.
	 *
	 * @throws InternalErrorException the internal error exception
	 */
	void restore() throws InternalErrorException;

}