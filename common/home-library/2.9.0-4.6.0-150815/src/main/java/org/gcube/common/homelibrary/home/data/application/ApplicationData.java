/**
 * 
 */
package org.gcube.common.homelibrary.home.data.application;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface ApplicationData {
	
	/**
	 * Return the application data name.
	 * @return the application data name.
	 */
	public String getName();
	
	/**
	 * Return the application data type.
	 * @return the application data type.
	 */
	public ApplicationDataType getType();
	
	/**
	 * Update the application data.
	 * @throws RuntimeException if an error occurs;
	 */
	public void dataUpdated() throws RuntimeException;

}
