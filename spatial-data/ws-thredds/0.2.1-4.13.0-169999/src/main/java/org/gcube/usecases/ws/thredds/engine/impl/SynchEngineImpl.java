package org.gcube.usecases.ws.thredds.engine.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gcube.data.transfer.model.plugins.thredds.DataSetScan;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.LocalConfiguration;
import org.gcube.usecases.ws.thredds.SyncEngine;
import org.gcube.usecases.ws.thredds.engine.impl.threads.ProcessInitializationThread;
import org.gcube.usecases.ws.thredds.engine.impl.threads.RequestLogger;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceFolderNotRootException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.CompletionCallback;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo;
import org.gcube.usecases.ws.thredds.model.gui.CatalogBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchEngineImpl implements SyncEngine{

	private static SynchEngineImpl instance=null;


	public static synchronized SyncEngine get() {
		if(instance==null) {
			instance=new SynchEngineImpl();
		}
		return instance;
	}


	private SynchEngineImpl() {

		localProcesses=new ConcurrentHashMap<>();

		int scannerMaxSize=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.SCANNER_POOL_MAX_SIZE));
		int scannerCoreSize=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.SCANNER_POOL_CORE_SIZE));
		int scannerIdleMs=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.SCANNER_POOL_IDLE_MS));

		
		int transfersMaxSize=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.TRANSFERS_POOL_MAX_SIZE));
		int transfersCoreSize=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.TRANSFERS_POOL_CORE_SIZE));
		int transfersIdleMs=Integer.parseInt(LocalConfiguration.getProperty(Constants.Configuration.TRANSFERS_POOL_IDLE_MS));
		
		initializationExecutor= new ThreadPoolExecutor(scannerCoreSize, scannerMaxSize, scannerIdleMs,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		synchronizationExecutor=new ThreadPoolExecutor(transfersCoreSize, transfersMaxSize, transfersIdleMs,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
	}


	private String requestLoggerPath=null;

	@Override
	public void setRequestLogger(String path) {
		requestLoggerPath=path;
	}
	@Override
	public boolean isRequestLoggerEnabled() {
		return requestLoggerPath!=null;
	}
	@Override
	public String getRequestLoggerPath() {
		return requestLoggerPath;
	}


	//folder ID -> Process
	private ConcurrentHashMap<String, Process> localProcesses;


	private ExecutorService initializationExecutor=null;

	private ExecutorService synchronizationExecutor=null;


	private final CompletionCallback completionCallback=new CompletionCallback() {

		@Override
		public void onProcessCompleted(Process completedProcess) {
			try {
				ProcessDescriptor descriptor=completedProcess.getDescriptor();
				log.info("Process {} is completed. Going to cleanup.. ",descriptor);
				localProcesses.remove(descriptor.getFolderId());
				completedProcess.cleanup();
			}catch(Throwable t) {
				log.warn("Unable to cleanup {} ",completedProcess,t);
			}
		}
	};



	@Override
	public SyncFolderDescriptor check(String folderId, boolean recursively) throws WorkspaceInteractionException, InternalException {
		WorkspaceFolderManager manager=new WorkspaceFolderManager(folderId);
		return manager.check(recursively);
	}

	@Override
	public void registerCallBack(String folderId, SyncOperationCallBack callback) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under local processes");
		localProcesses.get(folderId).addCallBack(callback);
	}

	@Override
	public ProcessDescriptor doSync(String folderId) throws WorkspaceInteractionException, InternalException {
		if(localProcesses.containsKey(folderId))
			return localProcesses.get(folderId).getDescriptor();
		else {
			WorkspaceFolderManager manager=new WorkspaceFolderManager(folderId);
			if (!manager.isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not configured for synchronization.");
			if(manager.isLocked()) throw new WorkspaceLockedException("Folder "+folderId+"is locked by an external process.");
			if(!manager.isRoot()) throw new WorkspaceFolderNotRootException("Unable to launch synch operation. Folder "+folderId+" is not root configuration");
			Process toLaunch=new Process(folderId,completionCallback);
			localProcesses.put(folderId, toLaunch);			
			initializationExecutor.submit(new ProcessInitializationThread(toLaunch,synchronizationExecutor));			
			return toLaunch.getDescriptor();
		}		
	}

	@Override
	public void stopSynch(String folderId) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under local processes");
		localProcesses.get(folderId).cancel();
	}


	@Override
	public void setSynchronizedFolder(SynchFolderConfiguration config,String folderId) throws WorkspaceInteractionException, InternalException {

		// Check config
		if(config==null) throw new InternalException("Passed config is null : "+config);
		String remotePath=config.getRemotePath();
		if(remotePath==null||remotePath.isEmpty()||remotePath.startsWith("/"))
			throw new InternalException("Invalid remote path "+remotePath+".");

		new WorkspaceFolderManager(folderId).configure(config);
	}

	@Override
	public void unsetSynchronizedFolder(String folderId,boolean deleteRemoteContent) throws WorkspaceInteractionException, InternalException {		
		new WorkspaceFolderManager(folderId).dismiss(deleteRemoteContent);
	}

	@Override
	public SynchronizedElementInfo getInfo(String elementId) {
		return WorkspaceFolderManager.getInfo(elementId);
	}

	@Override
	public void updateCatalogFile(String folderId, File toUpdate) throws InternalException {
		File previousCatalogFile=null;
		try {
			WorkspaceFolderManager manager=new WorkspaceFolderManager(folderId);
			previousCatalogFile=manager.loadCatalogFile();
			String lockId=UUID.randomUUID().toString();
			manager.lock(lockId);
			manager.updateCatalogFile(toUpdate);
			manager.unlock(lockId);
		}catch(Throwable t) {
			log.warn("Unable to update catalogFile for {}. Trying to restore previous one..",folderId,t);
			throw new InternalException("Unable to restore previous catalog.",t);
			//TODO try to restore previous catalog
		}
	}

	@Override
	public void shutDown() {
		log.trace("Cancelling processes...");
		for(Entry<String,Process> entry:localProcesses.entrySet())
			entry.getValue().cancel();

		log.trace("Shutting down services... ");
		initializationExecutor.shutdown();
		synchronizationExecutor.shutdown();

		do {
			log.trace("Waiting for services to terminate..");
			try {Thread.sleep(1000l);
			} catch (InterruptedException e) {}
		}while(!initializationExecutor.isTerminated()||!synchronizationExecutor.isTerminated());

		RequestLogger.get().close();
		log.trace("Terminated.");
	}

	@Override
	public void forceUnlock(String folderId) throws InternalException, WorkspaceInteractionException {
		log.warn("Forcing unlock of {} ",folderId);
		new WorkspaceFolderManager(folderId).forceUnlock();
	}


	@Override
	public ProcessDescriptor getProcessDescriptorByFolderId(String folderId) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under processes or process is not in this host");
		return localProcesses.get(folderId).getDescriptor();
	}

	@Override
	public ProcessStatus getProcessStatusByFolderId(String folderId) throws ProcessNotFoundException {
		if(!localProcesses.containsKey(folderId)) throw new ProcessNotFoundException(folderId+" is not under processes or process is not in this host");
		return localProcesses.get(folderId).getStatus();
	}


	@Override
	public Set<CatalogBean> getAvailableCatalogsByToken(String token) throws InternalException {
		ThreddsController controller=new ThreddsController("",token);
		ThreddsInfo info=controller.getThreddsInfo();
		Set<CatalogBean> toReturn=asCatalogBeanSet(info.getCatalog());
		DataSetScan mainScan=info.getCatalog().getDeclaredDataSetScan().iterator().next();
		CatalogBean defaultBean=new CatalogBean(mainScan.getName(),mainScan.getLocation(),true);
		toReturn.remove(defaultBean);
		toReturn.add(defaultBean);
		
		//*** Cleaning : 
		// absolute paths to relative paths (from thredds persistence)
		// leading/ending '/'
		String threddsPersistencePath=info.getLocalBasePath();
		for(CatalogBean bean:toReturn) {
			String path=bean.getPath();
			if(path.startsWith(threddsPersistencePath))
				path=path.substring(threddsPersistencePath.length());
			if(path.startsWith("/")) path=path.substring(1);
			if(path.endsWith("/"))path=path.substring(0, path.length()-1);
			bean.setPath(path);
		}
		
		
		return toReturn;
	}

	private static HashSet<CatalogBean> asCatalogBeanSet(ThreddsCatalog catalog){
		HashSet<CatalogBean> toReturn=new HashSet<>();
		for(DataSetScan scan:catalog.getDeclaredDataSetScan())
			toReturn.add(new CatalogBean(scan.getName(),
					scan.getLocation(),false));
		if(catalog.getSubCatalogs()!=null&&catalog.getSubCatalogs().getLinkedCatalogs()!=null)
			for(ThreddsCatalog sub:catalog.getSubCatalogs().getLinkedCatalogs())
				toReturn.addAll(asCatalogBeanSet(sub));
		return toReturn;
	}

}
