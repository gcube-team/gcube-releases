/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.util.Map;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Properties {

	/**
	 * Get ID
	 * @return the properties id.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public String getId() throws InternalErrorException;

	/**
	 * Get property value
	 * @param propertyName the property name.
	 * @return the property value.
	 * @throws InternalErrorException 
	 */
	public String getPropertyValue(String propertyName) throws InternalErrorException;

	/**
	 * Get Properties
	 * @return the properties map.
	 * @throws InternalErrorException 
	 */
	public Map<String, String> getProperties() throws InternalErrorException;

	/**
	 * Add a new property.
	 * @param name the property name.
	 * @param value the property value.
	 * @throws InternalErrorException 
	 */
	@Deprecated
	public void addProperty(String name, String value) throws InternalErrorException;

	/**
	 * Add properties to item
	 * @param properties a map of properties
	 * @throws InternalErrorException
	 */
	public void addProperties(Map<String, String> properties) throws InternalErrorException;


	/**
	 * Save modified properties
	 * @throws InternalErrorException
	 */
	public void update() throws InternalErrorException;
}
