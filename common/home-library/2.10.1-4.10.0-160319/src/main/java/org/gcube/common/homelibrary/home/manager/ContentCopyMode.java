/**
 * 
 */
package org.gcube.common.homelibrary.home.manager;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum ContentCopyMode {
	/**
	 * Replace the destination content.
	 */
	REPLACE_IF_EXISTS,
	
	/**
	 * Rename the copied content with a new unique name.
	 */
	RENAME_IF_EXISTS,
	
	/**
	 * If a name conflict exist skip the item. 
	 */
	SKIP_IF_EXISTS,
	
	/**
	 * If a name conflict exist the operation terminate and no changes are performed.
	 */
	FAIL_IF_EXISTS;

}
