/**
 *
 */
package org.gcube.common.storagehubwrapper.server.tohl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.GenericItemType;
import org.gcube.common.storagehubwrapper.shared.ACLType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceVREFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InsufficientPrivilegesException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemAlreadyExistException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongDestinationException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongItemTypeException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ItemStreamDescriptor;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLFileItem;



/**
 * The Interface Workspace.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 *
 * Represents a HL user workspace.
 */
public interface Workspace{


	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	public String getOwner() throws InternalErrorException, Exception;

	/**
	 * Returns the workspace root.
	 *
	 * @return the root.
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	public WorkspaceFolder getRoot() throws InternalErrorException, Exception;


	/**
	 * Gets the children.
	 *
	 * @param id the id
	 * @return the children
	 * @throws Exception the exception
	 */
	public List<? extends WorkspaceItem> getChildren(String id) throws Exception;


	/**
	 * Gets the parents by id.
	 *
	 * @param id the id
	 * @return the parents by id
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	public List<? extends WorkspaceItem> getParentsById(String id) throws InternalErrorException, Exception;

	/**
	 * Return the item with the specified id.
	 *
	 * @param itemId the item id.
	 * @return the item.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException, InternalErrorException, Exception;


	/**
	 * Return the item with the specified id.
	 *
	 * @param itemId the item id.
	 * @param withAccounting the with accounting
	 * @param withFileDetails the with file details
	 * @param withMapProperties the with map properties
	 * @return the item.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	public WorkspaceItem getItem(String itemId, boolean withAccounting, boolean withFileDetails, boolean withMapProperties) throws ItemNotFoundException, InternalErrorException, Exception;


	/**
	 * Create a new folder with specified name.
	 * The new folder is created into the specified folder.
	 *
	 * @param name the folder name.
	 * @param description the folder description.
	 * @param destinationFolderId the destination folder.
	 * @return the new folder.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException if an item with the same exist in the destination folder.
	 * @throws WrongDestinationException if the destination item is not a folder.
	 * @throws ItemNotFoundException if the destination folder has not been found.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws Exception the exception
	 */
	public WorkspaceFolder createFolder(String name, String description, String destinationFolderId) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException, Exception;

	/**
	 * Create a new folder with properties.
	 *
	 * @param name the name
	 * @param description the description
	 * @param destinationFolderId the destination folder id
	 * @param properties the properties
	 * @return the new folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceFolder createFolder(String name, String description, String destinationFolderId, Map<String, String> properties) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException;


	/**
	 * Upload file.
	 *
	 * @param folderId the folder id
	 * @param inputStream the input stream
	 * @param fileName the file name
	 * @param fileDescription the file description
	 * @return the item
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 */
	public WorkspaceItem uploadFile(String folderId, InputStream inputStream, String fileName, String fileDescription) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Gets the shared folder members.
	 *
	 * @param folderid the folderid
	 * @return the shared folder members
	 * @throws Exception the exception
	 */
	public List<String> getSharedFolderMembers(String folderid) throws Exception;


	/**
	 * Check if an item with the specified name exists in the specified folder.
	 * @param name the name to check.
	 * @param folderId the folder where to search the item.
	 * @return <code>true</code> if the item exists, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ItemNotFoundException if the folder has not been found.
	 * @throws WrongItemTypeException if the folderId referrer to an item with type different from Workspace or folder.
	 */
	public boolean exists(String name, String folderId) throws InternalErrorException, ItemNotFoundException, WrongItemTypeException;

	/**
	 * Get an item with the specified name in the specified folder.
	 * @param name the item name to find.
	 * @param folderId the folder where to search the item.
	 * @return the item if the item is found, <code>null</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ItemNotFoundException if the folder has not been found.
	 * @throws WrongItemTypeException if the folderId referrer to an item with type different from Workspace or folder.
	 */
	public List<WorkspaceItem> find(String name, String folderId) throws InternalErrorException, ItemNotFoundException, WrongItemTypeException;


	/**
	 * Gets the root shared folder.
	 *
	 * @param itemId the item id
	 * @return the root shared folder
	 * @throws Exception the exception
	 */
	public WorkspaceItem getRootSharedFolder(String itemId) throws Exception;



