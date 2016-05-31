package org.gcube.portlets.user.workspace.client.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.model.FileDetailsModel;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.ReportAssignmentACL;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface GWTWorkspaceServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 14, 2015
 */
public interface GWTWorkspaceServiceAsync {

	/**
	 * Gets the user workspace size.
	 *
	 * @param callback the callback
	 * @return the user workspace size
	 */
	void getUserWorkspaceSize(AsyncCallback<String> callback);

	/**
	 * Gets the root for tree.
	 *
	 * @param callback the callback
	 * @return the root for tree
	 */
	void getRootForTree(AsyncCallback<FolderModel> callback);

	/**
	 * Gets the root for tree.
	 *
	 * @param scopeId the scope id
	 * @param callback the callback
	 * @return the root for tree
	 */
	void getRootForTree(String scopeId, AsyncCallback<FolderModel> callback);

	/**
	 * Gets the folder children.
	 *
	 * @param folder the folder
	 * @param callback the callback
	 * @return the folder children
	 */
	void getFolderChildren(FolderModel folder, AsyncCallback<List<FileModel>> callback);

	/**
	 * Gets the folder children for file grid.
	 *
	 * @param folder the folder
	 * @param callback the callback
	 * @return the folder children for file grid
	 */
	void getFolderChildrenForFileGrid(FileModel folder, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Move item.
	 *
	 * @param itemId the item id
	 * @param destinationId the destination id
	 * @param callback the callback
	 */
	void moveItem(String itemId, String destinationId, AsyncCallback<Boolean> callback);

	/**
	 * Removes the item.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 */
	void removeItem(String itemId, AsyncCallback<Boolean> callback);

	/**
	 * Rename item.
	 *
	 * @param itemId the item id
	 * @param newName the new name
	 * @param oldName the old name
	 * @param callback the callback
	 */
	void renameItem(String itemId, String newName, String oldName, AsyncCallback<Boolean> callback);

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder the name folder
	 * @param description the description
	 * @param parent the parent
	 * @param callback the callback
	 */
	void createFolder(String nameFolder, String description, FileModel parent, AsyncCallback<FolderModel> callback);

	/**
	 * Gets the details file.
	 *
	 * @param item the item
	 * @param callback the callback
	 * @return the details file
	 */
	void getDetailsFile(FileModel item, AsyncCallback<FileDetailsModel> callback);

	/**
	 * Gets the children sub tree to root by identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param callback the callback
	 * @return the children sub tree to root by identifier
	 */
	void getChildrenSubTreeToRootByIdentifier(String itemIdentifier, AsyncCallback<ArrayList<SubTree>> callback);

	/**
	 * Gets the items by search name.
	 *
	 * @param text the text
	 * @param callback the callback
	 * @return the items by search name
	 */
	void getItemsBySearchName(String text, String folderId, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the smart folder results by category.
	 *
	 * @param category the category
	 * @param callback the callback
	 * @return the smart folder results by category
	 */
	void getSmartFolderResultsByCategory(String category, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Creates the smart folder.
	 *
	 * @param name the name
	 * @param description the description
	 * @param query the query
	 * @param callback the callback
	 */
	void createSmartFolder(String name, String description, String query, String parentId, AsyncCallback<SmartFolderModel> callback);

	/**
	 * Gets the smart folder results by id.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 * @return the smart folder results by id
	 */
	void getSmartFolderResultsById(String folderId, AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the all smart folders.
	 *
	 * @param callback the callback
	 * @return the all smart folders
	 */
	void getAllSmartFolders(AsyncCallback<List<SmartFolderModel>> callback);

	/**
	 * Gets the image by id.
	 *
	 * @param identifier the identifier
	 * @param isInteralImage the is interal image
	 * @param fullDetails the full details
	 * @param callback the callback
	 * @return the image by id
	 */
	void getImageById(String identifier, boolean isInteralImage, boolean fullDetails, AsyncCallback<GWTWorkspaceItem> callback);

	/**
	 * Gets the url by id.
	 *
	 * @param identifier the identifier
	 * @param isInternalUrl the is internal url
	 * @param fullDetails the full details
	 * @param callback the callback
	 * @return the url by id
	 */
	void getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails, AsyncCallback<GWTWorkspaceItem> callback);

	/**
	 * Creates the external url.
	 *
	 * @param parentFileModel the parent file model
	 * @param name the name
	 * @param description the description
	 * @param url the url
	 * @param callback the callback
	 */
	void createExternalUrl(FileModel parentFileModel, String name, String description, String url, AsyncCallback<FileModel> callback);

	/**
	 * Sets the value in session.
	 *
	 * @param name the name
	 * @param value the value
	 * @param callback the callback
	 */
	void setValueInSession(String name, String value, AsyncCallback<Void> callback);

	/**
	 * Removes the smart folder.
	 *
	 * @param itemId the item id
	 * @param name the name
	 * @param callback the callback
	 */
	void removeSmartFolder(String itemId, String name, AsyncCallback<Boolean> callback);

	/**
	 * Gets the all scope.
	 *
	 * @param callback the callback
	 * @return the all scope
	 */
	void getAllScope(AsyncCallback<List<ScopeModel>> callback);

	/**
	 * Gets the all contacts.
	 *
	 * @param callback the callback
	 * @return the all contacts
	 */
	void getAllContacts(AsyncCallback<List<InfoContactModel>> callback);

//	void sendTo(List<InfoContactModel> listContacts, List<FileModel> listAttachments, String subject, String text, AsyncCallback<Boolean> callback);

	/**
	 * Send to by id.
	 *
	 * @param listContactsId the list contacts id
	 * @param listAttachmentsId the list attachments id
	 * @param subject the subject
	 * @param text the text
	 * @param callback the callback
	 */
	void sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String text, AsyncCallback<Boolean> callback);

	/**
	 * Copy item.
	 *
	 * @param itemId the item id
	 * @param destinationFolderId the destination folder id
	 * @param callback the callback
	 */
	void copyItem(String itemId, String destinationFolderId, AsyncCallback<Boolean> callback);

	/**
	 * Gets the url web dav.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the url web dav
	 */
	void getUrlWebDav(String itemId, AsyncCallback<String> callback);

	/**
	 * Gets the time series by id.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the time series by id
	 */
	void getTimeSeriesById(String identifier,
			AsyncCallback<GWTWorkspaceItem> callback);

	/**
	 * Share folder.
	 *
	 * @param folder the folder
	 * @param listContacts the list contacts
	 * @param isNewFolder the is new folder
	 * @param acl the acl
	 * @param callback the callback
	 */
	void shareFolder(FileModel folder, List<InfoContactModel> listContacts,
			boolean isNewFolder, WorkspaceACL acl,
			AsyncCallback<Boolean> callback);

	/**
	 * Gets the list user shared by folder shared id.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the list user shared by folder shared id
	 */
	void getListUserSharedByFolderSharedId(String itemId,
			AsyncCallback<List<InfoContactModel>> callback);

	/**
	 * Un shared folder by folder shared id.
	 *
	 * @param folderSharedId the folder shared id
	 * @param callback the callback
	 */
	void unSharedFolderByFolderSharedId(String folderSharedId,
			AsyncCallback<Boolean> callback);

	/**
	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 * @param callback the callback
	 * @return the list parents by item identifier
	 */
	void getListParentsByItemIdentifier(String itemIdentifier,
			boolean includeItemAsParent, AsyncCallback<List<FileModel>> callback);

	/**
	 * Gets the URL from application profile.
	 *
	 * @param oid the oid
	 * @param callback the callback
	 * @return the URL from application profile
	 */
	void getURLFromApplicationProfile(String oid, AsyncCallback<String> callback);

	/**
	 * Gets the owner by item id.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the owner by item id
	 */
	void getOwnerByItemId(String itemId,
			AsyncCallback<InfoContactModel> callback);

	/**
	 * Item exists in workpace folder.
	 *
	 * @param parentId the parent id
	 * @param itemName the item name
	 * @param callback the callback
	 */
	void itemExistsInWorkpaceFolder(String parentId, String itemName,
			AsyncCallback<String> callback);

	/**
	 * Gets the item creation date by id.
	 *
	 * @param itemId the item id
	 * @param asyncCallback the async callback
	 * @return the item creation date by id
	 */
	void getItemCreationDateById(String itemId,
			AsyncCallback<Date> asyncCallback);

	/**
	 * Load size by item id.
	 *
	 * @param itemId the item id
	 * @param asyncCallback the async callback
	 */
	void loadSizeByItemId(String itemId, AsyncCallback<Long> asyncCallback);

	/**
	 * Load last modification date by id.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 */
	void loadLastModificationDateById(String itemId,
			AsyncCallback<Date> callback);

	/**
	 * Gets the parent by item id.
	 *
	 * @param identifier the identifier
	 * @param asyncCallback the async callback
	 * @return the parent by item id
	 */
	void getParentByItemId(String identifier,
			AsyncCallback<FileModel> asyncCallback);

	/**
	 * Gets the accounting readers.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the accounting readers
	 */
	void getAccountingReaders(String identifier,
			AsyncCallback<List<GxtAccountingField>> callback);

	/**
	 * Gets the accounting history.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the accounting history
	 */
	void getAccountingHistory(String identifier,
			AsyncCallback<List<GxtAccountingField>> callback);

	/**
	 * Gets the item for file grid.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the item for file grid
	 */
	void getItemForFileGrid(String itemId, AsyncCallback<FileGridModel> callback);

	/**
	 * @param itemId
	 * @param asyncCallback
	 */
	void getItemForFileTree(String itemId,AsyncCallback<FileModel> asyncCallback);

	/**
	 * Gets the folder children for file grid by id.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 * @return the folder children for file grid by id
	 */
	void getFolderChildrenForFileGridById(String folderId,
			AsyncCallback<List<FileGridModel>> callback);

	/**
	 * Gets the short url.
	 *
	 * @param longUrl the long url
	 * @param callback the callback
	 * @return the short url
	 */
	void getShortUrl(String longUrl, AsyncCallback<String> callback);

	/**
	 * Gets the public link for folder item id.
	 *
	 * @param itemId the item id
	 * @param shortenUrl the shorten url
	 * @param callback the callback
	 * @return the public link for folder item id
	 */
	void getPublicLinkForFolderItemId(String itemId, boolean shortenUrl,
			AsyncCallback<PublicLink> callback);

	/**
	 * Checks if is session expired.
	 *
	 * @param callback the callback
	 */
	void isSessionExpired(AsyncCallback<Boolean> callback);

	void deleteListItemsForIds(List<String> ids,
			AsyncCallback<List<GarbageItem>> callback);

	/**
	 * Copy items.
	 *
	 * @param idsItem the ids item
	 * @param destinationFolderId the destination folder id
	 * @param callback the callback
	 */
	void copyItems(List<String> idsItem, String destinationFolderId,
			AsyncCallback<Boolean> callback);

	/**
	 * Move items.
	 *
	 * @param ids the ids
	 * @param destinationId the destination id
	 * @param callback the callback
	 */
	void moveItems(List<String> ids, String destinationId,
			AsyncCallback<Boolean> callback);

	/**
	 * Gets the AC ls.
	 *
	 * @param callback the callback
	 * @return the AC ls
	 */
	void getACLs(AsyncCallback<List<WorkspaceACL>> callback);

	/**
	 * Sets the ac ls.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @param callback the callback
	 */
	void setACLs(String folderId, List<String> listLogins, String aclType,
			AsyncCallback<Void> callback);

	/**
	 * Gets the my login.
	 *
	 * @param callback the callback
	 * @return the my login
	 */
	void getMyLogin(AsyncCallback<UserBean> callback);

	/**
	 * Update acl for vr eby group name.
	 *
	 * @param folderId the folder id
	 * @param aclType the acl type
	 * @param callback the callback
	 */
	void updateACLForVREbyGroupName(String folderId, String aclType,
			AsyncCallback<Void> callback);

	/**
	 * Gets the user acl for folder id.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 * @return the user acl for folder id
	 */
	void getUserACLForFolderId(String folderId,
			AsyncCallback<List<ExtendedWorkspaceACL>> callback);

	/**
	 * Gets the trash content.
	 *
	 * @param callback the callback
	 * @return the trash content
	 */
	void getTrashContent(AsyncCallback<List<FileTrashedModel>> callback);

	/**
	 * Update trash content.
	 *
	 * @param operation the operation
	 * @param callback the callback
	 */
	void updateTrashContent(WorkspaceTrashOperation operation,
			AsyncCallback<TrashContent> callback);

	/**
	 * Gets the AC ls description for workspace item by id.
	 *
	 * @param workspaceItemId the workspace item id
	 * @param callback the callback
	 * @return the AC ls description for workspace item by id
	 */
	void getACLsDescriptionForWorkspaceItemById(String workspaceItemId,
			AsyncCallback<String> callback);

	/**
	 * Gets the users manager to shared folder.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 * @return the users manager to shared folder
	 */
	void getUsersManagerToSharedFolder(String folderId,
			AsyncCallback<List<InfoContactModel>> callback);

	/**
	 * Execute operation on trash.
	 *
	 * @param listTrashItemIds the list trash item ids
	 * @param operation the operation
	 * @param callback the callback
	 */
	void executeOperationOnTrash(List<String> listTrashItemIds,
			WorkspaceTrashOperation operation,
			AsyncCallback<TrashOperationContent> callback);

	/**
	 * Adds the administrators by folder id.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param callback the callback
	 */
	void addAdministratorsByFolderId(String folderId, List<String> listLogins,
			AsyncCallback<Boolean> callback);

	/**
	 * Gets the administrators by folder id.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the administrators by folder id
	 */
	void getAdministratorsByFolderId(String identifier, AsyncCallback<List<InfoContactModel>> callback);


	/**
	 * Gets the ACL by shared folder id.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the ACL by shared folder id
	 */
	void getACLBySharedFolderId(String identifier, AsyncCallback<WorkspaceACL> callback);

	/**
	 * Gets the user workspace total items.
	 *
	 * @param callback the callback
	 * @return the user workspace total items
	 */
	void getUserWorkspaceTotalItems(AsyncCallback<Long> callback);

	/**
	 * Gets the user workspace quote.
	 *
	 * @param callback the callback
	 * @return the user workspace quote
	 */
	void getUserWorkspaceQuote(AsyncCallback<WorkspaceUserQuote> callback);

	/**
	 * Gets the item description by id.
	 *
	 * @param identifier the identifier
	 * @param callback the callback
	 * @return the item description by id
	 */
	void getItemDescriptionById(String identifier,
			AsyncCallback<String> callback);

	/**
	 * Validate acl to user.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @param callback the callback
	 */
	void validateACLToUser(String folderId, List<String> listLogins,
			String aclType, AsyncCallback<ReportAssignmentACL> callback);

	/**
	 * Load gcube item properties.
	 *
	 * @param itemId the item id
	 * @param asyncCallback the async callback
	 */
	void loadGcubeItemProperties(String itemId,
			AsyncCallback<Map<String, String>> asyncCallback);

	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the HTML gcube item properties
	 */
	void getHTMLGcubeItemProperties(String itemId,
			AsyncCallback<String> callback);

	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId the item id
	 * @param properties the properties
	 * @param callback the callback
	 */
	void setGcubeItemProperties(String itemId, Map<String, String> properties,
			AsyncCallback<Void> callback);



}
