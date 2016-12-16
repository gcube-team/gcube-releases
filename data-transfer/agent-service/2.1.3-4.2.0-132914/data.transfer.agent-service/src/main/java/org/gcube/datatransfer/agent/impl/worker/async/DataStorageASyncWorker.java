package org.gcube.datatransfer.agent.impl.worker.async;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.handlers.DataStorageTransferAsyncHandler;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.worker.ASyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;


/**
 * 
 * Andrea Manzi(CERN)
 */
public class DataStorageASyncWorker extends ASyncWorker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	public DataStorageASyncWorker(String tranferID,SourceData source, DestData dest)   {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
	}

	@Override
	public Object call() throws Exception {

		
		String [] urlInputs = sourceParameters.getInputURIs();
		String [] urlOutputs =  destParameters.getOutUri().getOutUris();
		
		Set<TransferObject> transferObjects = new HashSet<TransferObject>();
		long totalsize = TransferUtils.getTotalSize(urlInputs);
		
		ServiceContext.getContext().getDbManager().updateTransferJDO(transferId,urlInputs,totalsize);
		
		if (urlInputs.length != urlOutputs.length)
			throw new Exception("The input and output URI lists do not contain the same number of arguments");
		
		ThreadGroup threadList = new ThreadGroup(transferId);
		
		int nPartitions = urlInputs.length/ServiceContext.FILESXTHREAD;
		int mod = urlInputs.length%ServiceContext.FILESXTHREAD;
		int startIndex = 0;
		int endIndex = 0;
		
		for (int j = 0 ; j< nPartitions; j++) {
			startIndex = j*ServiceContext.FILESXTHREAD;
			endIndex= startIndex+ServiceContext.FILESXTHREAD-1;
			
			DataStorageTransferAsyncHandler transferHandler =
					new DataStorageTransferAsyncHandler(urlInputs,urlOutputs,transferId, 
							TransferType.FileBasedTransfer,destParameters, startIndex, endIndex);
			list.add(transferHandler);
			Thread t = new Thread(threadList,transferHandler);
			t.start();
			}
		
		if (mod != 0){
			DataStorageTransferAsyncHandler transferHandler =
					new DataStorageTransferAsyncHandler(urlInputs,urlOutputs,transferId, 
							TransferType.FileBasedTransfer,destParameters, endIndex, endIndex+mod-1);
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
			
		logger.debug("Getting transferOBJList");
		//getting transferObject
		for (DataStorageTransferAsyncHandler t :(ArrayList<DataStorageTransferAsyncHandler>)list){
			transferObjects.addAll(t.getTransferObjList());
			if (!errorHappened)
				errorHappened = t.isErrorHappened();

		}
			
		//persisting stuff
		try {
			logger.debug("Persisting objects");
			ServiceContext.getContext().getDbManager().storeTransferObject(transferObjects);
			if (errorHappened)
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE_WITH_ERRORS.name());
			else ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE.name());
		}
		catch (Exception e){
			ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.FAILED.name());
			e.printStackTrace();
			throw e;
			
		}
		return true;
	}


}