	/**
	 * Checks if is item shared.
	 *
	 * @param itemId the item id
	 * @return true, if is item shared
	 * @throws Exception the exception
	 */
	public boolean isItemShared(String itemId) throws Exception;


	/**
	 * Gets the VRE folders id.
	 *
	 * @return the VRE folders id
	 * @throws Exception the exception
	 */
	public String getVREFoldersId() throws Exception;

	/**
	 * Get Trash Folder.
	 *
	 * @return the trash folder
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws Exception the exception
	 */
	public WorkspaceItem getTrash() throws InternalErrorException, ItemNotFoundException, Exception;


	/**
	 * Empty trash.
	 *
	 * @throws Exception the exception
	 */
	public void emptyTrash() throws Exception;



	/**
	 * Restore thrash item.
	 *
	 * @param itemId the item id
	 * @return the item
	 * @throws Exception the exception
	 */
	public WorkspaceItem restoreThrashItem(String itemId) throws Exception;



	/**
	 * Gets the filtered children.
	 *
	 * @param id the id
	 * @param aType the a type
	 * @return the filtered children
	 * @throws Exception the exception
	 */
	public List<? extends WorkspaceItem> getFilteredChildren(String id, Class<? extends Item> aType) throws Exception;



	/**
	 * Rename an item.
	 *
	 * @param itemId the item id.
	 * @param newName the new name.
	 * @return the workspace item
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if the user don't have sufficient privileges to perform this operation.
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws Exception the exception
	 */
	public WorkspaceItem renameItem(String itemId, String newName) throws ItemNotFoundException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, Exception;


	/**
	 * Gets the public link to the latest version of file item.
	 *
	 * @param fileItemId the file item id
	 * @return the public link for file
	 * @throws Exception the exception
	 */
	public URL getPublicLinkForFile(String fileItemId) throws Exception;

	/**
	 * Gets the public link of the file at the input version
	 *
	 * @param fileItemId the file item id
	 * @param version the version
	 * @return the public link for file
	 * @throws Exception the exception
	 */
	public URL getPublicLinkForFile(String fileItemId, String version) throws Exception;


	/**
	 * Gets the list versions for file.
	 *
	 * @param fileItemId the file item id
	 * @return the list versions for file
	 * @throws Exception the exception
	 */
	public List<WorkspaceFileVersion> getListVersionsForFile(String fileItemId) throws Exception;


	/**
	 * Gets the thumbnail data.
	 *
	 * @param itemId the item id
	 * @return the thumbnail data
	 * @throws Exception the exception
	 */
	public ItemStreamDescriptor getThumbnailData(String itemId) throws Exception;


	/**
	 * Mark as public.
	 *
	 * @param itemId the item id
	 * @param isPublic the is public
	 * @return the boolean
	 */
	public Boolean markAsPublic(String itemId, boolean isPublic);


	/**
	 * Gets the metadata.
	 *
	 * @param itemId the item id
	 * @return the metadata
	 * @throws Exception the exception
	 */
	public Map<String, Object> getMetadata(String itemId) throws Exception;




































