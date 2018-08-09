/**
 *
 */

package org.gcube.common.storagehubwrapper.shared.tohl;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;

/**
 * The Interface WorkspaceItem. Define a Workspace item like folder or
 * folder-item.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         Jun 15, 2018
 */
public interface WorkspaceItem {

	/**
	 * This item id.
	 *
	 * @return the id.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public String getId()
		throws InternalErrorException;

	/**
	 * This item name.
	 *
	 * @return the name.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public String getName()
		throws InternalErrorException;

	/**
	 * This item description.
	 *
	 * @return the description.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public String getDescription()
		throws InternalErrorException;

	/**
	 * This item creation time.
	 *
	 * @return the creation time.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public Calendar getCreationTime()
		throws InternalErrorException;

	/**
	 * This item last modification time.
	 *
	 * @return the last modification time.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public Calendar getLastModificationTime()
		throws InternalErrorException;


	/**
	 * Gets the last modified by.
	 *
	 * @return the last modified by
	 * @throws InternalErrorException the internal error exception
	 */
	public String getLastModifiedBy()
		throws InternalErrorException;

	/**
	 * This item owner.
	 *
	 * @return the owner.
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public String getOwner()
		throws InternalErrorException;

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
	 * @throws InternalErrorException
	 *             if an internal error occurs.
	 */
	public boolean isRoot()
		throws InternalErrorException;

	/**
	 * Return a flag indicating whether the element is hidden.
	 *
	 * @return <code>true</code> if the element is hidden, <code>false</code> if
	 *         the element is visible.
	 * @throws InternalErrorException
	 *             the internal error exception
	 */
	public boolean isHidden()
		throws InternalErrorException;

	/**
	 * Return a flag indicating whether the element is shared.
	 *
	 * @return <code>true</code> if the element is shared, <code>false</code>
	 *         otherwise.
	 * @throws InternalErrorException
	 *             the internal error exception
	 */
	public boolean isShared()
		throws InternalErrorException;

	/**
	 * Check if the item is in the trash.
	 *
	 * @return true if the item has been trashed
	 * @throws InternalErrorException
	 *             the internal error exception
	 */
	public boolean isTrashed()
		throws InternalErrorException;

	/**
	 * Check if the item is a folder.
	 *
	 * @return true if the item is a folder
	 * @throws InternalErrorException
	 *             the internal error exception
	 */
	public boolean isFolder()
		throws InternalErrorException;
}
