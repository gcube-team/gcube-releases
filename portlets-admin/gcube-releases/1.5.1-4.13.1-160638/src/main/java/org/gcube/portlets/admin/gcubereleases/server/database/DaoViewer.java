/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.database;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence.SQL_ORDER;

/**
 * The Interface DaoViewer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 * @param <T> the generic type
 */
public interface DaoViewer<T> {
	
	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getRows() throws DatabaseServiceException;
	
	/**
	 * Gets the rows.
	 *
	 * @param startIndex the start index
	 * @param offset the offset
	 * @return the rows
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getRows(int startIndex, int offset) throws DatabaseServiceException;
	
	/**
	 * Count items.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int countItems() throws DatabaseServiceException;
	
	/**
	 * Gets the rows filtered.
	 *
	 * @param filterMap the filter map
	 * @return the rows filtered
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getRowsFiltered(Map<String, String> filterMap) throws DatabaseServiceException;
	
	/**
	 * Gets the item by key.
	 *
	 * @param id the id
	 * @param t the t
	 * @return the item by key
	 * @throws DatabaseServiceException the database service exception
	 */
	public T getItemByKey(Integer id, Class<T> t) throws DatabaseServiceException;
	
	/**
	 * Gets the rows ordered.
	 *
	 * @param orderByField the order by field
	 * @param order the order
	 * @return the rows ordered
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getRowsOrdered(String orderByField, SQL_ORDER order) throws DatabaseServiceException;	
}
