/**
 *
 */
package org.gcube.common.storagehubwrapper.server;

import org.gcube.common.storagehubwrapper.server.tohl.Workspace;


/**
 * The Class StorageHubWrapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 31, 2019
 */
public class StorageHubWrapper {

	private StorageHubClientService storageHubClientService;
	public Object lock = new Object(); //Thread safety
	private Workspace workspace = null;
	private boolean withAccounting = false;
	private boolean withFileDetails = false;
	private boolean withMapProperties = false;


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
	 * Instantiates a new storage hub wrapper.
	 *
	 * @param scope the scope
	 * @param authorizationToken the authorization token
	 * @param withAccounting the with accounting
	 * @param withFileDetails the with file details
	 * @param withMapProperties the with map properties
	 */
	public StorageHubWrapper(String scope, String authorizationToken, final boolean withAccounting, final boolean withFileDetails,
		final boolean withMapProperties) {
		this.storageHubClientService = new StorageHubClientService(scope, authorizationToken);
		this.withAccounting = withAccounting;
		this.withFileDetails = withFileDetails;
		this.withMapProperties = withMapProperties;
	}

	/**
	 * A Workspace interface HL-Like 
	 *
	 * @return the workspace as single instance
	 */
	public Workspace getWorkspace(){

		synchronized (lock) {
			if(workspace==null){
				workspace = new WorkspaceStorageHubClientService.WorkspaceStorageHubClientServiceBuilder(storageHubClientService).
								withAccounting(withAccounting).
								withFileDetails(withFileDetails).
								withMapProperties(withMapProperties).
								build();
			}
			return workspace;
		}
	}
	

	/**
	 * Gets the storage hub client service.
	 *
	 * @return the storageHubClientService
	 */
	public StorageHubClientService getStorageHubClientService() {

		return storageHubClientService;
	}

}
