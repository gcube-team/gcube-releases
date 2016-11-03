package org.gcube.datatransfer.agent.impl.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;

import javax.jdo.Query;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.AgentContext;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.db.DataTransferDBManager;
import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.gcube.datatransfer.agent.impl.state.AgentResource;
import org.gcube.datatransfer.agent.impl.state.AgentResource.FutureWorker;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.utils.Utils;
import org.gcube.datatransfer.agent.impl.worker.Worker;
import org.gcube.datatransfer.agent.impl.worker.async.DataStorageASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.LocalFileTransferASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.StorageManagerASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.TreeManagerAsyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.GetTransferOutcomesFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.MonitorTransferFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.PostProcessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageAccessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageManagerDetails;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.postProcessType;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;


/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class AgentFunctions {
	protected final GCUBELog logger = new GCUBELog(this.getClass());

	public AgentFunctions(){}

	public AgentResource getResource() throws Exception {
		return (AgentResource) AgentContext.getContext().getAgent();
	}

	public  FutureTask<Worker>  startAsyncTask(String id,SourceData source ,DestData dest) {
		try{
			DataTransferDBManager dbManager = ServiceContext.getContext().getDbManager();
			logger.debug("startAsyncTask has been reached ... ");
			Worker worker = null;
			FutureTask<Worker> task = null;
			if (source.getType().getValue().compareTo(TransferType.TreeBasedTransfer.getValue()) ==0){
				worker= new  TreeManagerAsyncWorker(id, source, dest);
				//store transfer for the tree files
				if(dbManager.checkIfTransferExist(id)){
					//update
					dbManager.updateTransfer(id);
				}else{
					//new transfer - store
					dbManager.storeTransfer(TransferUtils.createTransferJDO(id,source.getInputSource().getSourceId(),dest.getOutSourceId()));
				}
			}
			else if (source.getType().getValue().compareTo(TransferType.FileBasedTransfer.getValue()) ==0) {
				if (dest.getOutUri().getOptions().getStorageType().getValue().compareTo(StorageType.StorageManager.getValue()) == 0)
					worker= new  StorageManagerASyncWorker(id,source, dest);
				else if (dest.getOutUri().getOptions().getStorageType().getValue().compareTo(StorageType.DataStorage.getValue()) == 0)
					worker= new  DataStorageASyncWorker(id,source, dest);
				else worker= new  LocalFileTransferASyncWorker(id,source, dest);
			
				//store transfer for the regular files
				if(dbManager.checkIfTransferExist(id)){
					//update
					dbManager.updateTransfer(id);
				}else{
					//new transfer - store
					dbManager.storeTransfer(TransferUtils.createTransferJDO(id));
				}
				
			}
			
			task = new FutureTask<Worker> (worker);
			worker.setTask(task);
			Thread t = new Thread(task);
			t.start();
			
			FutureWorker futureWorker = new AgentResource.FutureWorker();
			futureWorker.setFutureTask(task);
			futureWorker.setWorker(worker);
			
			getResource().getWorkerMap().put(id, futureWorker);	
			
			return task;
		} catch (Exception e) {
			logger.error("startAsyncTask - Exception");
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<FileTransferOutcome> getTransferOutcomes(String transferId) throws GetTransferOutcomesFault {
		
		if (transferId == null)
			throw Utils.newFault(new GetTransferOutcomesFault(), new Exception("The Transfer ID is null"));
		
		ArrayList<FileTransferOutcome> outcomes=null;
		try {
			outcomes = ServiceContext.getContext().getDbManager().getTransferObjectOutCome(transferId);
		}catch (Exception e){
			e.printStackTrace();
			throw  Utils.newFault(new GetTransferOutcomesFault(),e);
		}
		return outcomes;
	}
	
	public MonitorTransferReportMessage monitorTransferWithProgress(String transferId) throws MonitorTransferFault {
		
		if (transferId == null)
			throw Utils.newFault(new MonitorTransferFault(), new Exception("The Transfer ID is null"));
		
		MonitorTransferReportMessage message = null;
		try {
			message = ServiceContext.getContext().getDbManager().getTrasferProgressType(transferId);
		}catch (Exception e){
			e.printStackTrace();
			throw  Utils.newFault(new MonitorTransferFault(),e);
		}
		return message;
	}
	
	public static PostProcessType[] getPostProcessArray(List<postProcessType> list){
		if(list==null)return null;
		PostProcessType[] array=new PostProcessType[list.size()];
		int i=0;
		for(postProcessType type:list){
			if(type.equals(postProcessType.FileConversion)){
				array[i]=PostProcessType.FileConversion;
				i++;
			}
			else if(type.equals(postProcessType.FileUnzip)){
				array[i]=PostProcessType.FileUnzip;
				i++;
			}
			else if(type.equals(postProcessType.OriginalFileRemove)){
				array[i]=PostProcessType.OriginalFileRemove;
				i++;
			}
		}
		return array;
	}
	
	public static org.gcube.datatransfer.agent.stubs.datatransferagent.TransferOptions getMappedTransferOptions(org.gcube.datatransfer.common.agent.Types.TransferOptions optionsFromMessage){
		org.gcube.datatransfer.agent.stubs.datatransferagent.TransferOptions options = new org.gcube.datatransfer.agent.stubs.datatransferagent.TransferOptions();
		
		//set storage manager details
		StorageManagerDetails storageManagerDetails=new StorageManagerDetails();
		if(optionsFromMessage.getStorageManagerDetails()!=null){
			storageManagerDetails.setOwner(optionsFromMessage.getStorageManagerDetails().getOwner());
			storageManagerDetails.setServiceClass(optionsFromMessage.getStorageManagerDetails().getServiceClass());
			storageManagerDetails.setServiceName(optionsFromMessage.getStorageManagerDetails().getServiceName());
			StorageAccessType accessType=null;
			if(optionsFromMessage.getStorageManagerDetails().getAccessType()!=null){
				if(optionsFromMessage.getStorageManagerDetails().getAccessType().equals(storageAccessType.PRIVATE))accessType=StorageAccessType.PRIVATE;
				else if(optionsFromMessage.getStorageManagerDetails().getAccessType().equals(storageAccessType.SHARED))accessType=StorageAccessType.SHARED;
				else if(optionsFromMessage.getStorageManagerDetails().getAccessType().equals(storageAccessType.PUBLIC))accessType=StorageAccessType.PUBLIC;
			}
			storageManagerDetails.setAccessType(accessType);
		}
		options.setStorageManagerDetails(storageManagerDetails);
		
		//set storage type
		if(optionsFromMessage.getStorageType()!=null){
			StorageType type=null;
			if(optionsFromMessage.getStorageType().equals(storageType.LocalGHN))type=StorageType.LocalGHN;
			else if(optionsFromMessage.getStorageType().equals(storageType.DataStorage))type=StorageType.DataStorage;
			else if(optionsFromMessage.getStorageType().equals(storageType.StorageManager))type=StorageType.StorageManager;
			options.setStorageType(type);

			
		}
		//default timeout
		options.setTransferTimeout(3600000);
		options.setOverwrite(optionsFromMessage.isOverwrite());
		options.setPostProcess(AgentFunctions.getPostProcessArray(optionsFromMessage.getPostProcess()));
		if(optionsFromMessage.getConversionType()!=null){
			options.setConversionType(optionsFromMessage.getConversionType().toString());
		}
		return options;
	}
	
}
