package org.gcube.usecases.ws.thredds.engine.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process {

	private ProcessDescriptor descriptor;
	private ProcessStatus status;
	
	private String processId=UUID.randomUUID().toString();
	
	private String folderId;
	
	private WorkspaceFolderManager manager;
	
	private List<SyncOperationCallBack> toInvokeCallbacks=new CopyOnWriteArrayList<>();
	
	
	
	public Process(String folderId) throws WorkspaceInteractionException, InternalException {
		log.debug("Created Process with id {} ",processId);
		this.folderId=folderId;
		manager=new WorkspaceFolderManager(folderId);
		manager.lock(processId);
	}
	
	public void startProcess() {
		// invoke check (recursive)
		// scan for requests
	}
	
	
	//*** Describe the queue to which transfer processes notify their progress
	
	
	
	public void addCallBack(SyncOperationCallBack toAddCallback) {		
		toInvokeCallbacks.add(toAddCallback);
		log.debug("Added callback for process {}. Current callback size is {}",processId,toInvokeCallbacks.size());
	}
	
	public ProcessDescriptor getDescriptor() {
		return descriptor;
	}
	
	// signals from serving threads
	public void onStep() {
		// update status
		
		
//		for(SyncOperationCallBack )
	}
	
	
	public void cancel() {
		
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
	
	
	
}
