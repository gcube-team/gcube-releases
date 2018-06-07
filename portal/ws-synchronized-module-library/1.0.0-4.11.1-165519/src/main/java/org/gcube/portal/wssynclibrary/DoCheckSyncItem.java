/*
 *
 */
package org.gcube.portal.wssynclibrary;

import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;

// TODO: Auto-generated Javadoc
/**
 * The Interface DoCheckSyncItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2018
 * @param <T> the generic type
 */
public interface DoCheckSyncItem<T> {


	/**
	 * Check item synched.
	 *
	 * @param itemId the item id
	 * @return the t
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	T checkItemSynched(String itemId) throws ItemNotSynched, Exception;

}
