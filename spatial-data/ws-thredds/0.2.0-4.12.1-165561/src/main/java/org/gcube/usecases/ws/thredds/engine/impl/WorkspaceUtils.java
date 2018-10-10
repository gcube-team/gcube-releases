package org.gcube.usecases.ws.thredds.engine.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRenaming;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.engine.impl.threads.DeleteRemoteRequest;
import org.gcube.usecases.ws.thredds.engine.impl.threads.SynchronizationThread;
import org.gcube.usecases.ws.thredds.engine.impl.threads.TransferFromThreddsRequest;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.model.StepReport;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo.SynchronizationStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkspaceUtils {





	/**
	 * Checks current accounting info in order to infere synchronization status. 
	 * OwnerProcess and service parameters can be null for check purposes.  
	 * 
	 * @param folderPath
	 * @param toScanFolder
	 * @param config
	 * @param localChildrenNames
	 * @param remoteChildrenNames
	 * @param folderController
	 * 
	 * @param ownerProcess 
	 * @param service		 
	 * @return set of Item names that have been found to be synchronized
	 * @throws InternalErrorException 
	 */
	static Set<String> scanAccountingForStatus(
			WorkspaceFolder toScanFolder, 
			SynchFolderConfiguration config, 
			Set<String> localChildrenNames, 
			Set<String> remoteChildrenNames, 
			ThreddsController folderController, 

			Process ownerProcess,
			ExecutorService service) throws InternalErrorException{

		Set<String> handledAccountingEntries=new HashSet<>();

		log.debug("Checking history of {} ",toScanFolder.getPath());


		String relativePath=toScanFolder.getProperties().getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH);


		Date folderLastUpdateTime=null;
		try{
			folderLastUpdateTime=WorkspaceUtils.safelyGetLastUpdate(toScanFolder);			
		}catch(Throwable t) {
			log.warn("Unable to get folder {} last update time. Assuming first run.. ",toScanFolder.getName(),t);
			folderLastUpdateTime=new Date(0l);
		}
		// scanning for deletions
		log.debug("Checking Accounting for {}. Last update time is {} ",toScanFolder.getName(),Constants.DATE_FORMAT.format(folderLastUpdateTime));
		for(AccountingEntry entry:toScanFolder.getAccounting()) {
			try {				
				Date eventTime=entry.getDate().getTime();
				if(folderLastUpdateTime==null||	eventTime.after(folderLastUpdateTime)) { // SKIP IF ENTRY OLDER THAN LAST UPDATE TIME					
					String toDeleteRemote=null;
					switch(entry.getEntryType()) {
					case CUT:
					case REMOVAL:{					
						AccountingEntryRemoval removalEntry=(AccountingEntryRemoval) entry;
						if(removalEntry.getItemType().equals(WorkspaceItemType.FOLDER)||
								config.matchesFilter(removalEntry.getItemName()))
							toDeleteRemote=removalEntry.getItemName();								
						break;
					}
					case RENAMING:{					
						AccountingEntryRenaming renamingEntry=(AccountingEntryRenaming) entry;					
						WorkspaceItem newItem=toScanFolder.find(renamingEntry.getNewItemName());						
						if(newItem.isFolder()||config.matchesFilter(renamingEntry.getOldItemName()))
							toDeleteRemote=renamingEntry.getOldItemName();	
						break;
					}					
					}


					if(toDeleteRemote!=null){
						// SKIP IF LOCAL EXISTS
						if(localChildrenNames.contains(toDeleteRemote))
							log.debug("Skipping accounting entry for existing local item {} ",toDeleteRemote);
						else if(remoteChildrenNames.contains(toDeleteRemote)) {
							log.debug("Checking age of remote {} ",toDeleteRemote);

							// IF REMOTE OLDER THAN ENTRY -> DELETE REQUEST
							// IF REMOTE NEWER -> IMPORT REQUEST
							RemoteFileDescriptor remote=folderController.getFileDescriptor(relativePath+"/"+toDeleteRemote);
							Date remoteDate=new Date(remote.getLastUpdate());
							log.debug("Last remote update : {} . Event date {}  ",Constants.DATE_FORMAT.format(remoteDate),Constants.DATE_FORMAT.format(eventTime));
							if(service!=null) {
								log.debug("Service is not null. Submitting request ... ");
								if(eventTime.after(remoteDate)) {
									service.execute(new SynchronizationThread(new DeleteRemoteRequest(ownerProcess, toScanFolder,toDeleteRemote)));
									handledAccountingEntries.add(toDeleteRemote);
									log.debug("Submitted DELETION request number {} ",ownerProcess.getStatus().getQueuedTransfers().incrementAndGet());
								}
//								}else {
//									service.execute(new SynchronizationThread(new TransferFromThreddsRequest(ownerProcess, null, toScanFolder, toDeleteRemote)));
//									log.debug("Submitted UPDATE-LOCAL request number {} ",ownerProcess.getStatus().getQueuedTransfers().incrementAndGet());
//								}
							}
						}else log.debug("To delete remote {} not found. skipping it.. ",toDeleteRemote);
						// SKIP IF REMOTE NOT FOUND 
					}
				}
			}catch(Throwable t) {
				log.error("Unable to submit deletion request for {} ",entry,t);
			}
		}

		return handledAccountingEntries;
	}


	/**
	 * Scans remote Folder in order to gather elements to be synchronized.
	 * OwnerProcess and Service can be null for check purposes.
	 * 
	 * 
	 * @param folderPath
	 * @param folderDesc
	 * @param handledAccountingEntries
	 * @param handledWorkspaceItemEntries
	 * @param toScanFolder
	 * @param folderController
	 * @param config
	 * @param ownerProcess
	 * @param service
	 * @return
	 * @throws InternalException
	 * @throws InternalErrorException
	 */
	static Set<String> scanRemoteFolder(
			RemoteFileDescriptor folderDesc,
			Set<String> handledAccountingEntries, 
			Set<String> handledWorkspaceItemEntries, 
			WorkspaceFolder toScanFolder,
			ThreddsController folderController,
			SynchFolderConfiguration config,
			Process ownerProcess,
			ExecutorService service) throws InternalException, InternalErrorException{

		log.debug("Checking remote content for {}. Remote Absolute Path is {} ",toScanFolder.getPath(),folderDesc.getAbsolutePath());
		Set<String> handledRemoteElements=new HashSet<String>();

//		String relativePath=toScanFolder.getProperties().getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH);


		if(!folderDesc.isDirectory()) throw new InternalException("Remote Descriptor "+folderDesc.getAbsolutePath()+" Is not a directory. ");
		for(String child:folderDesc.getChildren()) {
			// skip if already handled with accounting
			if(handledAccountingEntries.contains(child))
				log.debug("Skipping remote child {} because already handled with accouting", child);
			// skip if already handled with local items
			else if(handledWorkspaceItemEntries.contains(child))
				log.debug("Skipping remote child {} because already handled with respective item",child);
			else {
				RemoteFileDescriptor childDesc=folderController.getFileDescriptor(child);
				if(childDesc.isDirectory()) {					
					handledRemoteElements.add(child);
				}else if (config.matchesFilter(child)){
					log.debug("Child {} matches filter...");
					handledRemoteElements.add(child);
					if(service!=null) {
						service.execute(new SynchronizationThread(new TransferFromThreddsRequest(ownerProcess, null, toScanFolder, child)));
						log.debug("Submitted IMPORT request number {} ",ownerProcess.getStatus().getQueuedTransfers().incrementAndGet());
					}
					// import if matching
				}else log.debug("Skipping not matching remote {} ",child);
				// skip if doesn't match filter or isn't folder
			}
		}		
		return handledRemoteElements;
	}


	static void initProperties(WorkspaceItem toInit, String remotePath, String filter, String targetToken,
			String catalogName,Boolean validateMeta, String rootFolderId) throws InternalErrorException {
		
		Map<String,String> toSetProperties=toInit.getProperties().getProperties();
		initIfMissing(toSetProperties,Constants.WorkspaceProperties.TBS,"true");
		
		
		initIfMissing(toSetProperties,Constants.WorkspaceProperties.LAST_UPDATE_TIME,0l+"");
		initIfMissing(toSetProperties,Constants.WorkspaceProperties.LAST_UPDATE_STATUS,StepReport.Status.OK+"");
		initIfMissing(toSetProperties,Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS,SynchronizationStatus.UP_TO_DATE+"");
		
		if(toInit.isFolder()) {
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.SYNCH_FILTER,filter);
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.REMOTE_PATH,remotePath);
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.REMOTE_PERSISTENCE,Constants.THREDDS_PERSISTENCE);
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.TARGET_TOKEN,targetToken);
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.RELATED_CATALOG,catalogName);
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.VALIDATE_METADATA,validateMeta+"");
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.ROOT_FOLDER_ID,rootFolderId);
		}else {
			initIfMissing(toSetProperties,Constants.WorkspaceProperties.METADATA_UUID,null);			
		}
		toInit.getProperties().addProperties(toSetProperties);
	}

	
	private static void initIfMissing(Map<String,String> current,String key,String defaultValue) {
		if(!current.containsKey(key)||
				current.get(key)==null||
				current.get(key).equals("null")) current.put(key, defaultValue);
	}
	
	
	static boolean isConfigured(WorkspaceItem toCheck) throws InternalErrorException {
		return isConfigured(toCheck.getProperties().getProperties());
	}
	static boolean isConfigured(Map<String,String> toCheckProperties) {
		return (toCheckProperties.containsKey(Constants.WorkspaceProperties.TBS)&&toCheckProperties.get(Constants.WorkspaceProperties.TBS)!=null);
	}

	static SynchronizationStatus getStatusAgainstRemote(WorkspaceItem item, Set<String> existingRemote, ThreddsController remoteFolderController,Date lastUpdateRoutine) throws NumberFormatException, InternalErrorException, RemoteFileNotFoundException {
		String itemName=item.getName();
		SynchronizationStatus status=SynchronizationStatus.OUTDATED_REMOTE;
		if(existingRemote.contains(itemName)) {
			RemoteFileDescriptor desc=remoteFolderController.getFileDescriptor(itemName);
			Date remoteDate=new Date(desc.getLastUpdate());
			Date localDate=item.getLastModificationTime().getTime();
			Date lastUpdate=safelyGetLastUpdate(item);
			
			if(localDate.equals(lastUpdate)) {
				//LAST MODIFCATION WAS FROM SYNCHRONIZATION
				if(remoteDate.after(lastUpdate)) status=SynchronizationStatus.OUTDATED_WS;
				else status=SynchronizationStatus.UP_TO_DATE;
			}else
				if(remoteDate.before(localDate)) { // REMOTE OLDER THAN LOCAL 
						if(isModifiedAfter(item,lastUpdateRoutine)) status=SynchronizationStatus.OUTDATED_REMOTE; // IT's been locally modified from last routine
						else status=SynchronizationStatus.UP_TO_DATE;
				}
				else if(remoteDate.after(localDate)) {	// REMOTE NEWER &..
					if (remoteDate.equals(lastUpdate))status =SynchronizationStatus.UP_TO_DATE;			// REMOTE DATE == LAST UPDATE ROUTINE -> UP TO DATE
					else if (remoteDate.before(lastUpdate))status =SynchronizationStatus.OUTDATED_REMOTE;	// REMOTE DATE < LAST UPDATE -> transfer to thredds, last update was faulty
					else status=SynchronizationStatus.OUTDATED_WS;										// REMOTE DATE != LAST UPDATE -> import from thredds
				}						
		}
		return status;
	}

	
	//	
	//	/**
	//	 * 
	//	 * @return max date between creation time, last modification time && LAST-UPDATE-PROP
	//	 * @throws InternalErrorException 
	//	 * @throws NumberFormatException 
	//	 */
	//	static Date getMaxLastUpdate(WorkspaceItem item) throws NumberFormatException, InternalErrorException {
	//		return new Date(Long.max(Long.parseLong(item.getProperties().getPropertyValue(Constants.WorkspaceProperties.LAST_UPDATE_TIME)),item.getLastModificationTime().getTimeInMillis()));		
	//	}

	static Date safelyGetLastUpdate(WorkspaceItem item) throws InternalErrorException {
		try {
			return new Date(Long.parseLong(item.getProperties().getPropertyValue(Constants.WorkspaceProperties.LAST_UPDATE_TIME)));
		}catch(NumberFormatException e) {
			log.debug("Unable to get last update time for {} ",item.getName(),e);
			return new Date(0l);
		}
	}

	public static boolean isModifiedAfter(WorkspaceItem item,Date fromDate) throws InternalErrorException {		
		for(AccountingEntry entry:item.getAccounting()) {
			if(entry.getDate().getTime().after(fromDate)) {
				switch(entry.getEntryType()) {
				case PASTE:
				case CREATE:
				case RESTORE:				
				case UPDATE:
				case ADD: return true;				
				}
			}
		}
		return false;
	}


	static void cleanItem(WorkspaceItem item) throws InternalErrorException {
		Properties props=item.getProperties();
		if(props.hasProperty(Constants.WorkspaceProperties.TBS)) {		
			if(item.isFolder()) {
				props.addProperties(Constants.cleanedFolderPropertiesMap);
				for(WorkspaceItem child : ((WorkspaceFolder)item).getChildren())
					cleanItem(child);
			}else props.addProperties(Constants.cleanedItemPropertiesMap);
		}
	}

	static void setLastUpdateTime(WorkspaceFolder folder,long toSetTime) throws InternalErrorException {
		StepReport.Status currentWSStatus=StepReport.Status.valueOf(folder.getProperties().getPropertyValue(Constants.WorkspaceProperties.LAST_UPDATE_STATUS));

		if(currentWSStatus.equals(StepReport.Status.OK))			
			folder.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.LAST_UPDATE_TIME, toSetTime+""));

		for(WorkspaceItem item:folder.getChildren())
			if(item.isFolder()) setLastUpdateTime((WorkspaceFolder) item, toSetTime);

	}

	public static SynchFolderConfiguration loadConfiguration(WorkspaceItem item) throws InternalErrorException {
		if(item.isFolder()) {
			Properties props=item.getProperties();
			SynchFolderConfiguration config=new SynchFolderConfiguration();
			config.setFilter(props.getPropertyValue(Constants.WorkspaceProperties.SYNCH_FILTER));
			config.setRemotePath(props.getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH));
			config.setRemotePersistence(props.getPropertyValue(Constants.WorkspaceProperties.REMOTE_PERSISTENCE));
			config.setTargetToken(props.getPropertyValue(Constants.WorkspaceProperties.TARGET_TOKEN));
			config.setToCreateCatalogName(props.getPropertyValue(Constants.WorkspaceProperties.RELATED_CATALOG));
			config.setValidateMetadata(Boolean.parseBoolean(props.getPropertyValue(Constants.WorkspaceProperties.VALIDATE_METADATA)));
			config.setRootFolderId(props.getPropertyValue(Constants.WorkspaceProperties.ROOT_FOLDER_ID));
			return config;
		}else return loadConfiguration(item.getParent());
	}
	
	static void resetStatus(WorkspaceItem item) throws InternalErrorException {
		if(item.isFolder()) {
			for(WorkspaceItem child: ((WorkspaceFolder)item).getChildren())
				resetStatus(child);
		}
		Map<String,String> props=item.getProperties().getProperties();
		if(props.containsKey(Constants.WorkspaceProperties.LAST_UPDATE_STATUS)) {
			props.put(Constants.WorkspaceProperties.LAST_UPDATE_STATUS, StepReport.Status.OK+"");
			item.getProperties().addProperties(props);
		}
	}
}
