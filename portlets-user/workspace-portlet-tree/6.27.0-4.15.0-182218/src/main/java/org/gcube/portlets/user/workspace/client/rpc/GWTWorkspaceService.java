package org.gcube.portlets.user.workspace.client.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.GcubeVRE;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceOperationResult;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface GWTWorkspaceService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 */
@RemoteServiceRelativePath("WorkspaceService")
public interface GWTWorkspaceService extends RemoteService {

	/**
	 * Gets the link for send to switch board.
	 *
	 * @param itemId
	 *            the item id
	 * @return the link for send to switch board
	 * @throws Exception
	 *             the exception
	 */
	String getLinkForSendToSwitchBoard(String itemId) throws Exception;

	/**
	 * Gets the servlet context path.
	 *
	 * @param protocol
	 *            the protocol
	 * @return the servlet context path
	 */
	String getServletContextPath(String protocol);

	/**
	 * Gets the user workspace size.
	 *
	 * @return the user workspace size
	 * @throws Exception
	 *             the exception
	 */
	String getUserWorkspaceSize() throws Exception;

	/**
	 * Gets the root for tree.
	 *
	 * @return the root for tree
	 * @throws Exception
	 *             the exception
	 */
	public FolderModel getRootForTree() throws Exception;

	/**
	 * Gets the folder children.
	 *
	 * @param folder
	 *            the folder
	 * @return the folder children
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exception
	 */
	public List<FileModel> getFolderChildren(FolderModel folder) throws Exception, SessionExpiredException;

	/**
	 * Gets the folder children for file grid.
	 *
	 * @param folder
	 *            the folder
	 * @return the folder children for file grid
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exception
	 */
	public List<FileGridModel> getFolderChildrenForFileGrid(FileModel folder) throws Exception, SessionExpiredException;


	/**
	 * Creates the folder.
	 *
	 * @param nameFolder
	 *            the name folder
	 * @param description
	 *            the description
	 * @param parent
	 *            the parent
	 * @return the folder model
	 * @throws Exception
	 *             the exception
	 */
	public FolderModel createFolder(String nameFolder, String description, FileModel parent) throws Exception;

	/**
	 * Gets the children sub tree to root by identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @return the children sub tree to root by identifier
	 * @throws Exception
	 *             the exception
	 */
	public ArrayList<SubTree> getChildrenSubTreeToRootByIdentifier(String itemIdentifier) throws Exception;

//	/**
//	 * Gets the smart folder results by category.
//	 *
//	 * @param category
//	 *            the category
//	 * @return the smart folder results by category
//	 * @throws Exception
//	 *             the exception
//	 */
//	List<FileGridModel> getSmartFolderResultsByCategory(GXTCategorySmartFolder category) throws Exception;

//	/**
//	 * Creates the smart folder.
//	 *
//	 * @param name
//	 *            the name
//	 * @param description
//	 *            the description
//	 * @param query
//	 *            the query
//	 * @param parentId
//	 *            the parent id
//	 * @return the smart folder model
//	 * @throws Exception
//	 *             the exception
//	 */
//	SmartFolderModel createSmartFolder(String name, String description, String query, String parentId) throws Exception;

//	/**
//	 * Gets the smart folder results by id.
//	 *
//	 * @param folderId
//	 *            the folder id
//	 * @return the smart folder results by id
//	 * @throws Exception
//	 *             the exception
//	 */
//	public List<FileGridModel> getSmartFolderResultsById(String folderId) throws Exception;

//	/**
//	 * Gets the all smart folders.
//	 *
//	 * @return the all smart folders
//	 * @throws Exception
//	 *             the exception
//	 */
//	public List<SmartFolderModel> getAllSmartFolders() throws Exception;

	/**
	 * Gets the image by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param isInteralImage
	 *            the is interal image
	 * @param fullDetails
	 *            the full details
	 * @return the image by id
	 * @throws Exception
	 *             the exception
	 */
	public GWTWorkspaceItem getImageById(String identifier, boolean isInteralImage, boolean fullDetails)
			throws Exception;

