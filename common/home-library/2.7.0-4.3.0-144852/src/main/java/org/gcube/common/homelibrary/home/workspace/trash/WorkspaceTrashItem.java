/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.trash;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;

/**
 * @author Valentina Marioli
 *
 */


public interface WorkspaceTrashItem extends WorkspaceFolder {
	
	/**
	 * Delete Permanently an item in the trash folder
	 * @throws InternalErrorException
	 */
	void deletePermanently() throws InternalErrorException;
	
	/**
	 * Restore an item in the trash folder
	 * @throws InternalErrorException
	 */
	void restore() throws InternalErrorException;

	/**
	 * Get original parent Id to restore the item
	 * @return the original parent Id to restore the item
	 */
	String getOriginalParentId();
	
	/**
	 * Get the path where the item was deleted
	 * @return the path where the item was deleted
	 */
	String getDeletedFrom();

	/**
	 * Get the user who deleted the item
	 * @return the user who deleted the item
	 */
	String getDeletedBy();

	/**
	 * Get the date when the item was deleted
	 * @return the date when the item was deleted
	 */
	Calendar getDeletedTime();

	/**
	 * Return true if the trash item was a folder
	 * @return true if the trash item was a folder, false otherwise
	 */
	boolean isFolder();

	/**
	 * Get mime type
	 * @return the mime type of the trashed item
	 * @throws InternalErrorException
	 */
	String getMimeType() throws InternalErrorException;
	
	/**
	 * Get the name of the trashed item
	 * @return the name of the trashed item
	 * @throws InternalErrorException
	 */
	String getName() throws InternalErrorException;
		
	/**
	 * Return this item type.
	 * @return the type.
	 */
	WorkspaceItemType getType();


}