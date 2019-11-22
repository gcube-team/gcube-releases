package org.gcube.portal.wssynclibrary.thredds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;
import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.Status;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.SyncEngine;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.gui.CatalogBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class WorkspaceThreddsSynchronize.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public class WorkspaceThreddsSynchronize implements WorkspaceThreddsSynchronizedRepository<ThSyncStatus, ThSyncFolderDescriptor>{

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkspaceThreddsSynchronize.class);

	/** The engine. */
	private SyncEngine engine=null;

	/** The instance. */
	private static WorkspaceThreddsSynchronize instance = null;

	private static StorageHubClient storageHubInstance;

	public static final String WS_SYNCH_SYNCH_STATUS = "WS-SYNCH.SYNCH-STATUS";

	/** The map call back. */
	// Fully synchronized HashMap
	private Map<String, ThSyncStatus> mapCallBack = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Instantiates a new workspace thredds synchronize.
	 */
	private WorkspaceThreddsSynchronize() {
		// GET ENGINE : SINGLETON INSTANCE
		engine = SyncEngine.get();
	}


	/**
	 * Gets the single instance of WorkspaceThreddsSynchronize.
	 *
	 * @return single instance of WorkspaceThreddsSynchronize
	 */
	public static WorkspaceThreddsSynchronize getInstance() {
		if (instance == null) {
			instance = new WorkspaceThreddsSynchronize();
			storageHubInstance = new StorageHubClient();
		}
		return instance;
	}

	/**
	 * Gets the storage hub instance.
	 *
	 * @return the storage hub instance
	 */
	public static StorageHubClient getStorageHubInstance() {

		if(storageHubInstance==null)
			storageHubInstance = new StorageHubClient();

		return storageHubInstance;

	}



	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#isItemSynchronized(java.lang.String)
	 */
	/**
	 * Check item synched.
	 *
	 * @param itemId the item id
	 * @return the th sync folder descriptor
	 * @throws ItemNotSynched the item not synched
	 * @throws WorkspaceFolderLocked the workspace folder locked
	 * @throws Exception the exception
	 */
	@Override
	public ThSyncFolderDescriptor checkItemSynched(String itemId) throws ItemNotSynched, WorkspaceFolderLocked, Exception {
		logger.debug("Perfoming checkItemSynched for id: "+itemId);
		try {
			// WHEN OPENING A FOLDER, INVOKE CHECK TO UPDATE SYNCH STATUS
			SyncFolderDescriptor desc = engine.check(itemId, false);

			logger.debug("The item  id: "+itemId +" is synched");
			ThSyncFolderDescriptor descr = ThreddsConverter.toThSyncFolderDescriptor.apply(desc);
			logger.trace("CheckItemSynched for id: "+itemId+" returning descriptor: "+descr);
			return descr;

		}catch(WorkspaceNotSynchedException e) {
			String err = "The item id: "+itemId +" is not synched";
			logger.trace(err);
			throw new ItemNotSynched("The item id: "+itemId +" is not synched");
//			System.out.println("Folder not synched, configurin it..");
			//engine.setSynchronizedFolder(config, folder.getId());
		}catch(WorkspaceLockedException e) {
			//logger.warn("Workspace locked, going to force unlock..");
			throw new WorkspaceFolderLocked(itemId, "The folder id: "+itemId +" is currently locked. Another sync process is in progress");
			//engine.forceUnlock(itemId);
		} catch (WorkspaceInteractionException | InternalException e) {
			logger.error("Error: ",e);
			if(e instanceof WorkspaceInteractionException)
				throw new Exception("Sorry, an error occurred during check syncronization due to WS interection for the itemId: "+itemId);
			else if(e instanceof InternalException)
				throw new Exception("Sorry, an Internal Exception occurred during check syncronization for the itemId: "+itemId);

			throw new Exception("Sorry, an error occurred server side  during chck syncronization for the itemId: "+itemId);
		}
	}



	/**
	 * Sets the synchronized folder.
	 *
	 * @param thConfig the th config
	 * @param itemId the item id
	 * @return the th sync folder descriptor
	 * @throws Exception the exception
	 */
	public ThSyncFolderDescriptor setSynchronizedFolder(ThSynchFolderConfiguration thConfig, String itemId) throws Exception {
		SynchFolderConfiguration config = ThreddsConverter.toSynchFolderConfiguration.apply(thConfig);

		if(thConfig.getRemotePath()==null || thConfig.getRemotePath().isEmpty())
			throw new Exception("A valid remote path must be provided");

		if(thConfig.getToCreateCatalogName()==null || thConfig.getToCreateCatalogName().isEmpty())
			throw new Exception("A valid Catalogue Name must be provided");

		if(thConfig.getTargetToken()==null || thConfig.getTargetToken().isEmpty())
			throw new Exception("A valid Target Token must be provided");

		try {
			engine.setSynchronizedFolder(config, itemId);
		} catch (WorkspaceInteractionException | InternalException e) {
			logger.error("Error on setSynchronizedFolder for config: "+thConfig);
			logger.error("Using itemId: "+itemId,e);
			throw new Exception("Error on setSynchronizedFolder");
		}
		return null;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#doSynchronization(java.lang.String)
	 */
	/**
	 * Do sync.
	 *
	 * @param itemId the item id
	 * @return the th sync status
	 * @throws Exception the exception
	 */
	@Override
	public ThSyncStatus doSync(String itemId) throws Exception {

		try {
			// INVOKE SYNCHRONIZATION ON FOLDER
			ProcessDescriptor descriptor = engine.doSync(itemId);
			ThSyncStatus synStatus = mapCallBack.get(itemId);

			if(synStatus==null) {

				registerCallbackForId(itemId);
				descriptor = engine.getProcessDescriptorByFolderId(itemId);
				ProcessStatus status = engine.getProcessStatusByFolderId(itemId);
				updateMapCallback(itemId, status, descriptor);
			}

			logger.debug("DoSync returning status: "+synStatus);
			return synStatus;

		} catch (WorkspaceInteractionException | InternalException | ProcessNotFoundException e) {
			logger.error("Error: ",e);

			if(e instanceof WorkspaceInteractionException)
				throw new Exception("Sorry, an error occurred during syncronization due to WS interection for the itemId: "+itemId);
			else if(e instanceof InternalException)
				throw new Exception("Sorry, an Internal Exception occurred during syncronization for the itemId: "+itemId);

			throw new Exception("Sorry, an error occurred server side  during syncronization for the itemId: "+itemId);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.DoSyncItem#getSyncStatus(java.lang.String)
	 */
	/**
	 * Monitor sync status.
	 *
	 * @param itemId the item id
	 * @return the th sync status
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	@Override
	public ThSyncStatus monitorSyncStatus(String itemId) throws ItemNotSynched, Exception {

		try {

			ThSyncStatus synStatus = mapCallBack.get(itemId);
			if(synStatus==null) {
				try{

					registerCallbackForId(itemId);
					ProcessDescriptor descriptor = engine.getProcessDescriptorByFolderId(itemId);
					ProcessStatus status = engine.getProcessStatusByFolderId(itemId);
					updateMapCallback(itemId, status, descriptor);
					ThSyncStatus thSyncStatus = mapCallBack.get(itemId);

					if(thSyncStatus==null)
						throw new Exception("No sync status found for item id: "+itemId);

					return thSyncStatus;

				}catch(ProcessNotFoundException e){
					throw new Exception("Monitor is not available here. The sync process is in progress on another machine");
				}
			}

			if(synStatus.getProcessStatus()!=null) {
				if(synStatus.getProcessStatus().getStatus()!=null) {
					if(synStatus.getProcessStatus().getStatus().equals(Status.COMPLETED)) {
						//TODO NOW?
						mapCallBack.put(itemId, null);
					}
				}
			}

			logger.trace("MonitorSyncStatus for item: "+itemId+" returning: "+synStatus);
			return synStatus;

		} catch (Exception e) {
			throw new Exception("Sorry, an error occurred during getting sync status for itemId: "+itemId, e);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#removeSync(java.lang.String)
	 */
	/**
	 * Removes the sync.
	 *
	 * @param itemId the item id
	 * @return the boolean
	 */
	@Override
	public Boolean removeSync(String itemId) {
		return null;
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#stopSync(java.lang.String)
	 */
	/**
	 * Stop sync.
	 *
	 * @param itemId the item id
	 * @return the boolean
	 */
	@Override
	public Boolean stopSync(String itemId) {
		return false;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#initRepository()
	 */
	/**
	 * Inits the repository.
	 *
	 * @return the boolean
	 */
	@Override
	public Boolean initRepository() {
		return false;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.WorkspaceSynchronizedRepository#shutDownRepository()
	 */
	/**
	 * Shut down repository.
	 *
	 * @return the boolean
	 */
	@Override
	public Boolean shutDownRepository() {
		try {
			engine.shutDown();
			return true;
		}catch (Exception e) {
			return null;
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.thredds.WorkspaceThreddsSynchronizedRepository#decribeSyncRepository()
	 */
	/**
	 * Decribe sync repository.
	 *
	 * @return the string
	 */
	@Override
	public String decribeSyncRepository() {
		return "Sync repository for Thredds";
	}


	/**
	 * Gets the synched status from item property.
	 *
	 * @param itemId the item id
	 * @param username the username
	 * @return the synched status from item property
	 * @throws ItemNotSynched the item not synched
	 * @throws Exception the exception
	 */
	public Sync_Status getSynchedStatusFromItemProperty(String itemId, String username) throws ItemNotSynched, Exception{

		if(itemId==null)
			throw new Exception("Invalid parameter itemId is null");

		String scope = ScopeProvider.instance.get();
		if(scope == null || scope.isEmpty())
			throw new Exception("You must set a valid scope into ScopeProvider instance");

		String wsSyncStatus = null;
		try{

			Map<String, Object> map = storageHubInstance.open(itemId).asItem().get().getMetadata().getMap();
			wsSyncStatus = (String) map.get(WS_SYNCH_SYNCH_STATUS);
			logger.debug("Item id: "+itemId+" read from Shub has current: "+WS_SYNCH_SYNCH_STATUS +" value at: "+wsSyncStatus);
			if(wsSyncStatus==null)
				return null;

			return Sync_Status.valueOf(wsSyncStatus);
		}catch (Exception e) {
			logger.warn(wsSyncStatus + " is not value of "+Sync_Status.values()+", returning null");
			return null;
		}
	}


	/**
	 * Register callback for id.
	 *
	 * @param itemId the item id
	 * @throws ProcessNotFoundException the process not found exception
	 * @throws Exception the exception
	 */
	@Override
	public void registerCallbackForId(String itemId) throws ProcessNotFoundException, Exception{

		try {

			SyncOperationCallBack callback = new SyncOperationCallBack() {

				@Override
				public void onStep(ProcessStatus status, ProcessDescriptor descriptor) {
					logger.debug("ON STEP : "+status+" "+descriptor);
					logger.debug("LOG : \n"+ status.getLogBuilder().toString());
					if(status.getStatus().equals(ProcessStatus.Status.COMPLETED)) {
						//mapCallBack.remove(itemId);
					}
					updateMapCallback(itemId, status, descriptor);
				}
			};

			// REGISTER CALLBACK TO MONITOR PROGRESS
			logger.debug("Registering callback on itemId: "+itemId);
			engine.registerCallBack(itemId, callback);
		}catch (ProcessNotFoundException e) {
			logger.error("Register callback for id: "+itemId+" threw ProcessNotFoundException: ", e);
			throw e;
		}catch (Exception e) {
			logger.error("Register callback exception: ",e);
			throw new Exception("An error occurred on registering callback for: "+itemId, e);
		}
	}


	/**
	 * Update map callback.
	 *
	 * @param itemId the item id
	 * @param status the status
	 * @param descriptor the descriptor
	 */
	private void updateMapCallback(String itemId, ProcessStatus status, ProcessDescriptor descriptor) {
		ThProcessDescriptor thDesc = ThreddsConverter.toThProcessDescriptor.apply(descriptor);
		ThProcessStatus thStatus = ThreddsConverter.toThProcessStatus.apply(status);
		mapCallBack.put(itemId, new ThSyncStatus(thDesc, thStatus));
		logger.debug("Update map for "+itemId +" with new "+thStatus);
	}



	/**
	 * Gets the available catalogues by token.
	 *
	 * @param token the token
	 * @return the available catalogues by token
	 * @throws Exception the exception
	 */
	public List<ThCatalogueBean> getAvailableCataloguesByToken(String token) throws Exception{

		if(token==null || token.isEmpty())
			throw new Exception("Invalid parameter token null or empty");

		String printToken = token.substring(0, token.length()-5)+"XXXXX";
		logger.debug("Get Available Catalogues by token: "+printToken);
		Set<CatalogBean> ctlgs = engine.getAvailableCatalogsByToken(token);

		if(ctlgs==null || ctlgs.size()==0){
			logger.debug("No Catalogue available for token: "+printToken +" returning empty list");
			return new ArrayList<ThCatalogueBean>(1);
		}

		List<ThCatalogueBean> listCtlgs = new ArrayList<ThCatalogueBean>(ctlgs.size());
		for(CatalogBean bean: ctlgs){
            System.out.println(bean.getName()+" in "+bean.getPath()+" Default : "+bean.getIsDefault());
            ThCatalogueBean toBean = ThreddsConverter.toThCatalogueBean.apply(bean);
            if(toBean!=null)
            	listCtlgs.add(toBean);
		}

		Collections.sort(listCtlgs);
		logger.debug("Returning sorted Catalogue list with: "+listCtlgs.size() +" item/s");
		return listCtlgs;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.wssynclibrary.DoSyncItem#doUnSync(java.lang.String)
	 */
	/**
	 * Do un sync.
	 *
	 * @param itemId the item id
	 * @param deleteRemoteContent the delete remote content
	 * @return the boolean
	 * @throws Exception the exception
	 */
	@Override
	public Boolean doUnSync(String itemId, boolean deleteRemoteContent) throws Exception {

		if(itemId==null)
			throw new Exception("Invalid parameter: itemId is null");

		try{
			engine.unsetSynchronizedFolder(itemId, deleteRemoteContent);
			return true;
		}catch(Exception e){
			logger.error("Unset Syncronized folder exception: ",e);
			throw new Exception("An error occurred on deleting configuration to the item id: "+itemId, e);
		}
	}
}
