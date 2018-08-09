/**
 *
 */
package org.gcube.common.storagehubwrapper.server;

import org.gcube.common.storagehubwrapper.server.tohl.Workspace;


/**
 * The Class StorageHubWrapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 26, 2018
 */
public class StorageHubWrapper {

	private StorageHubClientService storageHubClientService;

	public Object lock = new Object(); //Thread safety

	private Workspace workspace = null;


	/**
	 * Instantiates a new storage hub wrapper.
	 *
	 * @param scope the scope
	 * @param authorizationToken the authorization token
	 */
	public StorageHubWrapper(String scope, String authorizationToken) {
		this.storageHubClientService = new StorageHubClientService(scope, authorizationToken);
	}

	/**
	 * Gets the storage hub client service.
	 *
	 * @return the storageHubClientService
	 */
	public StorageHubClientService getStorageHubClientService() {

		return storageHubClientService;
	}


	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 */
	public Workspace getWorkspace(){

		synchronized (lock) {
			if(workspace==null){
				workspace = new WorkspaceStorageHubClientService.WorkspaceStorageHubClientServiceBuilder(storageHubClientService).build();
			}

			return workspace;
		}

	}

}
