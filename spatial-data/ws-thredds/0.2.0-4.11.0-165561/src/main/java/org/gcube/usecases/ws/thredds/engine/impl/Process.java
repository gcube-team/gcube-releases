package org.gcube.usecases.ws.thredds.engine.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus.Status;
import org.gcube.usecases.ws.thredds.engine.impl.threads.SynchronizationRequest;
import org.gcube.usecases.ws.thredds.engine.impl.threads.SynchronizationThread;
import org.gcube.usecases.ws.thredds.engine.impl.threads.TransferFromThreddsRequest;
import org.gcube.usecases.ws.thredds.engine.impl.threads.TransferToThreddsRequest;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.CompletionCallback;
import org.gcube.usecases.ws.thredds.model.StepReport;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo.SynchronizationStatus;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process {

	
	private boolean submittedRequests=false;

	private ProcessDescriptor descriptor;
	private ProcessStatus status;

	private String processId=UUID.randomUUID().toString();

	private Queue<StepReport> queuedReports=new LinkedList<>();


//	private String folderId;

	private WorkspaceFolderManager manager;

	private Set<SyncOperationCallBack> toInvokeCallbacks=ConcurrentHashMap.newKeySet();

	private CompletionCallback callback=null;

	public Process(String folderId,CompletionCallback callback) throws WorkspaceInteractionException, InternalException {
		log.debug("Created Process with id {} ",processId);
//		this.folderId=folderId;
		manager=new WorkspaceFolderManager(folderId);
		manager.lock(processId);
		SynchFolderConfiguration folderConfig=manager.getSynchConfiguration();

		try {
			descriptor=new ProcessDescriptor(folderId, manager.getTheFolder().getPath(),System.currentTimeMillis(),processId,folderConfig);			
		}catch(Exception e) {
			throw new WorkspaceInteractionException("Unable to read path from folder "+folderId,e);
		}

		this.callback=callback;

		status=new ProcessStatus();
	}


	public void launch(ExecutorService service) throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalErrorException, InternalException {
		WorkspaceUtils.resetStatus(manager.getTheFolder());	
		status.setCurrentMessage("Analyzing folder..");
		
		generateRequests(this,service, manager.getTheFolder());
		submittedRequests=true;
		if(status.getQueuedTransfers().get()>0) {
			status.setCurrentMessage("Waiting for requests ["+status.getQueuedTransfers().get()+"] to be served.");
			status.setStatus(Status.ONGOING);
			while(!queuedReports.isEmpty()) {
				onStep(queuedReports.remove());
			}
		}else {
			status.setCurrentMessage("Folder is up to date.");
			status.setStatus(Status.COMPLETED);
			callback.onProcessCompleted(this);
			invokeCallbacks();
		}
	}



	public void addCallBack(SyncOperationCallBack toAddCallback) {		
		toInvokeCallbacks.add(toAddCallback);
		log.debug("Added callback for process {}. Current callback size is {}",processId,toInvokeCallbacks.size());
	}

	public ProcessDescriptor getDescriptor() {
		return descriptor;
	}

	// signals from serving threads

	public void onStep(StepReport report) {
		if(!submittedRequests) {
			queuedReports.add(report);
		}else {
			// serve
			updateStatus(report);	
			if(isCompleted()) {
				try {
					manager.setLastUpdateTime();
				}catch(Throwable t) {
					log.error("Unable to update last update time.",t);
				}
				if(status.getStatus().equals(Status.WARNINGS))
					status.setCurrentMessage("Process completed with errors. Please check logs or retry.");
				else status.setCurrentMessage("Synchronization complete.");
				status.setStatus(Status.COMPLETED);
				callback.onProcessCompleted(this);
			}
			invokeCallbacks();
		}
	}

	private void invokeCallbacks() {

		for(SyncOperationCallBack callback:toInvokeCallbacks) {
			try {
				callback.onStep((ProcessStatus)status.clone(), (ProcessDescriptor)descriptor.clone());
			}catch(Throwable t) {
				log.warn("Unable to invoke callback {}.",callback,t);
			}
		}
	}




	private boolean isCompleted() {
		return (status.getErrorCount().get()+status.getServedTransfers().get()>=status.getQueuedTransfers().get());			
	}

	@Synchronized
	public void updateStatus(StepReport report) {
		log.debug("Logging report {} ",report);
		switch(report.getStatus()) {
		case CANCELLED :
		case ERROR:{
			status.getErrorCount().incrementAndGet();
			if(!status.getStatus().equals(Status.STOPPED))
				status.setStatus(Status.WARNINGS);
			break;
		}
		default : {
			status.getServedTransfers().incrementAndGet();
			break;
		}
		}
		status.getLogBuilder().append(
				String.format("%s - item [%s] %s: %s \n", Constants.DATE_FORMAT.format(new Date(report.getCompletionTime())),
						report.getElementName(),report.getStatus()+"",report.getMessage()));		
	}



	public ProcessStatus getStatus() {
		return status;
	}


	public void cancel() {
		if(status.getQueuedTransfers().get()>1) {
			status.setStatus(Status.STOPPED);
			status.setCurrentMessage("Process Stopped. Waiting for remaining requests to cancel..");
		}else {
			status.setStatus(Status.COMPLETED);
			status.setCurrentMessage("Process cancelled before it started.");
		}
		invokeCallbacks();
		callback.onProcessCompleted(this);

	}

	public void cleanup() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		manager.unlock(processId);		
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			cleanup();
		}catch(Throwable t) {
			log.warn("Exception while trying to cleanup {} ",this);
		}
	}


	private static final void generateRequests(Process ownerProcess,ExecutorService service,WorkspaceFolder toScanFolder ) throws InternalErrorException, InternalException{
		
		String folderPath=toScanFolder.getPath();
		
		log.info("Generating requests for folder {}",folderPath);
		log.debug("Process is {} ",ownerProcess.getDescriptor());
		Set<String> handledWorkspaceItemEntries=new HashSet<String>();
		
		SynchFolderConfiguration config=ownerProcess.getDescriptor().getSynchConfiguration();

		Set<String> remoteChildrenNames;
		Set<String> localChildrenNames=new HashSet<>();
		
		List<WorkspaceItem> localFolderChildren=toScanFolder.getChildren();
		for(WorkspaceItem item:localFolderChildren) {
			localChildrenNames.add(item.getName());
		}
		
		String relativePath=toScanFolder.getProperties().getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH);
		ThreddsController folderController=new ThreddsController(relativePath,config.getTargetToken());
		
		RemoteFileDescriptor folderDesc=null;
		try{
			folderDesc=folderController.getFileDescriptor();
		}catch(RemoteFileNotFoundException e) {
			log.debug("RemoteFolder {} doesn't exists. Creating it.. ",relativePath);
			folderController.createEmptyFolder(null);
			folderDesc=folderController.getFileDescriptor();
		}
		
		remoteChildrenNames=new HashSet<>(folderDesc.getChildren());
		
		
		//*********************** HANDLING ACCOUNTING ENTRIES
		
		Set<String> handledAccountingEntries=WorkspaceUtils.scanAccountingForStatus( toScanFolder, config, localChildrenNames, remoteChildrenNames, folderController, ownerProcess, service);
		
	
		
		//SCAN FOLDER CONTENT
		log.debug("Checking content of {} ",folderPath);
		for(WorkspaceItem item:localFolderChildren) {

			if(item.isFolder()) {
				// RECURSIVE ON SUB FOLDERS 
				generateRequests(ownerProcess,service,(WorkspaceFolder) item); 

			}else {
				Map<String,String> props=item.getProperties().getProperties();
				String itemId=item.getId();
				String itemName=item.getName();

				// REQUESTS ARE EVALUATED ON PROPERTIES (SET BY PREVIOUS SCAN)
				
				if(props.containsKey(Constants.WorkspaceProperties.TBS)&&(props.get(Constants.WorkspaceProperties.TBS)!=null)) {
					try {
						SynchronizationStatus status=SynchronizationStatus.valueOf(props.get(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS));
						log.trace("Found TBS item {}, name {}, status : ",item.getId(),item.getName(),status);
						SynchronizationRequest request=null;

						switch(status) {
						case OUTDATED_REMOTE : request= new TransferToThreddsRequest(ownerProcess,toScanFolder,item);
						break;
						case OUTDATED_WS : request = new TransferFromThreddsRequest(ownerProcess, item, toScanFolder, null);
						break;

						}
						if(request!=null) {
							// KEEP TRACK OF HANDLED ITEMS & LAUNCH
							service.execute(new SynchronizationThread(request));
							log.debug("Submitted request number {} ",ownerProcess.status.getQueuedTransfers().incrementAndGet());
						}else log.debug("Item is up to date");
						handledWorkspaceItemEntries.add(itemName);
					}catch(Throwable t) {
						log.error("Unable to submit request for {} ID {} ",itemName,itemId,t);
					}
				}
			}
		}
		
		// check items to be imported
		
		try {
			Set<String> toImportItems=WorkspaceUtils.scanRemoteFolder(folderDesc, handledAccountingEntries, handledWorkspaceItemEntries, toScanFolder, folderController, config, ownerProcess, service);
			log.debug("Checking if remote location contains folders to be imported...");
				for(String item:toImportItems) {
					if(folderController.getFileDescriptor(item).isDirectory()) {
						log.info("Creating folder {} under {} ",item,folderPath);
						try{
							WorkspaceFolder folder=toScanFolder.createFolder(item, "Imported from thredds");							
							WorkspaceUtils.initProperties(folder,relativePath+"/"+item , config.getFilter(), config.getTargetToken(),config.getToCreateCatalogName(),config.getValidateMetadata(),config.getRootFolderId());
							generateRequests(ownerProcess, service, folder);
						}catch(Throwable t) {
							log.error("Unable to import folder {} into {} ",item,folderPath);
						}
					}
				}
		}catch(InternalException e) {
			log.error("Unable to check remote content with config {} ",config,e);
		}
		
		
		log.info("All requests for {} synchronization have been submitted [count {} ]. ",folderPath,ownerProcess.status.getQueuedTransfers().get());
	}


}
