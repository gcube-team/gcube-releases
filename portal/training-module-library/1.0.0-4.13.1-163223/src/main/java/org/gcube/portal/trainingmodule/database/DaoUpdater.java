package org.gcube.portal.trainingmodule.database;


// TODO: Auto-generated Javadoc
/**
 * The Interface DaoUpdater.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
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
	 * Delete item by internal id.
	 *
	 * @param internalId the internal id
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteItemByInternalId(long internalId) throws DatabaseServiceException;
}