	/**
	 * Gets the url by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param isInternalUrl
	 *            the is internal url
	 * @param fullDetails
	 *            the full details
	 * @return the url by id
	 * @throws Exception
	 *             the exception
	 */
	public GWTWorkspaceItem getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails) throws Exception;

	/**
	 * Creates the external url.
	 *
	 * @param parentId
	 *            the parent id
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param url
	 *            the url
	 * @return the file model
	 * @throws Exception
	 *             the exception
	 */
	public FileModel createExternalUrl(String parentId, String name, String description, String url) throws Exception;

	/**
	 * Sets the value in session.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	public void setValueInSession(String name, String value) throws Exception;

	/**
	 * Gets the items by search name.
	 *
	 * @param text
	 *            the text
	 * @param folderId
	 *            the folder id
	 * @return the items by search name
	 * @throws Exception
	 *             the exception
	 */
	List<FileGridModel> getItemsBySearchName(String text, String folderId) throws Exception;

	/**
	 * Delete item.
	 *
	 * @param itemId
	 *            the item id
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	public Boolean deleteItem(String itemId) throws Exception;

	/**
	 * Rename item.
	 *
	 * @param itemId
	 *            the item id
	 * @param newName
	 *            the new name
	 * @param oldName
	 *            the old name
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	public Boolean renameItem(String itemId, String newName, String oldName) throws Exception;
	
	/**

	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param includeItemAsParent
	 *            the include item as parent
	 * @return the list parents by item identifier
	 * @throws Exception
	 *             the exception
	 */
	public List<FileModel> getListParentsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent)
			throws Exception;


	/**
	 * Item exists in workpace folder.
	 *
	 * @param parentId
	 *            the parent id
	 * @param itemName
	 *            the item name
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception;

	/**
	 * Gets the item creation date by id.
	 *
	 * @param itemId
	 *            the item id
	 * @return the item creation date by id
	 * @throws Exception
	 *             the exception
	 */
	public Date getItemCreationDateById(String itemId) throws Exception;

	/**
	 * Load size by item id.
	 *
	 * @param itemId
	 *            the item id
	 * @return the long
	 * @throws Exception
	 *             the exception
	 */
	public Long loadSizeByItemId(String itemId) throws Exception;

	/**
	 * Load last modification date by id.
	 *
	 * @param itemId
	 *            the item id
	 * @return the date
	 * @throws Exception
	 *             the exception
	 */
	public Date loadLastModificationDateById(String itemId) throws Exception;

	/**
	 * Gets the parent by item id.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the parent by item id
	 * @throws Exception
	 *             the exception
	 */
	public FileModel getParentByItemId(String identifier) throws Exception;

	/**
	 * Gets the accounting readers.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the accounting readers
	 * @throws Exception
	 *             the exception
	 */
	public List<GxtAccountingField> getAccountingReaders(String identifier) throws Exception;

	/**
	 * Gets the accounting history.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the accounting history
	 * @throws Exception
	 *             the exception
	 */
	public List<GxtAccountingField> getAccountingHistory(String identifier) throws Exception;

	/**
	 * Gets the item for file grid.
	 *
	 * @param itemId
	 *            the item id
	 * @return the item for file grid
	 * @throws Exception
	 *             the exception
	 */
	public FileGridModel getItemForFileGrid(String itemId) throws Exception;

	/**
	 * Gets the folder children for file grid by id.
	 *
	 * @param folderId
	 *            the folder id
	 * @return the folder children for file grid by id
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exception
	 */
	List<FileGridModel> getFolderChildrenForFileGridById(String folderId) throws Exception, SessionExpiredException;

	/**
	 * Gets the short url.
	 *
	 * @param longUrl
	 *            the long url
	 * @return the short url
	 * @throws Exception
	 *             the exception
	 */
	String getShortUrl(String longUrl) throws Exception;

	/**
	 * Gets the public link for file item id.
	 *
	 * @param itemId
	 *            the item id
	 * @param shortenUrl
	 *            the shorten url
	 * @return the public link for file item id
	 * @throws Exception
	 *             the exception
	 */
	PublicLink getPublicLinkForFileItemId(String itemId, boolean shortenUrl) throws Exception;

	/**
	 * Checks if is session expired.
	 *
	 * @return true, if is session expired
	 * @throws Exception
	 *             the exception
	 */
	boolean isSessionExpired() throws Exception;

	/**
	 * Delete list items for ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	List<GarbageItem> deleteListItemsForIds(List<String> ids) throws Exception;

	/**
	 * Copy items.
	 *
	 * @param idsItem
	 *            the ids item
	 * @param destinationFolderId
	 *            the destination folder id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	WorkspaceOperationResult copyItems(List<String> idsItem, String destinationFolderId) throws Exception;

	/**
	 * Move items.
	 *
	 * @param ids
	 *            the ids
	 * @param destinationId
	 *            the destination id
	 * @return the workpace operation result
	 * @throws Exception
	 *             the exception
	 */
	WorkspaceOperationResult moveItems(List<String> ids, String destinationId) throws Exception;

	/**
	 * Gets the my login.
	 *
	 * @param currentPortletUrl
	 *            the current portlet url
	 * @return the my login
	 */
	UserBean getMyLogin(String currentPortletUrl);

	/**
	 * Gets the trash content.
	 *
	 * @return the trash content
	 * @throws Exception
	 *             the exception
	 */
	List<FileTrashedModel> getTrashContent() throws Exception;

	/**
	 * Update trash content.
	 *
	 * @param operation
	 *            the operation
	 * @return the trash content
	 * @throws Exception
	 *             the exception
	 */
	TrashContent updateTrashContent(WorkspaceTrashOperation operation) throws Exception;

	/**
	 * Execute operation on trash.
	 *
	 * @param listTrashItemIds
	 *            the list trash item ids
	 * @param operation
	 *            the operation
	 * @return the trash operation content
	 * @throws Exception
	 *             the exception
	 */
	TrashOperationContent executeOperationOnTrash(List<String> listTrashItemIds, WorkspaceTrashOperation operation)
			throws Exception;

	
	/**
	 * Gets the user workspace total items.
	 *
	 * @return the user workspace total items
	 * @throws Exception
	 *             the exception
	 */
	long getUserWorkspaceTotalItems() throws Exception;

	/**
	 * Gets the user workspace quote.
	 *
	 * @return the user workspace quote
	 * @throws Exception
	 *             the exception
	 */
	WorkspaceUserQuote getUserWorkspaceQuote() throws Exception;

	/**
	 * Gets the item description by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the item description by id
	 * @throws Exception
	 *             the exception
	 */
	String getItemDescriptionById(String identifier) throws Exception;

	/**
	 * Load gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	Map<String, String> loadGcubeItemProperties(String itemId) throws Exception;

	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @return the HTML gcube item properties
	 * @throws Exception
	 *             the exception
	 */
	String getHTMLGcubeItemProperties(String itemId) throws Exception;

	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @param properties
	 *            the properties
	 * @throws Exception
	 *             the exception
	 */
	void setGcubeItemProperties(String itemId, Map<String, String> properties) throws Exception;

	/**
	 * Gets the my first name.
	 *
	 * @return the my first name
	 */
	String getMyFirstName();

	/**
	 * Mark folder as public for folder item id.
	 *
	 * @param itemId
	 *            the item id
	 * @param b
	 *            the b
	 * @return the public link
	 * @throws SessionExpiredException
	 *             the session expired exception
	 * @throws Exception
	 *             the exception
	 */
	PublicLink markFolderAsPublicForFolderItemId(String itemId, boolean b) throws SessionExpiredException, Exception;

	/**
	 * Perform operation on versioned file.
	 *
	 * @param fileId
	 *            the file id
	 * @param olderVersionIDs
	 *            the older version i ds
	 * @param operation
	 *            the operation
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	List<FileVersionModel> performOperationOnVersionedFile(String fileId, List<String> olderVersionIDs,
			WorkspaceVersioningOperation operation) throws Exception;

	/**
	 * Gets the version history.
	 *
	 * @param fileIdentifier
	 *            the file identifier
	 * @return the version history
	 * @throws Exception
	 *             the exception
	 */
	List<FileVersionModel> getVersionHistory(String fileIdentifier) throws Exception;

	/**
	 * Gets the images for folder.
	 *
	 * @param folderId
	 *            the folder id
	 * @param currentImageId
	 *            the current image id
	 * @return the images for folder
	 * @throws Exception
	 *             the exception
	 */
	List<GWTWorkspaceItem> getImagesForFolder(String folderId, String currentImageId) throws Exception;

	/**
	 * Gets the list of vr es for logged user.
	 *
	 * @return the list of vr es for logged user
	 * @throws Exception
	 *             the exception
	 */
	List<GcubeVRE> getListOfVREsForLoggedUser() throws Exception;

	/**
	 * Checks if is item under sync.
	 *
	 * @param itemId
	 *            the item id
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	Boolean isItemUnderSync(String itemId) throws Exception;

	/**
	 * Gets the public link for file item id to version.
	 *
	 * @param itemId
	 *            the item id
	 * @param version
	 *            the version
	 * @param shortenUrl
	 *            the shorten url
	 * @return the public link for file item id to version
	 * @throws Exception
	 *             Error
	 */
	PublicLink getPublicLinkForFileItemIdToVersion(String itemId, String version, boolean shortenUrl) throws Exception;

	
	/**
	 * Gets the item for file tree.
	 *
	 * @param itemId the item id
	 * @return the item for file tree
	 * @throws Exception the exception
	 */
	FileModel getItemForFileTree(String itemId) throws Exception;

}
