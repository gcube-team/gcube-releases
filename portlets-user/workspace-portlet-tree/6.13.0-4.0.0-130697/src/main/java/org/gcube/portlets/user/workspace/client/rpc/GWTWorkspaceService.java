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
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface GWTWorkspaceService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 14, 2015
 */
@RemoteServiceRelativePath("WorkspaceService")
public interface GWTWorkspaceService extends RemoteService{

	/**
	 * Gets the user workspace size.
	 *
	 * @return the user workspace size
	 * @throws Exception the exception
	 */
	String getUserWorkspaceSize() throws Exception;

	/**
	 * Gets the root for tree.
	 *
	 * @return the root for tree
	 * @throws Exception the exception
	 */
	public FolderModel getRootForTree() throws Exception;

	/**
	 * Gets the root for tree.
	 *
	 * @param scopeId the scope id
	 * @return the root for tree
	 * @throws Exception the exception
	 */
	public FolderModel getRootForTree(String scopeId) throws Exception;

	/**
	 * Gets the all scope.
	 *
	 * @return the all scope
	 * @throws Exception the exception
	 */
	public List<ScopeModel> getAllScope() throws Exception;

	/**
	 * Gets the folder children.
	 *
	 * @param folder the folder
	 * @return the folder children
	 * @throws Exception the exception
	 * @throws SessionExpiredException the session expired exception
	 */
	public List<FileModel> getFolderChildren(FolderModel folder) throws Exception, SessionExpiredException;

	/**
	 * Gets the folder children for file grid.
	 *
	 * @param folder the folder
	 * @return the folder children for file grid
	 * @throws Exception the exception
	 * @throws SessionExpiredException the session expired exception
	 */
	public List<FileGridModel> getFolderChildrenForFileGrid(FileModel folder) throws Exception, SessionExpiredException;

	/**
	 * Gets the details file.
	 *
	 * @param item the item
	 * @return the details file
	 * @throws Exception the exception
	 */
	public FileDetailsModel getDetailsFile(FileModel item) throws Exception;

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder the name folder
	 * @param description the description
	 * @param parent the parent
	 * @return the folder model
	 * @throws Exception the exception
	 */
	public FolderModel createFolder(String nameFolder, String description, FileModel parent) throws Exception;

	/**
	 * Gets the children sub tree to root by identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @return the children sub tree to root by identifier
	 * @throws Exception the exception
	 */
	public ArrayList<SubTree> getChildrenSubTreeToRootByIdentifier(String itemIdentifier) throws Exception;

	/**
	 * Gets the smart folder results by category.
	 *
	 * @param category the category
	 * @return the smart folder results by category
	 * @throws Exception the exception
	 */
	public List<FileGridModel> getSmartFolderResultsByCategory(String category) throws Exception;

	/**
	 * Creates the smart folder.
	 *
	 * @param name the name
	 * @param description the description
	 * @param query the query
	 * @param parentId the parent id
	 * @return the smart folder model
	 * @throws Exception the exception
	 */
	SmartFolderModel createSmartFolder(String name, String description,
			String query, String parentId) throws Exception;

	/**
	 * Gets the smart folder results by id.
	 *
	 * @param folderId the folder id
	 * @return the smart folder results by id
	 * @throws Exception the exception
	 */
	public List<FileGridModel> getSmartFolderResultsById(String folderId) throws Exception;

	/**
	 * Gets the all smart folders.
	 *
	 * @return the all smart folders
	 * @throws Exception the exception
	 */
	public List<SmartFolderModel> getAllSmartFolders() throws Exception;

	/**
	 * Gets the image by id.
	 *
	 * @param identifier the identifier
	 * @param isInteralImage the is interal image
	 * @param fullDetails the full details
	 * @return the image by id
	 * @throws Exception the exception
	 */
	public GWTWorkspaceItem getImageById(String identifier, boolean isInteralImage, boolean fullDetails) throws Exception;

