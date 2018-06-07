/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync.server;

import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;
import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.portal.wssynclibrary.thredds.WorkspaceThreddsSynchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SyncronizeWithThredds.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 7, 2018
 */
public class SyncronizeWithThredds {


	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(SyncronizeWithThredds.class);

	/** The workspace thredds synchronize. */
	private WorkspaceThreddsSynchronize workspaceThreddsSynchronize;

	/** The Constant sdf. */
	//private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


	/**
	 * Instantiates a new publish on thredds.
	 *
	 * @param wsScopeUserToken the ws scope user token
	 * @param username the username
	 * @param httpSession the http session
	 */
	public SyncronizeWithThredds() {
		this.workspaceThreddsSynchronize = WorkspaceThreddsSynchronize.getInstance();
	}


	/**
	 * Sets the context parameters.
	 *
	 * @param scope the scope
	 * @param userToken the user token
	 */
	private void setContextParameters(String scope, String userToken) {
		logger.debug("Setting context parameters, scope: "+scope +", user token: "+userToken);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(userToken);
	}


	/**
	 * Do sync folder.
	 *
	 * @param folderId the folder id
	 * @param thConfig the th config
	 * @param scope the scope
	 * @param userToken the user token
	 * @return the th sync status
	 * @throws Exception the exception
	 */
	public synchronized ThSyncStatus doSyncFolder(final String folderId, ThSynchFolderConfiguration thConfig, String scope, String userToken) throws Exception{
		logger.debug("Perfoming doSynFolder on folderId: "+folderId);
		boolean firstSync = false;

		try {
			setContextParameters(scope, userToken);
			ThSyncFolderDescriptor folder = workspaceThreddsSynchronize.checkItemSynched(folderId);
		}catch (ItemNotSynched e) {
			firstSync = true;
			// TODO: handle exception
		}catch (Exception e) {
			logger.error("Error on check item sync: ",e);
			throw new Exception("Sorry an error occurred during folder publishing, refresh and try again");
		}

		try{

			if(firstSync) {
				if(thConfig==null)
					throw new Exception("A valid folder configuration must be provided to perforom synchronization");

				logger.info("First sync setting synchronized folder configuration: "+thConfig);
				workspaceThreddsSynchronize.setSynchronizedFolder(thConfig, folderId);
			}

			logger.info("Calling do sync on folder id: "+folderId);
			return workspaceThreddsSynchronize.doSync(folderId);
			//SessionUtil.setTransferPublishingOnThredds(httpSession, status);
		}catch (Exception e) {
			logger.error("Error on do sync: ",e);
			throw new Exception(e.getMessage() +", refresh and try again");

		}
	}




	/**
	 * Gets the synched status from item property.
	 *
	 * @param folderId the folder id
	 * @param scope the scope
	 * @param username the username
	 * @return the synched status from item property
	 * @throws Exception the exception
	 */
	public Sync_Status getSynchedStatusFromItemProperty(String folderId, String scope, String username) throws Exception{

		try {
			try {
				ScopeProvider.instance.set(scope);
				return workspaceThreddsSynchronize.getSynchedStatusFromItemProperty(folderId, username);
			}catch (ItemNotSynched e) {
				logger.info("The folder id: "+folderId +" is not synched returning null as "+Sync_Status.class.getSimpleName());
				return null;
			}

		}catch (Exception e) {
			logger.error("Error on getSynchedStatusFromItemProperty for id: "+folderId, e);
			throw new Exception("Sorry, an error occurred during read sync status from HL properties, try again later");
		}
	}


	/**
	 * Checks if is item synched.
	 *
	 * @param folderId the folder id
	 * @param scope the scope
	 * @param username the username
	 * @return true, if is item synched
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	public boolean isItemSynched(String folderId, String scope, String username) throws ItemNotSynched, Exception{

		Sync_Status value = getSynchedStatusFromItemProperty(folderId, scope, username);

		if(value!=null)
			return true;

		return false;
	}


	/**
	 * Check item synched.
	 *
	 * @param folderId the folder id
	 * @param scope the scope
	 * @param userToken the user token
	 * @return the th sync folder descriptor
	 * @throws ItemNotSynched the item not synched
	 * @throws WorkspaceFolderLocked the workspace folder locked
	 * @throws Exception the exception
	 */
	public ThSyncFolderDescriptor checkItemSynched(String folderId, String scope, String userToken) throws ItemNotSynched, WorkspaceFolderLocked, Exception{

		setContextParameters(scope, userToken);
		return workspaceThreddsSynchronize.checkItemSynched(folderId);
	}

	/**
	 * Gets the sync status.
	 *
	 * @param itemId the item id
	 * @param scope the scope
	 * @param userToken the user token
	 * @return the sync status
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	public ThSyncStatus monitorSyncStatus(String itemId, String scope, String userToken) throws ItemNotSynched, Exception{

		setContextParameters(scope, userToken);
		return workspaceThreddsSynchronize.monitorSyncStatus(itemId);
	}



	/**
	 * Do un sync.
	 *
	 * @param folderId the folder id
	 * @param deleteRemoteContent the delete remote content
	 * @param scope the scope
	 * @param userToken the user token
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean doUnSync(String folderId, boolean deleteRemoteContent, String scope, String userToken) throws Exception {

		setContextParameters(scope, userToken);
		return workspaceThreddsSynchronize.doUnSync(folderId, deleteRemoteContent);
	}



	/**
	 * Register callback for id.
	 *
	 * @param folderId the folder id
	 * @param scope the scope
	 * @param userToken the user token
	 * @throws Exception the exception
	 */
	public void registerCallbackForId(String folderId, String scope, String userToken) throws Exception {
		setContextParameters(scope, userToken);
		workspaceThreddsSynchronize.registerCallbackForId(folderId);
	}



	/**
	 * Gets the available catalogues by token.
	 *
	 * @param scope the scope
	 * @param userToken the user token
	 * @param targetToken the target token
	 * @return the available catalogues by token
	 * @throws Exception the exception
	 */
	public List<ThCatalogueBean> getAvailableCataloguesByToken(String scope, String userToken, String targetToken) throws Exception {
		setContextParameters(scope, userToken);
		return workspaceThreddsSynchronize.getAvailableCataloguesByToken(targetToken);
	}

}
