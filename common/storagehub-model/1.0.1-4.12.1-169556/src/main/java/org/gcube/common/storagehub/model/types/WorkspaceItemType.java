/**
 * 
 */
package org.gcube.common.storagehub.model.types;


/**
 * @author gioia
 *
 */
public enum WorkspaceItemType implements GenericItemType{
	
	/**
	 * A folder.
	 */
	FOLDER,
	
	/**
	 * A shared folder
	 */
	SHARED_FOLDER,
	
	/**
	 * A smart folder
	 */
	SMART_FOLDER,
	
	/**
	 * A folder item.
	 */
	FOLDER_ITEM,
	
	/**
	 * A trash folder.
	 */	
	TRASH_FOLDER,
	
	/**
	 * A trash item.
	 */	
	TRASH_ITEM;

	
}
