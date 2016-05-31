/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.database;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;

/**
 * The Interface DaoUpdater.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 * @param <T> the generic type
 */
public interface DaoUpdater <T> {
	
	/**
	 * Creates the.
	 *
	 * @param item the item
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
	 */
	public boolean create(T item) throws DatabaseServiceException;
	
	/**
	 * Insert.
	 *
	 * @param item the item
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
	 */
	public boolean insert(T item) throws DatabaseServiceException;
	
	/**
	 * Update.
	 *
	 * @param item the item
	 * @return the t
	 * @throws DatabaseServiceException the database service exception
	 */
	public T update(T item) throws DatabaseServiceException;
	
	/**
	 * Removes the.
	 *
	 * @param item the item
	 * @param transaction the transaction
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
	 */
	public boolean remove(T item, boolean transaction) throws DatabaseServiceException;
	
	/**
	 * Removes the all.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int removeAll() throws DatabaseServiceException;
	
	/**
	 * Delete item by id field.
	 *
	 * @param idField the id field
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public abstract int deleteItemByIdField(String idField) throws DatabaseServiceException;
	
	/**
	 * Delete item by internal id.
	 *
	 * @param internalId the internal id
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteItemByInternalId(int internalId) throws DatabaseServiceException;
}
