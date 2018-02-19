package org.gcube.usecases.ws.thredds;

import java.io.File;

import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SyncOperationTicket;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo;

public interface SyncEngine {

	public SyncFolderDescriptor check(String folderId, boolean recursively) throws WorkspaceInteractionException, InternalException;
	public void registerCallBack(String folderId,SyncOperationCallBack callback) throws ProcessNotFoundException;
	public SyncOperationTicket doSync(String folderId);
	public void stopSynch(String folderId) throws ProcessNotFoundException;
	
	public void setSynchronizedFolder(SynchFolderConfiguration config,String folderId) throws WorkspaceInteractionException, InternalException;
	public void unsetSynchronizedFolder(String folderId,boolean deleteRemoteContent) throws WorkspaceInteractionException, InternalException;
	
	public SynchronizedElementInfo getInfo(String elementId);
	
	public void updateCatalogFile(String folderId, File toUpdate);
}
