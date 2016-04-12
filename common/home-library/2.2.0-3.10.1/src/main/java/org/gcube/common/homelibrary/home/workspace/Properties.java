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
	 * @return the properties id.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public String getId() throws InternalErrorException;

	/**
	 * @param propertyName the property name.
	 * @return the property value.
	 * @throws InternalErrorException 
	 */
	public String getPropertyValue(String propertyName) throws InternalErrorException;

	/**
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
	public void addProperty(String name, String value) throws InternalErrorException;

	/**
	 * Save modified properties
	 * @throws InternalErrorException
	 */
	public void update() throws InternalErrorException;

}