	/**
	 * Gets the url by id.
	 *
	 * @param identifier the identifier
	 * @param isInternalUrl the is internal url
	 * @param fullDetails the full details
	 * @return the url by id
	 * @throws Exception the exception
	 */
	public GWTWorkspaceItem getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails) throws Exception;

	/**
	 * Creates the external url.
	 *
	 * @param parentFileModel the parent file model
	 * @param name the name
	 * @param description the description
	 * @param url the url
	 * @return the file model
	 * @throws Exception the exception
	 */
	public FileModel createExternalUrl(FileModel parentFileModel, String name, String description, String url) throws Exception;

	/**
	 * Sets the value in session.
	 *
	 * @param name the name
	 * @param value the value
	 * @throws Exception the exception
	 */
	public void setValueInSession(String name, String value) throws Exception;

	/**
	 * Gets the items by search name.
	 *
	 * @param text the text
	 * @param folderId the folder id
	 * @return the items by search name
	 * @throws Exception the exception
	 */
	List<FileGridModel> getItemsBySearchName(String text, String folderId) throws Exception;

	/**
	 * Move item.
	 *
	 * @param itemId the item id
	 * @param destinationId the destination id
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean moveItem(String itemId, String destinationId) throws Exception;

	/**
	 * Removes the item.
	 *
	 * @param itemId the item id
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean removeItem(String itemId) throws Exception;

	/**
	 * Rename item.
	 *
	 * @param itemId the item id
	 * @param newName the new name
	 * @param oldName the old name
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean renameItem(String itemId, String newName, String oldName) throws Exception;

	/**
	 * Removes the smart folder.
	 *
	 * @param itemId the item id
	 * @param name the name
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean removeSmartFolder(String itemId, String name) throws Exception;

	/**
	 * Gets the all contacts.
	 *
	 * @return the all contacts
	 * @throws Exception the exception
	 */
	public List<InfoContactModel> getAllContacts() throws Exception;

	/**
	 * Gets the url web dav.
	 *
	 * @param itemId the item id
	 * @return the url web dav
	 * @throws Exception the exception
	 */
	public String getUrlWebDav(String itemId) throws Exception;

	/**
	 * Send to by id.
	 *
	 * @param listContactsId the list contacts id
	 * @param listAttachmentsId the list attachments id
	 * @param subject the subject
	 * @param text the text
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String text) throws Exception;

	/**
	 * Copy item.
	 *
	 * @param itemId the item id
	 * @param destinationFolderId the destination folder id
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean copyItem(String itemId, String destinationFolderId) throws Exception;

	/**
	 * Gets the time series by id.
	 *
	 * @param identifier the identifier
	 * @return the time series by id
	 * @throws Exception the exception
	 */
	public GWTWorkspaceItem getTimeSeriesById(String identifier) throws Exception;

	/**
	 * Share folder.
	 *
	 * @param folder the folder
	 * @param listContacts the list contacts
	 * @param isNewFolder the is new folder
	 * @param acl the acl
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean shareFolder(FileModel folder, List<InfoContactModel> listContacts,
			boolean isNewFolder, WorkspaceACL acl) throws Exception;

	/**
	 * Gets the list user shared by folder shared id.
	 *
	 * @param itemId the item id
	 * @return the list user shared by folder shared id
	 * @throws Exception the exception
	 */
	public List<InfoContactModel> getListUserSharedByFolderSharedId(String itemId) throws Exception;

	/**
	 * Un shared folder by folder shared id.
	 *
	 * @param folderSharedId the folder shared id
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean unSharedFolderByFolderSharedId(String folderSharedId) throws Exception;

	/**
	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 * @return the list parents by item identifier
	 * @throws Exception the exception
	 */
	public List<FileModel> getListParentsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent) throws Exception;

	/**
	 * Gets the URL from application profile.
	 *
	 * @param oid the oid
	 * @return the URL from application profile
	 * @throws Exception the exception
	 */
	public String getURLFromApplicationProfile(String oid) throws Exception;

	/**
	 * Gets the owner by item id.
	 *
	 * @param itemId the item id
	 * @return the owner by item id
	 * @throws Exception the exception
	 */
	public InfoContactModel getOwnerByItemId(String itemId) throws Exception;

	/**
	 * Item exists in workpace folder.
	 *
	 * @param parentId the parent id
	 * @param itemName the item name
	 * @return the string
	 * @throws Exception the exception
	 */
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception;

	/**
	 * Gets the item creation date by id.
	 *
	 * @param itemId the item id
	 * @return the item creation date by id
	 * @throws Exception the exception
	 */
	public Date getItemCreationDateById(String itemId) throws Exception;

	/**
	 * Load size by item id.
	 *
	 * @param itemId the item id
	 * @return the long
	 * @throws Exception the exception
	 */
	public Long loadSizeByItemId(String itemId) throws Exception;

	/**
	 * Load last modification date by id.
	 *
	 * @param itemId the item id
	 * @return the date
	 * @throws Exception the exception
	 */
	public Date loadLastModificationDateById(String itemId) throws Exception;

	/**
	 * Gets the parent by item id.
	 *
	 * @param identifier the identifier
	 * @return the parent by item id
	 * @throws Exception the exception
	 */
	public FileModel getParentByItemId(String identifier) throws Exception;

	/**
	 * Gets the accounting readers.
	 *
	 * @param identifier the identifier
	 * @return the accounting readers
	 * @throws Exception the exception
	 */
	public List<GxtAccountingField> getAccountingReaders(String identifier)
			throws Exception;

	/**
	 * Gets the accounting history.
	 *
	 * @param identifier the identifier
	 * @return the accounting history
	 * @throws Exception the exception
	 */
	public List<GxtAccountingField> getAccountingHistory(String identifier) throws Exception;

	/**
	 * Gets the item for file grid.
	 *
	 * @param itemId the item id
	 * @return the item for file grid
	 * @throws Exception the exception
	 */
	public FileGridModel getItemForFileGrid(String itemId) throws Exception;

	/**
	 * Gets the folder children for file grid by id.
	 *
	 * @param folderId the folder id
	 * @return the folder children for file grid by id
	 * @throws Exception the exception
	 * @throws SessionExpiredException the session expired exception
	 */
	List<FileGridModel> getFolderChildrenForFileGridById(String folderId) throws Exception, SessionExpiredException;

	/**
	 * Gets the short url.
	 *
	 * @param longUrl the long url
	 * @return the short url
	 * @throws Exception the exception
	 */
	String getShortUrl(String longUrl) throws Exception;

	/**
	 * Gets the public link for folder item id.
	 *
	 * @param itemId the item id
	 * @param shortenUrl the shorten url
	 * @return the public link for folder item id
	 * @throws Exception the exception
	 */
	PublicLink getPublicLinkForFolderItemId(String itemId, boolean shortenUrl)
			throws Exception;

	/**
	 * Checks if is session expired.
	 *
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	boolean isSessionExpired() throws Exception;




	/**
	 * Delete list items for ids.
	 *
	 * @param ids the ids
	 * @return the list
	 * @throws Exception the exception
	 */
	List<GarbageItem> deleteListItemsForIds(List<String> ids) throws Exception;

	/**
	 * Copy items.
	 *
	 * @param idsItem the ids item
	 * @param destinationFolderId the destination folder id
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean copyItems(List<String> idsItem, String destinationFolderId)
			throws Exception;

	/**
	 * Move items.
	 *
	 * @param ids the ids
	 * @param destinationId the destination id
	 * @return the boolean
	 * @throws Exception the exception
	 */
	Boolean moveItems(List<String> ids, String destinationId) throws Exception;

	/**
	 * Gets the AC ls.
	 *
	 * @return the AC ls
	 * @throws Exception the exception
	 */
	List<WorkspaceACL> getACLs() throws Exception;

	/**
	 * Sets the ac ls.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @throws Exception the exception
	 */
	void setACLs(String folderId, List<String> listLogins, String aclType)
			throws Exception;

	/**
	 * Gets the my login.
	 *
	 * @return the my login
	 */
	UserBean getMyLogin();

	/**
	 * Update acl for vr eby group name.
	 *
	 * @param folderId the folder id
	 * @param aclType the acl type
	 * @throws Exception the exception
	 */
	void updateACLForVREbyGroupName(String folderId, String aclType) throws Exception;

	/**
	 * Gets the user acl for folder id.
	 *
	 * @param folderId the folder id
	 * @return the user acl for folder id
	 * @throws Exception the exception
	 */
	List<ExtendedWorkspaceACL> getUserACLForFolderId(String folderId) throws Exception;

	/**
	 * Gets the trash content.
	 *
	 * @return the trash content
	 * @throws Exception the exception
	 */
	List<FileTrashedModel> getTrashContent() throws Exception;


	/**
	 * Gets the AC ls description for workspace item by id.
	 *
	 * @param workspaceItemId the workspace item id
	 * @return the AC ls description for workspace item by id
	 * @throws Exception the exception
	 */
	String getACLsDescriptionForWorkspaceItemById(String workspaceItemId) throws Exception;

	/**
	 * Gets the users manager to shared folder.
	 *
	 * @param folderId the folder id
	 * @return the users manager to shared folder
	 * @throws Exception the exception
	 */
	List<InfoContactModel> getUsersManagerToSharedFolder(String folderId)
			throws Exception;

	/**
	 * Update trash content.
	 *
	 * @param operation the operation
	 * @return the trash content
	 * @throws Exception the exception
	 */
	TrashContent updateTrashContent(WorkspaceTrashOperation operation) throws Exception;

	/**
	 * Execute operation on trash.
	 *
	 * @param listTrashItemIds the list trash item ids
	 * @param operation the operation
	 * @return the trash operation content
	 * @throws Exception the exception
	 */
	TrashOperationContent executeOperationOnTrash(List<String> listTrashItemIds,
			WorkspaceTrashOperation operation) throws Exception;

	/**
	 * Adds the administrators by folder id.
	 *
	 * @param folderId the folder id
	 * @param listContactIds the list contact ids
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean addAdministratorsByFolderId(String folderId,
			List<String> listContactIds) throws Exception;

	/**
	 * Gets the administrators by folder id.
	 *
	 * @param identifier the identifier
	 * @return the administrators by folder id
	 * @throws Exception the exception
	 */
	List<InfoContactModel> getAdministratorsByFolderId(String identifier) throws Exception;

	/**
	 * Gets the ACL by shared folder id.
	 *
	 * @param identifier the identifier
	 * @return the ACL by shared folder id
	 * @throws Exception the exception
	 */
	WorkspaceACL getACLBySharedFolderId(String identifier) throws Exception;

	/**
	 * Gets the user workspace total items.
	 *
	 * @return the user workspace total items
	 * @throws Exception the exception
	 */
	long getUserWorkspaceTotalItems() throws Exception;

	/**
	 * Gets the user workspace quote.
	 *
	 * @return the user workspace quote
	 * @throws Exception the exception
	 */
	WorkspaceUserQuote getUserWorkspaceQuote() throws Exception;

	/**
	 * Gets the item description by id.
	 *
	 * @param identifier the identifier
	 * @return the item description by id
	 * @throws Exception the exception
	 */
	String getItemDescriptionById(String identifier) throws Exception;

	/**
	 * Validate acl to user.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @return the report assignment acl
	 * @throws Exception the exception
	 */
	ReportAssignmentACL validateACLToUser(String folderId,
			List<String> listLogins, String aclType) throws Exception;

	/**
	 * Load gcube item properties.
	 *
	 * @param itemId the item id
	 * @return the map
	 * @throws Exception the exception
	 */
	Map<String, String> loadGcubeItemProperties(String itemId) throws Exception;

	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId the item id
	 * @return the HTML gcube item properties
	 * @throws Exception the exception
	 */
	String getHTMLGcubeItemProperties(String itemId) throws Exception;

	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId the item id
	 * @param properties the properties
	 * @throws Exception the exception
	 */
	void setGcubeItemProperties(String itemId, Map<String, String> properties) throws Exception;

	FileModel getItemForFileTree(String itemId) throws Exception;
	
	/**
	 * Allows the user to public onto the data catalogue if he has at least 
	 * the role admin somewhere.
	 * @return true if he can publish, false otherwise
	 */
	boolean hasUserRoleAdmin();
	
	/**
	 * Retrieve the username of the user into the session
	 * @return the username of the current user
	 */
	String getUser();
}
