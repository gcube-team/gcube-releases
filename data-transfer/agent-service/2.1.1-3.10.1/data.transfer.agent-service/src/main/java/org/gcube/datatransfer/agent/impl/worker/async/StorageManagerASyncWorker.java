package org.gcube.datatransfer.agent.impl.worker.async;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.handlers.StorageManagerAsyncHandler;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.worker.ASyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageAccessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageManagerDetails;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

/**
 * 
 * 
 * @author andrea
 *
 */
public class StorageManagerASyncWorker extends ASyncWorker {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private IClient client;
	

	public StorageManagerASyncWorker(String id,SourceData source, DestData dest) throws Exception {
		this.sourceParameters = source;
		this.destParameters = dest;
		this.transferId = id;

		StorageManagerDetails details = dest.getOutUri().getOptions().getStorageManagerDetails();

		if (details == null)
			throw new Exception("Missing storage Manager configuration details");
		StorageAccessType accessType = details.getAccessType();
		if (accessType == null){
			logger.warn("Missing accessType from Storage Manager.. set to default:SHARED");
			accessType=StorageAccessType.SHARED;
		}
		
		//to add parameter to dest Data
		//new client (there is need to set the scope)
		ScopeProvider.instance.set(source.getScope().toString());
		
		client = new StorageClient(
				details.getServiceClass(), 
				details.getServiceName(),
				details.getOwner(),
				AccessType.valueOf(accessType.getValue())).getClient();
		logger.debug("StorageClient details: \nServiceClass="+details.getServiceClass()+"\n"+
				"ServiceName="+details.getServiceName()+"\n"+
				"Owner="+details.getOwner()+"\n"+
				"AccessType="+details.getAccessType().getValue()+"\n"+
				"Scope="+source.getScope()+"\n");

	}

	@Override
	public Object call() throws Exception{

		String [] urlInputs= sourceParameters.getInputURIs();
		String outPath = destParameters.getOutUri().getOutUris()[0];


		Set<TransferObject> transferObjects = new HashSet<TransferObject>();
		long totalsize = TransferUtils.getTotalSize(urlInputs);
		
		ServiceContext.getContext().getDbManager().updateTransferJDO(transferId,urlInputs,totalsize);
		

		try{		

			ThreadGroup threadList = new ThreadGroup(transferId);			
			
			int nPartitions = urlInputs.length/ServiceContext.FILESXTHREAD;
			int mod = urlInputs.length%ServiceContext.FILESXTHREAD;
			int startIndex = 0;
			int endIndex = 0;
			
			
			for (int j = 0 ; j< nPartitions; j++) {
				startIndex = j*ServiceContext.FILESXTHREAD;
				endIndex= startIndex+ServiceContext.FILESXTHREAD-1;
				
				StorageManagerAsyncHandler transferHandler =
						new StorageManagerAsyncHandler(urlInputs,outPath,transferId, 
								TransferType.FileBasedTransfer,client,destParameters,startIndex, endIndex);
				list.add(transferHandler);
				Thread t = new Thread(threadList,transferHandler);
				t.start();
			}
			
			if (mod != 0){
				StorageManagerAsyncHandler transferHandler =
						new StorageManagerAsyncHandler(urlInputs,outPath,transferId, 
								TransferType.FileBasedTransfer,client,destParameters, endIndex, endIndex+mod-1);
				list.add(transferHandler);
				Thread t = new Thread(threadList,transferHandler);
				t.start();
				
			}
			this.setThreadList(threadList);
			
			try {
				Thread tga[] = new Thread[threadList.activeCount()]; 	
				threadList.enumerate(tga);

				for (Thread t : tga){
					logger.debug("waiting for thread" + t.getId()); 
					t.join();
				}

			}
			catch (Exception e){
				e.printStackTrace();
				throw e;

			}

			logger.debug("Getting transferOBJ");
			//getting transferObject
			for (StorageManagerAsyncHandler t :(ArrayList<StorageManagerAsyncHandler>)list){
				transferObjects.addAll(t.getTransferObjList());
				if (!errorHappened)
					errorHappened = t.isErrorHappened();
			}
			
			logger.debug("Persisting objects");
			ServiceContext.getContext().getDbManager().storeTransferObject(transferObjects);
			if (errorHappened)
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE_WITH_ERRORS.name());
			else ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE.name());
	
		}
		catch (Exception e){
			ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.FAILED.name());
			e.printStackTrace();
			return e;
		}
		return true;
	}

}
