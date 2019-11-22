/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl;

import java.util.Calendar;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 * The Interface TrashedItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Sep 21, 2018
 */
public interface TrashedItem extends WorkspaceItem {

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
	 */
	String getName();

	/**
	 * Return this item type.
	 * @return the type.
	 */
	WorkspaceItemType getType();

}