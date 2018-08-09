package org.gcube.portal.wssynclibrary;

import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;


// TODO: Auto-generated Javadoc
/**
 * The Interface DoSyncItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 * @param <O> the generic type
 */
public interface DoSyncItem<O> {

	/**
	 * Do sync.
	 *
	 * @param itemId the item id
	 * @return the t
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	O doSync(String itemId) throws ItemNotSynched, Exception;


	/**
	 * Do un sync.
	 *
	 * @param itemId the item id
	 * @param deleteRemoteContent the delete remote content
	 * @return the boolean
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	Boolean doUnSync(String itemId, boolean deleteRemoteContent)
		throws ItemNotSynched, Exception;



	/**
	 * Gets the sync status.
	 *
	 * @param itemId the item id
	 * @return the sync status
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	O monitorSyncStatus(String itemId) throws ItemNotSynched, Exception;


	/**
	 * Removes the sync.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	Boolean removeSync(String itemId) throws ItemNotSynched, Exception;



	/**
	 * Stop sync.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	Boolean stopSync(String itemId) throws ItemNotSynched, Exception;



	/**
	 * Register callback for id.
	 *
	 * @param itemId the item id
	 * @throws Exception the exception
	 */
	void registerCallbackForId(String itemId) throws Exception;


}
