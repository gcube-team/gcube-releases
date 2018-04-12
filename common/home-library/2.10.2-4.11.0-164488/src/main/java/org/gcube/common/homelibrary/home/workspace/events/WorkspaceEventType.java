/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

/**
 * Define a event type.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum WorkspaceEventType {
	
	/**
	 * A new item has been created.
	 */
	ITEM_CREATED,
	
	/**
	 * An item has been deleted. 
	 */
	ITEM_REMOVED,
	
	/**
	 * An item has been imported. 
	 */
	ITEM_IMPORTED,
	
	/**
	 * An item has been renamed.
	 */
	ITEM_RENAMED,
	
	/**
	 * An item has been sent.
	 */
	ITEM_SENT, 
	
	/**
	 * An item has been updated.
	 */
	ITEM_UPDATED;

}
