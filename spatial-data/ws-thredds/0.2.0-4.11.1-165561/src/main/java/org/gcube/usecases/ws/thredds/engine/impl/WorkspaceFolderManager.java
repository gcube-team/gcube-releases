package org.gcube.usecases.ws.thredds.engine.impl;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceException;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.engine.impl.threads.ProcessIdProvider;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.LockNotOwnedException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo.SynchronizationStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkspaceFolderManager {



	public static SynchronizedElementInfo getInfo(String elementId) {
		// TODO FILL THIS
		return null;
	}


	private WorkspaceFolder theFolder;


	private String folderId;

	// Cahced objects
	private SynchFolderConfiguration config=null;
	private ThreddsController threddsController=null;

	private Workspace ws;

	public WorkspaceFolderManager(String folderId) throws WorkspaceInteractionException {
		try{
			ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			theFolder=(WorkspaceFolder) ws.getItem(folderId);
			this.folderId=folderId;
		}catch(WorkspaceException | InternalErrorException | HomeNotFoundException | UserNotFoundException e) {
			throw new WorkspaceInteractionException("Unable to access folder id "+folderId,e);
		}
	}


	public WorkspaceFolder getTheFolder() {
		return theFolder;
	}

	public ThreddsController getThreddsController() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		if(threddsController==null) {
			SynchFolderConfiguration config=getSynchConfiguration();
			threddsController=new ThreddsController(config.getRemotePath(),config.getTargetToken());
		}
		return threddsController;
	}

	private ThreddsController getRootThreddsController() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		try {
			WorkspaceFolder root=(WorkspaceFolder) ws.getItem(getSynchConfiguration().getRootFolderId());
			SynchFolderConfiguration rootConfig=WorkspaceUtils.loadConfiguration(root);
			return new ThreddsController(rootConfig.getRemotePath(),rootConfig.getTargetToken());
		}catch(WorkspaceException | InternalErrorException e) {
			throw new WorkspaceInteractionException(e);
		}

	}

	public boolean isRoot() throws WorkspaceNotSynchedException, WorkspaceInteractionException{
		try{
			return getSynchConfiguration().getRootFolderId().equals(theFolder.getId());
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException(e);
		}
	}
	
	
	public SynchFolderConfiguration getSynchConfiguration() throws WorkspaceInteractionException, WorkspaceNotSynchedException {
		if(config==null) {
			try {
				if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
				log.debug("Loading properties for ");
				config=WorkspaceUtils.loadConfiguration(theFolder);				
			}catch(InternalErrorException e) {
				throw new WorkspaceInteractionException("Unable to load synch configuration in "+folderId,e);
			}
		}
		return config;
	}	

	public boolean isSynched() throws WorkspaceInteractionException {
		try{			
			Map<String,String> props=theFolder.getProperties().getProperties();
			return props.containsKey(Constants.WorkspaceProperties.TBS)&&(props.get(Constants.WorkspaceProperties.TBS)!=null);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to check Synch flag on "+folderId,e);
		}
	}


	public SyncFolderDescriptor check(boolean recursively) throws WorkspaceInteractionException, InternalException {
		if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
		if(isLocked()&&!isLockOwned())throw new WorkspaceLockedException("Workspace "+folderId+" is locked.");

		SynchFolderConfiguration config=getSynchConfiguration();
		try{
			checkFolder(theFolder,recursively,config,null,theFolder.getId(),WorkspaceUtils.safelyGetLastUpdate(theFolder));
			return new SyncFolderDescriptor(this.folderId,this.theFolder.getPath(),config);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException(e);
		}
	}

	private boolean isLockOwned() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		String currentProcessId=ProcessIdProvider.instance.get();
		if(currentProcessId==null) return false;
		return currentProcessId.equals(getLockId());
	}


	public String getLockId() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {		
		return getRootThreddsController().readThreddsFile(Constants.LOCK_FILE);
	}

	public boolean isLocked() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		return getRootThreddsController().existsThreddsFile(Constants.LOCK_FILE);
	}


	public void configure(SynchFolderConfiguration toSet) throws WorkspaceInteractionException, InternalException {
		if(isSynched()) throw new WorkspaceInteractionException("Folder "+folderId+" is already configured for synchronization.");
		log.info("Configuring folder {} as {} ",folderId,toSet);



		// Checking AND initializing remote folder
		log.debug("Checking remote folder existence .. ");
		boolean createCatalog=false;
		try {
			String catalogName=toSet.getToCreateCatalogName();

			ThreddsController controller=	new ThreddsController(toSet.getRemotePath(),toSet.getTargetToken());
			if(!controller.existsThreddsFile(null)) {
				log.info("Folder not found, creating it..");
				controller.createEmptyFolder(null);
				createCatalog=true;
			}else {				
				ThreddsCatalog catalog=controller.getCatalog();
				if (catalog==null) {
					createCatalog=true;
				}else {
					log.info("Found matching catalog {} ",catalog);
					catalogName=catalog.getTitle();
					if(catalogName==null) catalogName=catalog.getDeclaredDataSetScan().iterator().next().getName();
					toSet.setToCreateCatalogName(catalogName);
				}
			}

			if(createCatalog) {
				log.info("Creating catalog {} ",catalogName);
				log.debug("Created catalog {}", controller.createCatalog(catalogName));
			}

			WorkspaceUtils.initProperties(theFolder, toSet.getRemotePath(), toSet.getFilter(), 
					toSet.getTargetToken(),toSet.getToCreateCatalogName(),toSet.getValidateMetadata(),theFolder.getId());

		}catch(InternalException e) {
			throw new InternalException ("Unable to check/initialize remote folder",e);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to set Properties to "+folderId,e);
		}
	}

	public void dismiss(boolean deleteRemote) throws WorkspaceInteractionException, InternalException {
		if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
		if(isLocked()&&!isLockOwned())throw new WorkspaceLockedException("Workspace "+folderId+" is locked.");

		try {
			cleanCache();
			WorkspaceUtils.cleanItem(theFolder);
			if(deleteRemote) 
				getThreddsController().createEmptyFolder(null);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to cleanup "+folderId,e);
		}
	}

	public void setLastUpdateTime() throws InternalErrorException {
		WorkspaceUtils.setLastUpdateTime(theFolder, System.currentTimeMillis());
	}


	public void forceUnlock() throws InternalException,WorkspaceInteractionException {
		try {
			getRootThreddsController().deleteThreddsFile(Constants.LOCK_FILE);
		} catch (RemoteFileNotFoundException e) {
			log.debug("Forced unlock but no file found.",e);
		} catch (WorkspaceNotSynchedException e) {
			log.warn("Invoked force lock on not synched folder.",e);
		} catch (InternalException | WorkspaceInteractionException e) {
			throw e ;
		}
	}


	public void lock(String processId) throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {		
		getRootThreddsController().lockFolder(processId);
	}

	public void unlock() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		unlock(ProcessIdProvider.instance.get());		 
	}

	public void unlock(String processId) throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		String currentLock=getLockId();
		if(processId.equals(currentLock)) getRootThreddsController().deleteThreddsFile(Constants.LOCK_FILE);
		else throw new LockNotOwnedException("Process "+processId+" can't remove lock owned by "+currentLock);
	}


	//*************************** PRIVATE 

	private void cleanCache() {
		this.config=null;
		this.threddsController=null;
	}





	private static void checkFolder(WorkspaceFolder folder,boolean recursive, SynchFolderConfiguration rootConfig, String relativePathFromRootFolder, String rootFolderId,Date lastUpdatedRoutine) throws InternalErrorException, InternalException {
		// Check folder configuration
		log.trace("Checking folder {} ",folder.getPath());
		log.debug("Configuration is {}, relativePath is {} ",rootConfig,relativePathFromRootFolder);

		String folderName=folder.getName();

		String currentRemotePath=rootConfig.getRemotePath()+((relativePathFromRootFolder==null)?"":"/"+relativePathFromRootFolder);

		
		
		ThreddsController controller=new ThreddsController(currentRemotePath, rootConfig.getTargetToken());


		HashSet<String> currentFolderExistingItem=new HashSet<String>();


		log.debug("Initializing properties for {} ",folderName);
		//INIT PROPERTIES IF NOT PRESENT
		if(!WorkspaceUtils.isConfigured(folder))
			WorkspaceUtils.initProperties(folder,currentRemotePath,rootConfig.getFilter(),rootConfig.getTargetToken(),rootConfig.getToCreateCatalogName(),rootConfig.getValidateMetadata(),rootFolderId);

		for(WorkspaceItem item:folder.getChildren()) {
			String itemName=item.getName();
			String itemRelativePath=(relativePathFromRootFolder==null)?itemName:relativePathFromRootFolder+"/"+itemName;
			String itemRemotePath=currentRemotePath+"/"+itemName;
			if(item.isFolder()) {
				if(recursive)
					checkFolder((WorkspaceFolder) item,recursive,rootConfig,itemRelativePath,rootFolderId,lastUpdatedRoutine);
				else WorkspaceUtils.initProperties(item, itemRemotePath, rootConfig.getFilter(), rootConfig.getTargetToken(),rootConfig.getToCreateCatalogName(),rootConfig.getValidateMetadata(),rootFolderId);
			}else if(rootConfig.matchesFilter(itemName)) {
				if(!WorkspaceUtils.isConfigured(item))
					WorkspaceUtils.initProperties(item, null, null, null,null,null,null);
			}
			currentFolderExistingItem.add(itemName);
		}


		// ACTUALLY CHECK STATUS 

		if(controller.existsThreddsFile(null)) {
			SynchronizationStatus folderStatus=SynchronizationStatus.OUTDATED_REMOTE;
			log.debug("Remote Folder {} exists. Checking status..",currentRemotePath);
			RemoteFileDescriptor folderDesc=controller.getFileDescriptor();
			HashSet<String> remoteFolderItems=new HashSet<>(folderDesc.getChildren());

			
			
			
			// CHECK HISTORY
			Set<String> accountingEntries=WorkspaceUtils.scanAccountingForStatus(folder, rootConfig, 
					currentFolderExistingItem, remoteFolderItems, 
					controller, null, null);
			if(accountingEntries.isEmpty()) {
				log.debug("No accounting entries found");
				folderStatus=SynchronizationStatus.UP_TO_DATE;
			}

			// CHECK WS ITEMS 
			for(WorkspaceItem item:folder.getChildren())
				if(item.isFolder()||rootConfig.matchesFilter(item.getName())) {
					SynchronizationStatus itemStatus=WorkspaceUtils.getStatusAgainstRemote(item, remoteFolderItems, controller,lastUpdatedRoutine);
					item.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS,itemStatus+""));
					folderStatus=folderStatus.equals(SynchronizationStatus.UP_TO_DATE)?itemStatus:folderStatus;

				}

			// CHECK REMOTE FOLDER
			if(folderStatus.equals(SynchronizationStatus.UP_TO_DATE)) {
				Set<String> toImportItems=WorkspaceUtils.scanRemoteFolder(folderDesc, accountingEntries, currentFolderExistingItem, folder, controller, rootConfig, null, null);
				if(!toImportItems.isEmpty()) folderStatus=SynchronizationStatus.OUTDATED_WS;
			}

			folder.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS,folderStatus+""));
		}else {
			// Remote Folder not existing, set everything to OUTDATED_REMOTE
			for(WorkspaceItem item:folder.getChildren())				
				item.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS,SynchronizationStatus.OUTDATED_REMOTE+""));
			folder.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS,SynchronizationStatus.OUTDATED_REMOTE+""));
		}



	}

	public File loadCatalogFile() {
		// TODO Auto-generated method stub
		return null;
	}


	public void updateCatalogFile(File toUpload) {
		// TODO Auto-generated method stub

	}


}