	/**
	 * Create a new External URL into a folder.
	 *
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the external URL value.
	 * @param destinationFolderId the destination folder.
	 * @return the new external URL.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public URLFileItem createExternalUrl(String name, String description, String url, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, IOException;

	/**
	 * Add a Bookmark.
	 *
	 * @param name the name
	 * @param description the description
	 * @param url the url
	 * @param destinationfolderId the destinationfolder id
	 * @return the external url
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/**
	 * Create a new External URL into a folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the external URL.
	 * @param destinationfolderId the destination folder.
	 * @return the new external URL.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 * @throws IOException
	 */
	public URLFileItem createExternalUrl(String name, String description, InputStream url, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException;


	/**
	 * Delete item.
	 *
	 * @param itemId the item id
	 * @throws ItemNotFoundException the item not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws Exception the exception
	 */
	public void deleteItem(String itemId) throws ItemNotFoundException, InternalErrorException, InsufficientPrivilegesException, Exception;


	/**
	 * Move item.
	 *
	 * @param itemId the item id
	 * @param destinationFolderId the destination folder id
	 * @return the workspace item
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws Exception the exception
	 */
	public WorkspaceItem moveItem(String itemId, String destinationFolderId) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WorkspaceFolderNotFoundException, Exception;


	/**
	 * Change an item description.
	 * @param itemId the item to update.
	 * @param newDescription the new item description.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void changeDescription(String itemId, String newDescription) throws ItemNotFoundException, InternalErrorException;


	/**
	 * Return the item with the specified path.
	 *
	 * @param path the item path.
	 * @return the item.
	 * @throws ItemNotFoundException if the item has not been found.
	 */
	public WorkspaceItem getItemByPath(String path) throws ItemNotFoundException;

	/**
	 * Remove an item from a folder.
	 * @param itemName the item name.
	 * @param folderId the folder id.
	 * @throws ItemNotFoundException if the folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WrongItemTypeException if the specified folder is neither a workspace nor a folder.
	 */
	public void remove(String itemName, String folderId) throws ItemNotFoundException, InternalErrorException, InsufficientPrivilegesException, WrongItemTypeException;

	/**
	 * Copy file.
	 *
	 * @param itemId the item id
	 * @param folderDestinationId the folder destination id
	 * @return the list
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws Exception the exception
	 */
	WorkspaceItem copyFile(String itemId, String folderDestinationId)
		throws ItemNotFoundException, WrongDestinationException,
		InternalErrorException, ItemAlreadyExistException,
		InsufficientPrivilegesException, Exception;

	/**
	 * Copy file items.
	 *
	 * @param itemIds the item ids
	 * @param folderDestinationId the folder destination id
	 * @return the list
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws Exception the exception
	 */
	List<WorkspaceItem> copyFileItems(
		List<String> itemIds, String folderDestinationId)
		throws ItemNotFoundException, WrongDestinationException,
		InternalErrorException, ItemAlreadyExistException,
		InsufficientPrivilegesException, Exception;

	/**
	 * Move items.
	 *
	 * @param itemIds the item ids
	 * @param folderDestinationId the folder destination id
	 * @return the list
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws Exception the exception
	 */
	List<WorkspaceItem> moveItems(List<String> itemIds, String folderDestinationId)
		throws ItemNotFoundException, WrongDestinationException,
		InsufficientPrivilegesException, InternalErrorException,
		ItemAlreadyExistException, Exception;

	/**
	 * Search by Name.
	 *
	 * @param name the name
	 * @param folderId the folder id
	 * @return a list of SearchItem
	 * @throws InternalErrorException the internal error exception
	 */
	public List<WorkspaceItem> searchByName(String name, String folderId) throws InternalErrorException;

	/**
	 * Search By MimeType.
	 *
	 * @param mimeType the mime type
	 * @return a list of SearchFolderItem
	 * @throws InternalErrorException the internal error exception
	 */
	public List<WorkspaceItem> searchByMimeType(String mimeType)
			throws InternalErrorException;

	/**
	 * Get items by type.
	 *
	 * @param type the type
	 * @return a list of SearchItem
	 * @throws InternalErrorException the internal error exception
	 */
	public List<WorkspaceItem> getFolderItems(GenericItemType type) throws InternalErrorException;


	/**
	 * Create a shared folder with a list of users.
	 *
	 * @param name the name
	 * @param description the description
	 * @param users A list of portal logins
	 * @param destinationFolderId the destination folder id
	 * @return the shared folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceSharedFolder createSharedFolder(String name, String description,
			List<String> users, String destinationFolderId)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Create a shared folder associated with a groupId.
	 *
	 * @param name the name of the folder
	 * @param description the description
	 * @param groupId an existing groupId to associate with the folder
	 * @param destinationFolderId the destination folder id
	 * @param displayName a friendly name for the folder
	 * @param isVREFolder a flag to indicate the folder is a VRE Folder
	 * @return the shared folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceSharedFolder createSharedFolder(String name, String description,
			String groupId, String destinationFolderId, String displayName, boolean isVREFolder)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Shared an exist {@link WorkspaceFolder} with a list of users.
	 *
	 * @param users A list of portal logins.
	 * @param destinationFolderId the destination folder id
	 * @return the shared folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceSharedFolder shareFolder(List<String> users, String destinationFolderId)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException, WrongDestinationException,
			ItemNotFoundException, WorkspaceFolderNotFoundException;


	/**
	 * Shared an exist {@link WorkspaceFolder} with a list of users.
	 *
	 * @param users A list of portal logins.
	 * @param itemId the item id
	 * @return the shared folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceSharedFolder share(List<String> users, String itemId)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException, WrongDestinationException,
			ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Create a GCubeItem.
	 *
	 * @param name the name
	 * @param description the description
	 * @param scopes the scopes
	 * @param creator the creator
	 * @param itemType the item type
	 * @param properties the properties
	 * @param destinationFolderId the destination folder id
	 * @return a GCubeItem
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 */
	public WorkspaceItem createGcubeItem(String name, String description,
			List<String> scopes, String creator, String itemType, Map<String, String> properties,
			String destinationFolderId) throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException;

	/**
	 * Unshare a shared item.
	 *
	 * @param itemId the item id
	 * @return the workspace item
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemNotFoundException the item not found exception
	 */
	public WorkspaceItem unshare(String itemId) throws InternalErrorException, ItemNotFoundException;



	/**
	 * Get MySpecialFolders.
	 *
	 * @return my special folders
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemNotFoundException the item not found exception
	 */
	public WorkspaceFolder getMySpecialFolders() throws InternalErrorException, ItemNotFoundException;


	/**
	 * Search By Properties.
	 *
	 * @param properties the properties
	 * @return a list of WorkspaceItem
	 * @throws InternalErrorException the internal error exception
	 */
	public List<WorkspaceItem> searchByProperties(List<String> properties)
			throws InternalErrorException;


	/**
	 * Get the disk usage of a worskpace.
	 *
	 * @return the disk usage
	 * @throws InternalErrorException the internal error exception
	 */
	public long getDiskUsage() throws InternalErrorException;

	/**
	 * Get the total number of items in a workspace.
	 *
	 * @return the numer of total Items
	 * @throws InternalErrorException the internal error exception
	 */
	public int getTotalItems() throws InternalErrorException;


	/**
	 * Removes the items.
	 *
	 * @param id the id
	 * @return the map of errors (id,error)
	 * @throws ItemNotFoundException the item not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 */
	public Map<String, String> removeItems(String... id) throws ItemNotFoundException,
			InternalErrorException, InsufficientPrivilegesException;


	/**
	 * Create a VRE folder.
	 *
	 * @param scope the scope
	 * @param description the description
	 * @param displayName the display name
	 * @param privilege the privilege
	 * @return a new VRE folder
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public WorkspaceVREFolder createVREFolder(String scope, String description,
			String displayName, ACLType privilege) throws InternalErrorException,
			InsufficientPrivilegesException, ItemAlreadyExistException,
			WrongDestinationException, ItemNotFoundException,
			WorkspaceFolderNotFoundException;


	/**
	 * Get group name by group id.
	 *
	 * @param groupId the group id
	 * @return the group name
	 * @throws InternalErrorException the internal error exception
	 */
	String getGroup(String groupId) throws InternalErrorException;

	/**
	 * Check if a user is a group.
	 *
	 * @param groupId the group id
	 * @return true if the user is a group, false otherwise
	 * @throws InternalErrorException the internal error exception
	 */
	boolean isGroup(String groupId) throws InternalErrorException;

	/**
	 * Get public folders.
	 *
	 * @return a list of public folder
	 * @throws InternalErrorException the internal error exception
	 */
	List<WorkspaceItem> getPublicFolders() throws InternalErrorException;


	/**
	 * Upload archive.
	 *
	 * @param folderId the folder id
	 * @param is the is
	 * @param extractionFolderName the extraction folder name
	 * @return the workspace item
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws InternalErrorException the internal error exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 */
	WorkspaceItem uploadArchive(
		String folderId, InputStream is, String extractionFolderName)
		throws
		WorkspaceFolderNotFoundException, InternalErrorException,
		ItemAlreadyExistException, WrongDestinationException, InsufficientPrivilegesException;



	/**
	 * Download file.
	 *
	 * @param itemId the item id
	 * @param fileName the file name
	 * @param versionName the version name. If is null or empty returns the latest version of file
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the item stream descriptor
	 * @throws Exception the exception
	 */
	ItemStreamDescriptor downloadFile(String itemId, String fileName, String versionName, String nodeIdsToExclude) throws Exception;


	/**
	 * Download folder.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the item stream descriptor
	 * @throws Exception the exception
	 */
	ItemStreamDescriptor downloadFolder(
		String folderId, String folderName, String nodeIdsToExclude)
		throws Exception;


}
