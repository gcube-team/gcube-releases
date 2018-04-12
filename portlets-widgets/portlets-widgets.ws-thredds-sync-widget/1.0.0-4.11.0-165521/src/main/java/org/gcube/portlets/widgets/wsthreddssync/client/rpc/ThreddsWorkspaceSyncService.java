package org.gcube.portlets.widgets.wsthreddssync.client.rpc;

import java.util.List;

import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;
import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface ThreddsWorkspaceSyncService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 8, 2018
 */
@RemoteServiceRelativePath("wsthreddssync")
public interface ThreddsWorkspaceSyncService extends RemoteService {

	/**
	 * Checks if is item synched.
	 *
	 * @param folderId the folder id
	 * @return the ws thredds synch folder descriptor
	 * @throws WorkspaceFolderLocked the workspace folder locked
	 * @throws Exception the exception
	 */
	WsThreddsSynchFolderDescriptor isItemSynched(String folderId) throws WorkspaceFolderLocked, Exception;

	/**
	 * Do sync folder.
	 *
	 * @param folderId the folder id
	 * @param clientConfig the client config
	 * @return the th sync status
	 * @throws Exception the exception
	 */
	ThSyncStatus doSyncFolder(final String folderId, WsThreddsSynchFolderConfiguration clientConfig) throws Exception;

	/**
	 * Monitor sync status.
	 *
	 * @param folderId the folder id
	 * @return the th sync status
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	ThSyncStatus monitorSyncStatus(String folderId) throws ItemNotSynched, Exception;

	/**
	 * Gets the list of vr es for logged user.
	 *
	 * @return the list of vr es for logged user
	 * @throws Exception the exception
	 */
	List<GcubeScope> getListOfScopesForLoggedUser() throws Exception;

	/**
	 * Gets the available catalogues for scope.
	 *
	 * @param scope the scope
	 * @return the available catalogues for scope
	 * @throws Exception the exception
	 */
	List<ThCatalogueBean> getAvailableCataloguesForScope(String scope) throws Exception;


	/**
	 * Do un sync folder.
	 *
	 * @param folderId the folder id
	 * @return the boolean
	 * @throws Exception the exception
	 */
	Boolean doUnSyncFolder(String folderId) throws Exception;
}
