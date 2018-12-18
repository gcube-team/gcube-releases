/**
 *
 */

package org.gcube.common.storagehubwrapper.shared.tohl;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;


/**
 * The Interface WorkspaceItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 2, 2018
 */
public interface WorkspaceItem {

	/**
	 * This item id.
	 *
	 * @return the id.
	 */
	public String getId();

	/**
	 * This item name.
	 *
	 * @return the name.
	 */
	public String getName();

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle();

	/**
	 * This item description.
	 *
	 * @return the description.
	 */
	public String getDescription();

	/**
	 * This item creation time.
	 *
	 * @return the creation time.
	 */
	public Calendar getCreationTime();

	/**
	 * This item last modification time.
	 *
	 * @return the last modification time.
	 */
	public Calendar getLastModificationTime();

	/**
	 * Gets the last modified by.
	 *
	 * @return the last modified by
	 */
	public String getLastModifiedBy();

	/**
	 * This item owner.
	 *
	 * @return the owner.
	 */
	public String getOwner();

	/**
	 * Gets the map property.
	 *
	 * @return the map property
	 */
	public PropertyMap getPropertyMap();

	/**
	 * Gets the accounting.
	 *
	 * @return the accounting
	 */
	public List<AccountingEntry> getAccounting();

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath();

	/**
	 * Gets the parent id.
	 *
	 * @return the parent id
	 */
	public String getParentId();

	/**
	 * Return this item type.
	 *
	 * @return the type.
	 */
	public WorkspaceItemType getType();

	/**
	 * Says if this item is a root element.
	 *
	 * @return <code>true</code> if this element is a root, <code>false</code>
	 *         otherwise.
	 */
	public boolean isRoot();

	/**
	 * Return a flag indicating whether the element is hidden.
	 *
	 * @return <code>true</code> if the element is hidden, <code>false</code> if
	 *         the element is visible.
	 */
	public boolean isHidden();

	/**
	 * Return a flag indicating whether the element is shared.
	 *
	 * @return <code>true</code> if the element is shared, <code>false</code>
	 *         otherwise.
	 */
	public boolean isShared();

	/**
	 * Check if the item is in the trash.
	 *
	 * @return true if the item has been trashed
	 */
	public boolean isTrashed();

	/**
	 * Check if the item is a folder.
	 *
	 * @return true if the item is a folder
	 */
	public boolean isFolder();
}
