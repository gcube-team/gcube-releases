/*
 *
 */
package org.gcube.portlets.user.workspace.client.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.model.FileDetailsModel;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.GcubeVRE;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceOperationResult;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface GWTWorkspaceServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 */
public interface GWTWorkspaceServiceAsync {

	/**
	 * Gets the user workspace size.
	 *
	 * @param callback
	 *            the callback
	 */
	void getUserWorkspaceSize(AsyncCallback<String> callback);

	/**
	 * Gets the root for tree.
	 *
	 * @param callback
	 *            the callback
	 */
	void getRootForTree(AsyncCallback<FolderModel> callback);

	/**
	 * Gets the folder children.
	 *
	 * @param folder
	 *            the folder
	 * @param callback
	 *            the callback
	 */
	void getFolderChildren(FolderModel folder, AsyncCallback<List<FileModel>> callback);

	/**
	 * Gets the folder children for file grid.
	 *
	 * @param folder
	 *            the folder
	 * @param callback
	 *            the callback
	 */
	void getFolderChildrenForFileGrid(FileModel folder, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Delete item.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void deleteItem(String itemId, AsyncCallback<Boolean> callback);

	/**
	 * Rename item.
	 *
	 * @param itemId
	 *            the item id
	 * @param newName
	 *            the new name
	 * @param oldName
	 *            the old name
	 * @param callback
	 *            the callback
	 */
	void renameItem(String itemId, String newName, String oldName, AsyncCallback<Boolean> callback);

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder
	 *            the name folder
	 * @param description
	 *            the description
	 * @param parent
	 *            the parent
	 * @param callback
	 *            the callback
	 */
	void createFolder(String nameFolder, String description, FileModel parent, AsyncCallback<FolderModel> callback);

	/**
	 * 
	 * @param item
	 *            the item
	 * @param callback
	 *            file details model
	 */
	void getDetailsFile(FileModel item, AsyncCallback<FileDetailsModel> callback);

	/**
	 * Gets the children sub tree to root by identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param callback
	 *            the callback
	 */
	void getChildrenSubTreeToRootByIdentifier(String itemIdentifier, AsyncCallback<ArrayList<SubTree>> callback);

	/**
	 * Gets the items by search name.
	 *
	 * @param text
	 *            the text
	 * @param folderId
	 *            the folder id
	 * @param callback
	 *            the callback
	 */
	void getItemsBySearchName(String text, String folderId, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the smart folder results by category.
	 *
	 * @param category
	 *            the category
	 * @param callback
	 *            the callback
	 */
	void getSmartFolderResultsByCategory(GXTCategorySmartFolder category, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Creates the smart folder.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param query
	 *            the query
	 * @param parentId
	 *            the parent id
	 * @param callback
	 *            the callback
	 */
	void createSmartFolder(String name, String description, String query, String parentId,
			AsyncCallback<SmartFolderModel> callback);

	/**
	 * Gets the smart folder results by id.
	 *
	 * @param folderId
	 *            the folder id
	 * @param callback
	 *            the callback
	 */
	void getSmartFolderResultsById(String folderId, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the all smart folders.
	 *
	 * @param callback
	 *            the callback
	 */
	void getAllSmartFolders(AsyncCallback<List<SmartFolderModel>> callback);

	/**
	 * Gets the image by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param isInteralImage
	 *            the is interal image
	 * @param fullDetails
	 *            the full details
	 * @param callback
	 *            the callback
	 */
	void getImageById(String identifier, boolean isInteralImage, boolean fullDetails,
			AsyncCallback<GWTWorkspaceItem> callback);

	/**
	 * Gets the url by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param isInternalUrl
	 *            the is internal url
	 * @param fullDetails
	 *            the full details
	 * @param callback
	 *            the callback
	 */
	void getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails,
			AsyncCallback<GWTWorkspaceItem> callback);

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
	 * @param callback
	 *            the callback
	 */
	void createExternalUrl(String parentId, String name, String description, String url,
			AsyncCallback<FileModel> callback);

	/**
	 * Sets the value in session.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @param callback
	 *            the callback
	 */
	void setValueInSession(String name, String value, AsyncCallback<Void> callback);

	/**
	 * Removes the smart folder.
	 *
	 * @param itemId
	 *            the item id
	 * @param name
	 *            the name
	 * @param callback
	 *            the callback
	 */
	void removeSmartFolder(String itemId, String name, AsyncCallback<Boolean> callback);

	/**
	 * Gets the all scope.
	 *
	 * @param callback
	 *            the callback
	 */
	void getAllScope(AsyncCallback<List<ScopeModel>> callback);

	/**
	 * Send to by id.
	 *
	 * @param listContactsId
	 *            the list contacts id
	 * @param listAttachmentsId
	 *            the list attachments id
	 * @param subject
	 *            the subject
	 * @param text
	 *            the text
	 * @param callback
	 *            the callback
	 */
	void sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String text,
			AsyncCallback<Boolean> callback);

	/**
	 * Gets the url web dav.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void getUrlWebDav(String itemId, AsyncCallback<String> callback);

	/**
	 * Gets the time series by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param callback
	 *            the callback
	 */
	void getTimeSeriesById(String identifier, AsyncCallback<GWTWorkspaceItem> callback);

	/**
	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param includeItemAsParent
	 *            the include item as parent
	 * @param callback
	 *            the callback
	 */
	void getListParentsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent,
			AsyncCallback<List<FileModel>> callback);

	/**
	 * Gets the URL from application profile.
	 *
	 * @param oid
	 *            the oid
	 * @param callback
	 *            the callback
	 */
	void getURLFromApplicationProfile(String oid, AsyncCallback<String> callback);

	/**
	 * Item exists in workpace folder.
	 *
	 * @param parentId
	 *            the parent id
	 * @param itemName
	 *            the item name
	 * @param callback
	 *            the callback
	 */
	void itemExistsInWorkpaceFolder(String parentId, String itemName, AsyncCallback<String> callback);

	/**
	 * Gets the item creation date by id.
	 *
	 * @param itemId
	 *            the item id
	 * @param asyncCallback
	 *            the async callback
	 */
	void getItemCreationDateById(String itemId, AsyncCallback<Date> asyncCallback);

	/**
	 * Load size by item id.
	 *
	 * @param itemId
	 *            the item id
	 * @param asyncCallback
	 *            the async callback
	 */
	void loadSizeByItemId(String itemId, AsyncCallback<Long> asyncCallback);

	/**
	 * Load last modification date by id.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void loadLastModificationDateById(String itemId, AsyncCallback<Date> callback);

	/**
	 * Gets the parent by item id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param asyncCallback
	 *            the async callback
	 */
	void getParentByItemId(String identifier, AsyncCallback<FileModel> asyncCallback);

	/**
	 * Gets the accounting readers.
	 *
	 * @param identifier
	 *            the identifier
	 * @param callback
	 *            the callback
	 */
	void getAccountingReaders(String identifier, AsyncCallback<List<GxtAccountingField>> callback);

	/**
	 * Gets the accounting history.
	 *
	 * @param identifier
	 *            the identifier
	 * @param callback
	 *            the callback
	 */
	void getAccountingHistory(String identifier, AsyncCallback<List<GxtAccountingField>> callback);

	/**
	 * Gets the item for file grid.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void getItemForFileGrid(String itemId, AsyncCallback<FileGridModel> callback);

	/**
	 * Gets the item for file tree.
	 *
	 * @param itemId
	 *            the item id
	 * @param asyncCallback
	 *            the async callback
	 */
	void getItemForFileTree(String itemId, AsyncCallback<FileModel> asyncCallback);

	/**
	 * Gets the folder children for file grid by id.
	 *
	 * @param folderId
	 *            the folder id
	 * @param callback
	 *            the callback
	 */
	void getFolderChildrenForFileGridById(String folderId, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the short url.
	 *
	 * @param longUrl
	 *            the long url
	 * @param callback
	 *            the callback
	 */
	void getShortUrl(String longUrl, AsyncCallback<String> callback);

	/**
	 * Gets the public link for file item id.
	 *
	 * @param itemId
	 *            the item id
	 * @param shortenUrl
	 *            the shorten url
	 * @param callback
	 *            the callback
	 */
	void getPublicLinkForFileItemId(String itemId, boolean shortenUrl, AsyncCallback<PublicLink> callback);

	/**
	 * Gets the public link for file item id to version.
	 *
	 * @param itemId
	 *            the item id
	 * @param version
	 *            the version
	 * @param shortenUrl
	 *            the shorten url
	 * @param callback
	 *            the callback
	 */
	void getPublicLinkForFileItemIdToVersion(String itemId, String version, boolean shortenUrl,
			AsyncCallback<PublicLink> callback);

	/**
	 * Checks if is session expired.
	 *
	 * @param callback
	 *            the callback
	 */
	void isSessionExpired(AsyncCallback<Boolean> callback);

	/**
	 * Delete list items for ids.
	 *
	 * @param ids
	 *            the ids
	 * @param callback
	 *            the callback
	 */
	void deleteListItemsForIds(List<String> ids, AsyncCallback<List<GarbageItem>> callback);

	/**
	 * Copy items.
	 *
	 * @param idsItem
	 *            the ids item
	 * @param destinationFolderId
	 *            the destination folder id
	 * @param callback
	 *            the callback
	 */
	void copyItems(List<String> idsItem, String destinationFolderId, AsyncCallback<WorkspaceOperationResult> callback);

	/**
	 * Move items.
	 *
	 * @param ids
	 *            the ids
	 * @param destinationId
	 *            the destination id
	 * @param callback
	 *            the callback
	 */
	void moveItems(List<String> ids, String destinationId, AsyncCallback<WorkspaceOperationResult> callback);

	/**
	 * Gets the my login.
	 *
	 * @param currentPortletUrl
	 *            the current portlet url
	 * @param callback
	 *            the callback
	 */
	void getMyLogin(String currentPortletUrl, AsyncCallback<UserBean> callback);

	/**
	 * Gets the trash content.
	 *
	 * @param callback
	 *            the callback
	 */
	void getTrashContent(AsyncCallback<List<FileTrashedModel>> callback);

	/**
	 * Update trash content.
	 *
	 * @param operation
	 *            the operation
	 * @param callback
	 *            the callback
	 */
	void updateTrashContent(WorkspaceTrashOperation operation, AsyncCallback<TrashContent> callback);

	/**
	 * Execute operation on trash.
	 *
	 * @param listTrashItemIds
	 *            the list trash item ids
	 * @param operation
	 *            the operation
	 * @param callback
	 *            the callback
	 */
	void executeOperationOnTrash(List<String> listTrashItemIds, WorkspaceTrashOperation operation,
			AsyncCallback<TrashOperationContent> callback);

	/**
	 * Gets the user workspace total items.
	 *
	 * @param callback
	 *            the callback
	 */
	void getUserWorkspaceTotalItems(AsyncCallback<Long> callback);

	/**
	 * Gets the user workspace quote.
	 *
	 * @param callback
	 *            the callback
	 */
	void getUserWorkspaceQuote(AsyncCallback<WorkspaceUserQuote> callback);

	/**
	 * Gets the item description by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param callback
	 *            the callback
	 */
	void getItemDescriptionById(String identifier, AsyncCallback<String> callback);

	/**
	 * Load gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @param asyncCallback
	 *            the async callback
	 */
	void loadGcubeItemProperties(String itemId, AsyncCallback<Map<String, String>> asyncCallback);

	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void getHTMLGcubeItemProperties(String itemId, AsyncCallback<String> callback);

	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @param properties
	 *            the properties
	 * @param callback
	 *            the callback
	 */
	void setGcubeItemProperties(String itemId, Map<String, String> properties, AsyncCallback<Void> callback);

	/**
	 * Gets the my first name.
	 *
	 * @param callback
	 *            the callback
	 */
	void getMyFirstName(AsyncCallback<String> callback);

	/**
	 * Mark folder as public for folder item id. return the PublicLink in case
	 * of setPublic is true, null otherwise
	 * 
	 * @param itemId
	 *            the item id
	 * @param setPublic
	 *            the set public
	 * @param callback
	 *            the callback
	 */
	void markFolderAsPublicForFolderItemId(String itemId, boolean setPublic, AsyncCallback<PublicLink> callback);

	/**
	 * Gets the servlet context path.
	 *
	 * @param protocol
	 *            the protocol
	 * @param callback
	 *            the callback
	 */
	void getServletContextPath(String protocol, AsyncCallback<String> callback);

	/**
	 * Gets the version history.
	 *
	 * @param fileIdentifier
	 *            the file identifier
	 * @param callback
	 *            the callback
	 */
	void getVersionHistory(String fileIdentifier, AsyncCallback<List<FileVersionModel>> callback);

	/**
	 * Perform operation on versioned file.
	 *
	 * @param fileId
	 *            the file id
	 * @param olderVersionIds
	 *            the older version ids
	 * @param operation
	 *            the operation
	 * @param callback
	 *            the callback
	 */
	void performOperationOnVersionedFile(String fileId, List<String> olderVersionIds,
			WorkspaceVersioningOperation operation, AsyncCallback<List<FileVersionModel>> callback);

	/**
	 * Gets the images for folder.
	 *
	 * @param folderId
	 *            the folder id
	 * @param currentImageId
	 *            the current image id
	 * @param asyncCallback
	 *            the async callback
	 */
	void getImagesForFolder(String folderId, String currentImageId,
			AsyncCallback<List<GWTWorkspaceItem>> asyncCallback);

	/**
	 * Gets the list of vr es for logged user.
	 *
	 * @param callback
	 *            the callback
	 */
	void getListOfVREsForLoggedUser(AsyncCallback<List<GcubeVRE>> callback);

	/**
	 * Checks if is item under sync.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void isItemUnderSync(String itemId, AsyncCallback<Boolean> callback);

	/**
	 * Gets the link for send to switch board.
	 *
	 * @param itemId
	 *            the item id
	 * @param callback
	 *            the callback
	 */
	void getLinkForSendToSwitchBoard(String itemId, AsyncCallback<String> callback);

}
