package org.gcube.datatransfer.agent.impl.worker.async;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.handlers.LocalFileTransferAsyncHandler;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.worker.ASyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.OutUriData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

/**
 * 
 * @author andrea
 *
 */
public class LocalFileTransferASyncWorker extends ASyncWorker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public LocalFileTransferASyncWorker(String tranferID,SourceData source, DestData dest)   {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
	}

	@Override
	public Object call() throws Exception {
		logger.debug("---LocalFileTransferASyncWorker is being called ...");

		String [] urlInputs= sourceParameters.getInputURIs();
		String outPath = destParameters.getOutUri().getOutUris()[0];

		Set<TransferObject> transferObjects = new HashSet<TransferObject>();
		long totalsize = 0;
		
		try {
			totalsize = TransferUtils.getTotalSize(urlInputs);
		}	catch  (Exception e){
			logger.equals("Error getting object size");
			errorHappened =true;
		}

		try {
			ServiceContext.getContext().getDbManager().updateTransferJDO(transferId,urlInputs,totalsize);
		}	catch  (Exception e){
			e.printStackTrace();
			logger.equals("Error updating the transfer ");
			errorHappened =true;
		}

		ThreadGroup threadList = null;
		try{
			threadList = new ThreadGroup(transferId);
			
			int nPartitions = urlInputs.length/ServiceContext.FILESXTHREAD;
			int mod = urlInputs.length%ServiceContext.FILESXTHREAD;
			
			int startIndex = 0;
			int endIndex = 0;

			logger.debug("Number of partitions : "+nPartitions );
			for (int j = 0 ; j< nPartitions; j++) {
				startIndex = j*ServiceContext.FILESXTHREAD;
				endIndex= startIndex+ServiceContext.FILESXTHREAD-1;

				LocalFileTransferAsyncHandler transferHandler =
						new LocalFileTransferAsyncHandler(urlInputs,outPath,transferId, 
								TransferType.LocalFileBasedTransfer,destParameters, startIndex, endIndex);
				list.add(transferHandler);
				Thread t = new Thread(threadList,transferHandler);
				t.start();
			}

			if (mod != 0){
				LocalFileTransferAsyncHandler transferHandler =
						new LocalFileTransferAsyncHandler(urlInputs,outPath,transferId, 
								TransferType.LocalFileBasedTransfer,destParameters, endIndex, endIndex+mod-1);
				list.add(transferHandler);
				Thread t = new Thread(threadList,transferHandler);
				t.start();
			}
		}	catch  (Exception e){
			e.printStackTrace();
			errorHappened =true;
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
		for (LocalFileTransferAsyncHandler t :(ArrayList<LocalFileTransferAsyncHandler>)list) {
			transferObjects.addAll(t.getTransferObjList());
			if (!errorHappened)
				errorHappened = t.isErrorHappened();
		}

		try{		
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
