/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.acl.Capabilities;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public interface WorkspaceSmartFolder {

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
	 * Return this item type.
	 * @return the type.
	 */
	public WorkspaceItemType getType();
	
	/**
	 * Return this item children.
	 * @return the children.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public List<? extends WorkspaceItem> getChildren() throws InternalErrorException;
	
	public List<? extends SearchItem> getSearchItems() throws InternalErrorException;
	/**
	 * Remove this item from the workspace.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 */
	public void remove() throws InternalErrorException, InsufficientPrivilegesException;


	
}
