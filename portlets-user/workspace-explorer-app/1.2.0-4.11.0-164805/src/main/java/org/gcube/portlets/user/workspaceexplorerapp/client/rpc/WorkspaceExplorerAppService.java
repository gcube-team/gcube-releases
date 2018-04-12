package org.gcube.portlets.user.workspaceexplorerapp.client.rpc;

import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.shared.FilterCriteria;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemCategory;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemType;
import org.gcube.portlets.user.workspaceexplorerapp.shared.WorkspaceNavigatorServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface WorkspaceExplorerAppService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 3, 2017
 */
@RemoteServiceRelativePath("WorkspaceExplorerAppService")
public interface WorkspaceExplorerAppService extends RemoteService {


	/**
	 * Gets the breadcrumbs by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 * @return the breadcrumbs by item identifier
	 * @throws Exception the exception
	 */
	List<Item> getBreadcrumbsByItemIdentifier(String itemIdentifier,
			boolean includeItemAsParent) throws Exception;

	/**
	 * Check name.
	 *
	 * @param name the name
	 * @return true, if successful
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	boolean checkName(String name) throws WorkspaceNavigatorServiceException;

	/**
	 * Gets the folder.
	 *
	 * @param folder the folder
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @return the folder
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	Item getFolder(
		Item folder, List<ItemType> showableTypes, boolean purgeEmpyFolders,
		FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException;

	/**
	 * Gets the root.
	 *
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @return the root
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders,
			FilterCriteria filterCriteria)
			throws WorkspaceNavigatorServiceException;

	/**
	 * Gets the my special folder.
	 *
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @return the my special folder
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	Item getMySpecialFolder(List<ItemType> showableTypes,
			boolean purgeEmpyFolders, FilterCriteria filterCriteria)
			throws WorkspaceNavigatorServiceException;

	/**
	 * Gets the item by category.
	 *
	 * @param category the category
	 * @return the item by category
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	Item getItemByCategory(ItemCategory category)throws WorkspaceNavigatorServiceException;

	/**
	 * Gets the size by item id.
	 *
	 * @param itemId the item id
	 * @return the size by item id
	 * @throws Exception the exception
	 */
	Long getSizeByItemId(String itemId) throws Exception;

	/**
	 * Gets the mime type.
	 *
	 * @param itemId the item id
	 * @return the mime type
	 * @throws Exception the exception
	 */
	String getMimeType(String itemId) throws Exception;

	/**
	 * Gets the user acl for folder id.
	 *
	 * @param folderId the folder id
	 * @return the user acl for folder id
	 * @throws Exception the exception
	 */
	String getUserACLForFolderId(String folderId) throws Exception;


	/**
	 * Gets the readable size by item id.
	 *
	 * @param id the id
	 * @return the readable size by item id
	 * @throws Exception the exception
	 */
	String getReadableSizeByItemId(String id) throws Exception;

	/**
	 * Gets the breadcrumbs by item identifier to parent limit.
	 *
	 * @param itemIdentifier the item identifier
	 * @param parentLimit the parent limit
	 * @param includeItemAsParent the include item as parent
	 * @return the breadcrumbs by item identifier to parent limit
	 * @throws Exception the exception
	 */
	List<Item> getBreadcrumbsByItemIdentifierToParentLimit(
			String itemIdentifier, String parentLimit,
			boolean includeItemAsParent) throws Exception;

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder the name folder
	 * @param description the description
	 * @param parentId the parent id
	 * @return the item
	 * @throws Exception the exception
	 */
	Item createFolder(
		String nameFolder, String description, String parentId)
		throws Exception;

	/**
	 * Gets the public link for item id.
	 *
	 * @param itemId the item id
	 * @return the public link for item id
	 * @throws Exception the exception
	 */
	String getPublicLinkForItemId(String itemId)
		throws Exception;

	/**
	 * Gets the folder id from encrypted.
	 *
	 * @param encryptedFolderId the encrypted folder id
	 * @return the folder id from encrypted
	 * @throws Exception the exception
	 */
	String getFolderIdFromEncrypted(String encryptedFolderId)
		throws Exception;
}
