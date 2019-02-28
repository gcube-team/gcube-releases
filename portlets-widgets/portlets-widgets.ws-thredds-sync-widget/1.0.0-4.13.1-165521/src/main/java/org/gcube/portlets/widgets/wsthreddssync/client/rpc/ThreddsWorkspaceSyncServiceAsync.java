package org.gcube.portlets.widgets.wsthreddssync.client.rpc;

import java.util.List;

import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface ThreddsWorkspaceSyncServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 8, 2018
 */
public interface ThreddsWorkspaceSyncServiceAsync
{

    /**
     * Utility class to get the RPC Async interface from client-side code.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * Mar 8, 2018
     */
    public static final class Util
    {
        private static ThreddsWorkspaceSyncServiceAsync instance;

        /**
         * Gets the single instance of Util.
         *
         * @return single instance of Util
         */
        public static final ThreddsWorkspaceSyncServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (ThreddsWorkspaceSyncServiceAsync) GWT.create( ThreddsWorkspaceSyncService.class );
            }
            return instance;
        }

        /**
         * Instantiates a new util.
         */
        private Util()
        {
            // Utility class should not be instantiated
        }
    }


	/**
	 * Checks if is item synched.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 */
	void isItemSynched(String folderId, AsyncCallback<WsThreddsSynchFolderDescriptor> callback);


	/**
	 * Do sync folder.
	 *
	 * @param folderId the folder id
	 * @param clientConfig the client config
	 * @param callback the callback
	 */
	void doSyncFolder(String folderId, WsThreddsSynchFolderConfiguration clientConfig, AsyncCallback<ThSyncStatus> callback);


	/**
	 * Monitor sync status.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 */
	void monitorSyncStatus(String folderId, AsyncCallback<ThSyncStatus> callback);



	/**
	 * Gets the list of scopes for logged user.
	 *
	 * @param callback the callback
	 * @return the list of scopes for logged user
	 */
	void getListOfScopesForLoggedUser(AsyncCallback<List<GcubeScope>> callback);

	/**
	 * Gets the available catalogues for scope.
	 *
	 * @param scope the scope
	 * @param callback the callback
	 * @return the available catalogues for scope
	 */
	void getAvailableCataloguesForScope(String scope, AsyncCallback<List<ThCatalogueBean>> callback);


	/**
	 * Do un sync folder.
	 *
	 * @param folderId the folder id
	 * @param callback the callback
	 */
	void doUnSyncFolder(String folderId, AsyncCallback<Boolean> callback);
}
