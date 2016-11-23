/**
 * 
 */
package org.gcube.common.homelibrary.home.data.application;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface ApplicationDataArea {
	
	/**
	 * The application name.
	 * @return the name.
	 */
	public String getApplicationName();
	
	/**
	 * The list of data names.
	 * @return the data names list.
	 */
	public List<String> getDataNames();
	
	/**
	 * Return the application data requested.
	 * @param dataName the application data name.
	 * @return the application data.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ApplicationDataNotFoundException if the ApplicationData specified don't exist.
	 */
	public ApplicationData getData(String dataName) throws ApplicationDataNotFoundException, InternalErrorException;
	
	/**
	 * Check if a specified application data exists.
	 * @param dataName the application data name.
	 * @return <code>true</code> if the application data exist, <code>false</code> otherwise.
	 */
	public boolean existsData(String dataName);
	

	/**
	 * Create a new list. If an ApplicationList with the specified name already exists the old one is removed.
	 * @param <E> the list item type.
	 * @param dataName the list name.
	 * @return the new application list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public <E> ApplicationList<E> createList(String dataName) throws InternalErrorException;
	
	/**
	 * If the ApplicationList with the specified name already exists this one is returned, otherwise a new one is created.
	 * @param <E> the list item type.
	 * @param dataName the list name.
	 * @return the application list.
	 * @throws InternalErrorException if an error occurs.
	 */
	public <E> ApplicationList<E> getList(String dataName) throws InternalErrorException;
	
	/**
	 * Create a new map. If an ApplicationMap with the specified name already exists the old one is removed.
	 * @param <K> the map key type.
	 * @param <V> the map value type.
	 * @param dataName the map name.
	 * @return the new application map.
	 * @throws InternalErrorException if an error occurs. 
	 */
	public <K, V> ApplicationMap<K, V> createMap(String dataName) throws InternalErrorException;
	
	/**
	 * If an ApplicationMap with the specified name already exists this one is returnes, otherwise a new one is created.
	 * @param <K> the map key type.
	 * @param <V> the map value type.
	 * @param dataName the map name.
	 * @return the application map.
	 * @throws InternalErrorException if an error occurs. 
	 */
	public <K, V> ApplicationMap<K, V> getMap(String dataName) throws InternalErrorException;
	
	/**
	 * Delete the specified application data.
	 * @param dataName the data name.
	 * @return the removed application data.
	 * @throws InternalErrorException if an error occurs. 
	 */
	public ApplicationData deleteData(String dataName) throws InternalErrorException;
}
