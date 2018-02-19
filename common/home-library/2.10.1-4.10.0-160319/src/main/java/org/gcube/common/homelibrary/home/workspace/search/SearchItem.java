/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.search;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

/**
 * @author gioia
 *
 */
public interface SearchItem {

	/**
	 * Get the id of the SearchItem
	 * @return the item id
	 */
	public String getId();
	
	/**
	 * Get the name of the SearchItem
	 * @return the item name
	 */
	public String getName();
	
	/**
	 * Get the Creation Date of the SearchItem
	 * @return the item creation date
	 */	
	public Calendar getCreationDate();
	
	/**
	 * Get the Last Modified of the SearchItem
	 * @return the last modified date
	 */
	public Calendar getLastModified();
	
	/**
	 * Get the owner of the SearchItem
	 * @return the owner of the item
	 */
	public String getOwner();
	
	/**
	 * Get the Creation Date of the SearchItem
	 * @return the item type
	 */
	public WorkspaceItemType getType();
	
	/**
	 * Check if the SearchItem is a VRE folder
	 * @return true if the Folder is a VRE folder
	 */
	public boolean isVreFolder();
	
	/**
	 * Check if the SearchItem is shared
	 * @return true if the item is shared
	 */
	public boolean isShared();
}
