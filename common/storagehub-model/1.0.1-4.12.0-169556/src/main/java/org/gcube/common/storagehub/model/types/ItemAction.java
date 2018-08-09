/**
 * 
 */
package org.gcube.common.storagehub.model.types;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum ItemAction {
	/**
	 * The item has been created.
	 */
	CREATED,
	
	/**
	 * The item has been renamed.
	 */
	RENAMED,
	
	/**
	 * The item has been moved. 
	 */
	MOVED,
	
	/**
	 * The item has been cloned. 
	 */
	CLONED,
	
	/**
	 * The item has been updates. 
	 */
	UPDATED;

}
