/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.trash;

import java.util.Calendar;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;

/**
 * @author valentina
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
	 * @return
	 */
	String getOriginalParentId();
	
	/**
	 * Get original path
	 * @return
	 */
	String getDeletedFrom();

	/**
	 * Get the name of the user who deleted the item
	 * @return
	 */
	String getDeletedBy();

	/**
	 * Get the date when the item was deleted
	 * @return
	 */
	Calendar getDeletedTime();

	/**
	 * Return true if the trash item was a folder
	 * @return
	 */
	boolean isFolder();

	/**
	 * @return
	 * @throws InternalErrorException
	 */
	String getMimeType() throws InternalErrorException;
	
	/**
	 * @return
	 * @throws InternalErrorException
	 */
	String getName() throws InternalErrorException;
	


}