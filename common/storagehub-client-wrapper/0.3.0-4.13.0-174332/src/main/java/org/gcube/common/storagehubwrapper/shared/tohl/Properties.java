/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl;

import java.util.Map;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 * The Interface Properties.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface Properties {

	/**
	 * Get ID.
	 *
	 * @return the properties id.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public String getId() throws InternalErrorException;

	/**
	 * Get property value.
	 *
	 * @param propertyName the property name.
	 * @return the property value.
	 * @throws InternalErrorException the internal error exception
	 */
	public String getPropertyValue(String propertyName) throws InternalErrorException;

	/**
	 * Get Properties.
	 *
	 * @return the properties map.
	 * @throws InternalErrorException the internal error exception
	 */
	public Map<String, String> getProperties() throws InternalErrorException;

//	/**
//	 * Add properties to item.
//	 *
//	 * @param properties a map of properties
//	 * @throws InternalErrorException the internal error exception
//	 */
//	public void addProperties(Map<String, String> properties) throws InternalErrorException;
//
//	/**
//	 * Save modified properties.
//	 *
//	 * @throws InternalErrorException the internal error exception
//	 */
//	public void update() throws InternalErrorException;
//
//	/**
//	 * Check if the item has a given property.
//	 *
//	 * @param property the property
//	 * @return true, if successful
//	 * @throws InternalErrorException the internal error exception
//	 */
//	public boolean hasProperty(String property) throws InternalErrorException;
}
