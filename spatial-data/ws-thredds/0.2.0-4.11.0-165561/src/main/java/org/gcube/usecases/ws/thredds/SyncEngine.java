package org.gcube.usecases.ws.thredds;

import java.io.File;
import java.util.Set;

import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.engine.impl.SynchEngineImpl;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo;
import org.gcube.usecases.ws.thredds.model.gui.CatalogBean;

public interface SyncEngine {

	public static SyncEngine get() {
		return SynchEngineImpl.get();
	}
	
	
	public SyncFolderDescriptor check(String folderId, boolean recursively) throws WorkspaceInteractionException, InternalException;
	public void registerCallBack(String folderId,SyncOperationCallBack callback) throws ProcessNotFoundException;
	public ProcessDescriptor doSync(String folderId) throws WorkspaceInteractionException, InternalException;
	public void stopSynch(String folderId) throws ProcessNotFoundException;
	
	public void setSynchronizedFolder(SynchFolderConfiguration config,String folderId) throws WorkspaceInteractionException, InternalException;
	public void unsetSynchronizedFolder(String folderId,boolean deleteRemoteContent) throws WorkspaceInteractionException, InternalException;
	
	public SynchronizedElementInfo getInfo(String elementId);
	
	public void updateCatalogFile(String folderId, File toUpdate) throws InternalException;
	
	public void forceUnlock(String folderId)throws InternalException, WorkspaceInteractionException;


	public void shutDown();
	
	
	public ProcessDescriptor getProcessDescriptorByFolderId(String folderId)throws ProcessNotFoundException;
	public ProcessStatus getProcessStatusByFolderId(String folderId)throws ProcessNotFoundException;
	
	
	
	public void setRequestLogger(String path);
	public boolean isRequestLoggerEnabled();
	public String getRequestLoggerPath();
	
	
	public Set<CatalogBean> getAvailableCatalogsByToken(String token) throws InternalException;
		
	
	
}
