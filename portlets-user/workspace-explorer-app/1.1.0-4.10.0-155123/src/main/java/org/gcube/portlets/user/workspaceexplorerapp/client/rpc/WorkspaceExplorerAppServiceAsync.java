/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.rpc;

import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.shared.FilterCriteria;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemCategory;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemType;

import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The Interface WorkspaceExplorerAppServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 3, 2017
 */
public interface WorkspaceExplorerAppServiceAsync {


	/**
	 * Gets the root.
	 *
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @param callback the callback
	 * @return the root
	 */
	public void getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders,
			FilterCriteria filterCriteria, AsyncCallback<Item> callback);


	/**
	 * Check name.
	 *
	 * @param name the name
	 * @param callback the callback
	 */
	public void checkName(String name, AsyncCallback<Boolean> callback);



	/**
	 * Gets the folder.
	 *
	 * @param folder the folder
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @param callback the callback
	 * @return the folder
	 */
	public void getFolder(Item folder, List<ItemType> showableTypes,
			boolean purgeEmpyFolders, FilterCriteria filterCriteria,
			AsyncCallback<Item> callback);



	/**
	 * Gets the breadcrumbs by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 * @param asyncCallback the async callback
	 * @return the breadcrumbs by item identifier
	 */
	public void getBreadcrumbsByItemIdentifier(String itemIdentifier,
			boolean includeItemAsParent, AsyncCallback<List<Item>> asyncCallback);


	/**
	 * Gets the my special folder.
	 *
	 * @param showableTypes the showable types
	 * @param purgeEmpyFolders the purge empy folders
	 * @param filterCriteria the filter criteria
	 * @param asyncCallback the async callback
	 * @return the my special folder
	 */
	public void getMySpecialFolder(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria, AsyncCallback<Item> asyncCallback);


	/**
	 * Gets the item by category.
	 *
	 * @param category the category
	 * @param asyncCallback the async callback
	 * @return the item by category
	 */
	public void getItemByCategory(ItemCategory category,  AsyncCallback<Item> asyncCallback);


	/**
	 * Gets the size by item id.
	 *
	 * @param id the id
	 * @param asyncCallback the async callback
	 * @return the size by item id
	 */
	public void getSizeByItemId(String id, AsyncCallback<Long> asyncCallback);


	/**
	 * Gets the readable size by item id.
	 *
	 * @param id the id
	 * @param asyncCallback the async callback
	 * @return the readable size by item id
	 */
	public void getReadableSizeByItemId(String id, AsyncCallback<String> asyncCallback);

	/**
	 * Gets the mime type.
	 *
	 * @param id the id
	 * @param asyncCallback the async callback
	 * @return the mime type
	 */
	public void getMimeType(String id, AsyncCallback<String> asyncCallback);

	/**
	 * Gets the user acl for folder id.
	 *
	 * @param id the id
	 * @param asyncCallback the async callback
	 * @return the user acl for folder id
	 */
	public void getUserACLForFolderId(String id,
			AsyncCallback<String> asyncCallback);

	/**
	 * Gets the breadcrumbs by item identifier to parent limit.
	 *
	 * @param itemIdentifier the item identifier
	 * @param parentLimit the parent limit
	 * @param includeItemAsParent the include item as parent
	 * @param callback the callback
	 * @return the breadcrumbs by item identifier to parent limit
	 */
	void getBreadcrumbsByItemIdentifierToParentLimit(String itemIdentifier,
			String parentLimit, boolean includeItemAsParent,
			AsyncCallback<List<Item>> callback);

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder the name folder
	 * @param description the description
	 * @param parentId the parent id
	 * @param callback the callback
	 */
	void createFolder(
		String nameFolder, String description, String parentId,
		AsyncCallback<Item> callback);

	/**
	 * Gets the public link for item id.
	 *
	 * @param itemId the item id
	 * @param callback the callback
	 * @return the public link for item id
	 */
	void getPublicLinkForItemId(String itemId, AsyncCallback<String> callback);

	/**
	 * Gets the folder id from encrypted.
	 *
	 * @param encodedFolderID the encoded folder id
	 * @param callback the callback
	 * @return the folder id from encrypted
	 */
	void getFolderIdFromEncrypted(
		String encodedFolderID, AsyncCallback<String> callback);

}
