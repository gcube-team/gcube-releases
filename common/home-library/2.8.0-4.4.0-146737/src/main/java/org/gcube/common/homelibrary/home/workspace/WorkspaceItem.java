/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRead;
import org.gcube.common.homelibrary.home.workspace.acl.Capabilities;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;

/**
 * Define a Workspace item like folder or folder-item.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */

public interface WorkspaceItem {

	/**
	 * This item id.
	 * @return the id.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public String getId() throws InternalErrorException;

	/**
	 * This item name.
	 * @return the name.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public String getName() throws InternalErrorException;

	/**
	 * This item description.
	 * @return the description.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public String getDescription() throws InternalErrorException;


	/**
	 * Set a new item description.
	 * @param description the new description.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public void setDescription(String description) throws InternalErrorException;

	/**
	 * Change this item name.
	 * @param name the new name.
	 * @throws InternalErrorException if an internal error occurs.  
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException if an item with this name already exists in the containing folder.
	 */
	public void rename(String name) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * This item creation time.
	 * @return the creation time.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public Calendar getCreationTime() throws InternalErrorException;

	/**
	 * This item last modification time.
	 * @return the last modification time.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public Calendar getLastModificationTime() throws InternalErrorException;

	/**
	 * Return last updated by
	 * @return
	 * @throws InternalErrorException
	 */
	public String getLastUpdatedBy() throws InternalErrorException;

	/**
	 * Return the last action on this Item.
	 * @return the last action.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public WorkspaceItemAction getLastAction() throws InternalErrorException;

	/**
	 * This item owner.
	 * @return the owner.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public User getOwner() throws InternalErrorException;

	/**
	 * The item capabilities.
	 * @return the capabilities.
	 */
	public Capabilities getCapabilities();

	/**
	 * The item properties.
	 * @return the properties.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public Properties getProperties() throws InternalErrorException;

	/**
	 * @return the list of {link
	 */
	public List<AccountingEntry> getAccounting();

	/**
	 * Return this item type.
	 * @return the type.
	 */
	public WorkspaceItemType getType();

	/**
	 * Return this item parent.
	 * @return the parent.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public WorkspaceFolder getParent() throws InternalErrorException;

	/**
	 * Return the current item path.
	 * @return the path.
	 * @throws InternalErrorException if an internal error occurs.  
	 */
	public String getPath() throws InternalErrorException; 

	/**
	 * Says if this item is a root element.
	 * @return <code>true</code> if this element is a root, <code>false</code> otherwise.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public boolean isRoot() throws InternalErrorException; 

	/**
	 * Return this item children.
	 * @return the children.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public List<? extends WorkspaceItem> getChildren() throws InternalErrorException;

	/**
	 * Remove a child from this item.
	 * @param child the child to remove.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 */
	public void removeChild(WorkspaceItem child) throws InternalErrorException, InsufficientPrivilegesException;

	/**
	 * Remove this item from the workspace.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 */
	public void remove() throws InternalErrorException, InsufficientPrivilegesException;

	/**
	 * Move this item to a new folder.
	 * @param destination the destination folder.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws WrongDestinationException if the destination folder have a wrong type.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException  if an item with the new name already exist in the destination folder.
	 */
	public void move(WorkspaceFolder destination) throws InternalErrorException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * Clone this item.
	 * @param cloneName the new clone name.
	 * @return the clone.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException if an item with the new name already exist.
	 */
	public WorkspaceItem cloneItem(String cloneName) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * Mark items as hidden
	 * @param flag <code>true</code> if the element is hidden, <code>false</code> if the element is visible.
	 * @throws InternalErrorException
	 */
	public void setHidden(boolean flag) throws InternalErrorException;

	/**
	 *  Return a flag indicating whether the element is hidden
	 * @return <code>true</code> if the element is hidden, <code>false</code> if the element is visible.
	 * @throws InternalErrorException
	 */
	public boolean isHidden() throws InternalErrorException;

	/**
	 *  Return a flag indicating whether the element is shared
	 * @return <code>true</code> if the element is shared, <code>false</code> otherwise.
	 * @throws InternalErrorException
	 */
	public boolean isShared() throws InternalErrorException;

	/**
	 * Return a flag indicating whether the element marked as read
	 * @return <code>true</code> if the element is marked as read, <code>false</code> otherwise.
	 * @throws InternalErrorException
	 */
	public boolean isMarkedAsRead() throws InternalErrorException;

	/**
	 * Get a list of item readers.
	 * @return the list of item readers.
	 * @throws InternalErrorException 
	 */
	public List<AccountingEntryRead> getReaders() throws InternalErrorException;

	/**
	 * Mark as read a shared item 
	 * @throws InternalErrorException
	 */
	public void markAsRead(boolean read) throws InternalErrorException;

	/**
	 * Get the id of the parent shared folder
	 * @return the id of the parent shared folder if the item is shared, null otherwise.
	 * @throws InternalErrorException
	 */
	public String getIdSharedFolder() throws InternalErrorException;

	/**
	 * Get the remote path of the element
	 * @return the remote path of the element
	 * @throws InternalErrorException
	 */
	public String getRemotePath() throws InternalErrorException;

	/**
	 * Check if the item is in the trash
	 * @return true if the item has been trashed
	 * @throws InternalErrorException
	 */
	public boolean isTrashed() throws InternalErrorException;

	/**
	 * Check if the item is a folder
	 * @return true if the item is a folder
	 * @throws InternalErrorException
	 */
	public boolean isFolder() throws InternalErrorException;

	/**
	 * Get a public link for the item
	 * @param shortenUrl
	 * @return storageId
	 * @throws InternalErrorException
	 */
	public String getPublicLink(boolean shortUrl) throws InternalErrorException;

	/**
	 * Get a storageId
	 * @return storageId
	 * @throws InternalErrorException
	 */
	public String getStorageID() throws InternalErrorException;


	/**
	 * Return true if the user has the right to read the resource at abspath
	 * @param user
	 * @param absPath: absolute path of a node
	 * @return true if the user has the right to read the resource at abspath
	 * @throws RepositoryException
	 * @throws InternalErrorException
	 */
	public boolean hasAccessRight(String user, String absPath) throws InternalErrorException;

	/**
	 * Delete ACL for a list of users
	 * @param users
	 * @throws InternalErrorException
	 */
	public void deleteACL(List<String> users) throws InternalErrorException;

	/**
	 * Get the ACL of a WorkspaceSharedFolder
	 * @return
	 * @throws InternalErrorException
	 */
	public ACLType getACLUser() throws InternalErrorException;

	/**
	 * Get a map of ACL of a WorkspaceSharedFolder
	 * @return
	 * @throws InternalErrorException
	 */
	public Map<ACLType, List<String>> getACLOwner() throws InternalErrorException;

	/**
	 * Update item
	 * @param fileData
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemNotFoundException
	 */
	void updateItem(InputStream fileData)
			throws InternalErrorException, InsufficientPrivilegesException, ItemNotFoundException;

//	/**
//	 * Get Versioning
//	 * @return a list of versions
//	 * @throws InternalErrorException
//	 */
//	public List<String> getVersioning() throws InternalErrorException;
//
//	/**
//	 * Restore Version
//	 * @param version a version to restore
//	 * @throws InternalErrorException
//	 */
//	public void restoreVersion(String version) throws InternalErrorException;

}
