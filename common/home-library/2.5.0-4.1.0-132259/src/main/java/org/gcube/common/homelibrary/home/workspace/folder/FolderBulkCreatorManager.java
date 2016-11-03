package org.gcube.common.homelibrary.home.workspace.folder;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * 
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface FolderBulkCreatorManager {

	/**
	 * @return the actives bulk creators.
	 */
	public abstract List<FolderBulkCreator> getActiveFolderBulkCreators();

	/**
	 * Wait for the specified bulk creator.
	 * @param id the folder bulk creator id.
	 * @throws InterruptedException if an internal error occurs.
	 */
	public abstract void waitFolderBulkCreator(String id) throws InterruptedException;

	/**
	 * @param id
	 * @return
	 * @throws InternalErrorException
	 */
	FolderBulkCreator getActiveFolderBulkCreator(String id)
			throws InternalErrorException;

}