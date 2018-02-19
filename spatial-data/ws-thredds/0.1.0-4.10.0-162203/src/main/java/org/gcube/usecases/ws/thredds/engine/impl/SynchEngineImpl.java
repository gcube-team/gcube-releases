package org.gcube.usecases.ws.thredds.engine.impl;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.gcube.usecases.ws.thredds.SyncEngine;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SyncOperationTicket;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo;

public class SynchEngineImpl implements SyncEngine{

	
	
	
	//folder ID -> Process
	private ConcurrentHashMap<String, Process> localProcesses;
	
	
	private ExecutorService service=null;
	
	
	
	
	
	
	@Override
	public SyncFolderDescriptor check(String folderId, boolean recursively) throws WorkspaceInteractionException, InternalException {
		new WorkspaceFolderManager(folderId).check(recursively);
		return null;
	}

	@Override
	public void registerCallBack(String folderId, SyncOperationCallBack callback) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under processes");
			localProcesses.get(folderId).addCallBack(callback);
	}

	@Override
	public SyncOperationTicket doSync(String folderId) {		
		//lock
		//create process
		//start process
		//return ticket
		return null;
	}

	@Override
	public void stopSynch(String folderId) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under processes");
		localProcesses.get(folderId).cancel();
	}

	
	@Override
	public void setSynchronizedFolder(SynchFolderConfiguration config,String folderId) throws WorkspaceInteractionException, InternalException {
		new WorkspaceFolderManager(folderId).configure(config);
	}

	@Override
	public void unsetSynchronizedFolder(String folderId,boolean deleteRemoteContent) throws WorkspaceInteractionException, InternalException {		
		new WorkspaceFolderManager(folderId).dismiss(deleteRemoteContent);
	}

	@Override
	public SynchronizedElementInfo getInfo(String elementId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCatalogFile(String folderId, File toUpdate) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
