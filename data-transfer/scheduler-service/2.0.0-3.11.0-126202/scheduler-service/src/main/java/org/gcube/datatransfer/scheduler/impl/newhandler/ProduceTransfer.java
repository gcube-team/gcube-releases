package org.gcube.datatransfer.scheduler.impl.newhandler;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.is.ISManager;

import com.thoughtworks.xstream.XStream;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class ProduceTransfer extends Thread {
	private Transfer transfer;
	private String submitter;
	private String transferId;
	private DataTransferDBManager dbManager;
	private SchedulerResource resource;
	private String transferType;

	private List<String> errorsInTransfer = new ArrayList<String>();
	private Agent agent;
	private String hostAgent;
	private int portAgent;
	private GCUBELog logger = new GCUBELog(this);
	private MSGClient msgClient;
	private ServiceContext context;
	private GCUBEScope scope;
	private ISManager isManagerForAgents;



	public ProduceTransfer(String transferId, GCUBEWSResource ws,boolean isPeriodically){
		this.dbManager=ServiceContext.getContext().getDbManager();
		this.transferId=transferId;		
		this.resource=(SchedulerResource) ws;
		this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
		this.agent=null;
		this.submitter=this.transfer.getSubmitter();		
		this.transferType=this.transfer.getTransferType();
		this.msgClient=ServiceContext.getContext().getMsgClient();
		this.context=ServiceContext.getContext();
		this.scope=GCUBEScope.getScope(transfer.getScope());
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();

	}

	public void run() {	

		ScopeProvider.instance.set(scope.toString());


		logger.debug("\nTransferHandler(Version With Messaging) - "+this.resource.getName()+" -- Thread has started");

		String agentId = this.transfer.getAgentId();
		if(agentId==null){
			logger.error("TransferHandler - Error - agentId=null");
			errorsInTransfer.add("TransferHandler - Error - agentId=null");
			SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED",errorsInTransfer);
			return;
		}
		//retrieving the Agent by checking first if it exists 
		Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
		Iterator<?> iter = resultExtent.iterator();
		boolean flagExists=false;
		while (iter.hasNext()){
			Agent obj=(Agent)iter.next();
			if(obj.getAgentId().compareTo(agentId)==0){
				agent=obj;
				hostAgent=obj.getHost();
				portAgent=obj.getPort();
				flagExists=true;
				break;
			}
		}
		if(flagExists==false){
			//then we should check for the agent in DB by host name because maybe it has been 
			//deleted/or deleted and then stored again(updated)

			String checkResultFromDB=null;
			String hostnameOfAgent=transfer.getAgentHostname();
			checkResultFromDB=this.isManagerForAgents.checkIfObjExistsInDB_ByHostname(hostnameOfAgent);
			if(checkResultFromDB!=null){
				try {
					this.dbManager.updateAgentInTransfer(transferId,checkResultFromDB);
				} catch (Exception e) {
					e.printStackTrace();
				}
				agent=null;
				//retrieving the Agent again...
				Extent<?> resultExtent2 = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
				Iterator<?> iter2 = resultExtent2.iterator();
				flagExists=false;
				while (iter2.hasNext()){
					Agent obj=(Agent)iter2.next();
					if(obj.getAgentId().compareTo(agentId)==0){
						agent=obj;
						hostAgent=obj.getHost();
						portAgent=obj.getPort();
						flagExists=true;
						break;
					}
				}

				if(flagExists==false){
					String errorMsg="TransferHandler -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
					logger.error(errorMsg);
					errorsInTransfer.add(errorMsg);
					SchedulerUtils.updateStatusAndErrors(this.transferId, agent,"FAILED",errorsInTransfer);
					return;
				}
			}
			else{
				String errorMsg="TransferHandler -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
				logger.error(errorMsg);
				errorsInTransfer.add(errorMsg);
				SchedulerUtils.updateStatusAndErrors(this.transferId, agent,"FAILED",errorsInTransfer);
				return;
			}
		}

		if(hostAgent==null){
			logger.error("TransferHandler - Error - hostAgent=null");
			errorsInTransfer.add("TransferHandler - Error - hostAgent=null");
			SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED",errorsInTransfer);
			return;
		}
		//updating the agent statistics - increase the ongoing transfers
		SchedulerUtils.updateAgentStatistics("ONGOING",agent);

		//    *******TRANSFER*******    //
		if(this.transferType.compareTo("LocalFileBasedTransfer")==0){
			logger.error("TransferHandler - Error - LocalFileBasedTransfer is not used anymore");
			errorsInTransfer.add("TransferHandler - LocalFileBasedTransfer is not used anymore");
			SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED",errorsInTransfer);
			return;
		}
		else if(this.transferType.compareTo("FileBasedTransfer")==0)fileBasedProcess();	
		else if(this.transferType.compareTo("TreeBasedTransfer")==0)treeBasedProcess();
	}

	public void fileBasedProcess(){
		logger.debug("TransferHandler -- (treeBasedProcess)...");
		String transferIdOfAgent = null;
		List<TransferObject> objectsToBeTransferred= new ArrayList<TransferObject>();

		Query query=null;
		List<TransferObject> list=null;
		//checking for the DB if it has finished storing the objs
		//if it has not in 2 minutes the transfer stops and the status becomes "FAILED"
		this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
		boolean flagReadyObjs= this.transfer.isReadyObjects();				
		int numSleeps=0;
		while(!flagReadyObjs){
			logger.debug("TransferHandler -- DB has not finished storing the objs.. sec="+numSleeps*6);
			sleepSixSec();
			this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
			flagReadyObjs= this.transfer.isReadyObjects();
			numSleeps++;
			if(numSleeps>20){
				logger.debug("TransferHandler -- DB did not store the objs in 120s .. transfer aborted-failed..");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED", null);
				return;
			}
		}

		query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(TransferObject.class);
		list = (List<TransferObject>) query.execute();

		String transferIdOfSpecific;

		logger.debug("TransferHandler -- listOfObjects="+list.size()+" -- transferId="+this.transferId);

		for(TransferObject obj : list){
			transferIdOfSpecific = obj.getTransferid();
			if(transferIdOfSpecific==null)continue;

			if(transferIdOfSpecific.compareTo(this.transferId)==0){
				objectsToBeTransferred.add(obj); 				
			}
		}
		logger.debug("TransferHandler -- listOfObjectsToBeTransferrwes="+objectsToBeTransferred.size());

		//first needed input for the agent method
		ArrayList<URI> inputUris= new ArrayList<URI>();				
		for(TransferObject obj : objectsToBeTransferred){			
			try {				
				inputUris.add(new URI(obj.getSrcURI()));
			} catch (URISyntaxException  e) {
				e.printStackTrace();
			}
		}	

		storageType typeOfStorage=null;
		StorageManagerDetails storageManagerDetails = new StorageManagerDetails();
		logger.debug("TransferHandler -- scooooooooope="+scope);

		// sourceEndpoint such as "pcitgt1012:8080" for example;			
		EndpointReferenceType endpoint = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint("gcube/datatransfer/scheduler/Scheduler");
		String address = endpoint.getAddress().toString();
		String sourceEndpoint=address;
		//we keep only the host name and the port
		String[] parts = address.split("/");
		if(parts.length>=3){
			sourceEndpoint = parts[0]+"//"+parts[2];
		}
		//destEndpoint
		String destEndpoint = "http://"+hostAgent+":"+portAgent;

		TransferRequestMessage transferRequestMessage = new TransferRequestMessage();
		transferRequestMessage.setTransferId(transferId);
		transferRequestMessage.setSourceEndpoint(sourceEndpoint);
		transferRequestMessage.setDestEndpoint(destEndpoint);

		transferRequestMessage.setScope(scope.toString());
		transferRequestMessage.setInputUris(inputUris);


		//transfer to the local GHN
		if(this.transfer.getTypeOfStorage().compareTo(storageType.LocalGHN.name())==0){					
			typeOfStorage=storageType.LocalGHN;
			logger.debug("TransferHandler -- storageType=LocalGHN");
			//second needed input for the agent method
			String destinationFolder = this.transfer.getDestinationFolder();

			//third input for the agent method
			TransferOptions transferOptions= new TransferOptions();				
			transferOptions.setType(typeOfStorage);
			transferOptions.setOverwriteFile(this.transfer.isOverwrite());
			transferOptions.setUnzipFile(this.transfer.isUnzipFile());
			transferOptions.setStorageManagerDetails(storageManagerDetails);

			//fill the rest message
			transferRequestMessage.setDestination(destinationFolder);
			transferRequestMessage.setTransferOptions(SchedulerUtils.fillTransferOptions(transferOptions));
			transferRequestMessage.createTopicName(scope);

			// SEND IT 
			try {
				msgClient.sendRequestMessage(context, transferRequestMessage, scope);
			}catch (Exception e) {
				e.printStackTrace();
				logger.debug("TransferHandler -- Exception when call msgClient.sendRequestMessage ...");
				errorsInTransfer.add("TransferHandler -- IllegalArgumentException when call msgClient.sendRequestMessage ...");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED", errorsInTransfer);
				return;
			}
			logger.debug("TransferHandler -- Message sent...");

		}
		//transfer to the MongoDB
		else if(this.transfer.getTypeOfStorage().compareTo(storageType.StorageManager.name())==0){
			// now storageManagerDetails has to be filled with the real info
			DataStorage storage = null;
			String storageId = this.transfer.getStorageId();
			//retrieving the data storage by checking first if it exists 
			Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(DataStorage.class, true);
			Iterator<?> iter = resultExtent.iterator();
			boolean flagExists=false;
			while (iter.hasNext()){
				DataStorage obj=(DataStorage)iter.next();
				if(obj.getDataStorageId().compareTo(storageId)==0){
					storage=obj;
					flagExists=true;
					break;
				}
			}
			if(flagExists==false){
				logger.error("TransferHandler - Error - datastorage with id="+storageId+" does not exist in DB anymore");
				errorsInTransfer.add("TransferHandler - Error - datastorage with id="+storageId+" does not exist in DB anymore");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED",errorsInTransfer);
				return;
			}

			typeOfStorage=storageType.StorageManager;
			logger.debug("TransferHandler -- storageType=StorageManager");
			storageManagerDetails.setOwner(storage.getOwner());
			storageManagerDetails.setServiceName(storage.getServiceName());
			storageManagerDetails.setServiceClass(storage.getServiceClass());
			storageManagerDetails.setAccessType(storageAccessType.valueOf(storage.getAccessType()));

			//second needed input for the agent method
			String destinationFolder = this.transfer.getDestinationFolder();

			//third input for the agent method
			TransferOptions transferOptions= new TransferOptions();				
			transferOptions.setType(typeOfStorage);
			transferOptions.setOverwriteFile(this.transfer.isOverwrite());
			transferOptions.setUnzipFile(this.transfer.isUnzipFile());
			transferOptions.setStorageManagerDetails(storageManagerDetails);

			//fill the rest message
			transferRequestMessage.setDestination(destinationFolder);
			transferRequestMessage.setTransferOptions(SchedulerUtils.fillTransferOptions(transferOptions));
			transferRequestMessage.createTopicName(scope);

			// SEND IT 
			try {
				msgClient.sendRequestMessage(context, transferRequestMessage, scope);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("TransferHandler -- Exception when call msgClient.sendRequestMessage ...");
				errorsInTransfer.add("TransferHandler -- Exception when call msgClient.sendRequestMessage ...");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED", errorsInTransfer);
				return;
			}
			logger.debug("TransferHandler -- Message sent...");


		}
		//transfer to a data storage 
		else if(this.transfer.getTypeOfStorage().compareTo(storageType.DataStorage.name())==0){
			typeOfStorage=storageType.DataStorage;
			logger.debug("TransferHandler -- storageType=DataStorage");
			//second needed input for the agent method
			ArrayList<URI> outputURIs=new ArrayList<URI>();
			for(TransferObject obj : objectsToBeTransferred){			
				try {
					outputURIs.add(new URI(obj.getDestUri()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}	
			//third input for the agent method
			TransferOptions transferOptions= new TransferOptions();				
			transferOptions.setType(typeOfStorage);
			transferOptions.setOverwriteFile(this.transfer.isOverwrite());
			transferOptions.setUnzipFile(this.transfer.isUnzipFile());
			transferOptions.setStorageManagerDetails(storageManagerDetails);

			//fill the rest message
			transferRequestMessage.setOutputUris(outputURIs);
			transferRequestMessage.setTransferOptions(SchedulerUtils.fillTransferOptions(transferOptions));
			transferRequestMessage.createTopicName(scope);

			// SEND IT 
			try {
				msgClient.sendRequestMessage(context, transferRequestMessage, scope);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("TransferHandler -- Exception when call msgClient.sendRequestMessage ...");
				errorsInTransfer.add("TransferHandler -- IllegalArgumentException when call msgClient.sendRequestMessage ...");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED", errorsInTransfer);
				return;
			}
			logger.debug("TransferHandler -- Message sent...");

		}
		//update start time
		updateStartTimeInTransfer();

	}

	public void treeBasedProcess(){
		try{
			logger.debug("TransferHandler -- (treeBasedProcess)...");
			this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
			String inputSourceID=transfer.getSourceId();
			String outputStorageId=transfer.getStorageId();
			String scope = transfer.getScope();
			String patternInputString= transfer.getPattern();

			// sourceEndpoint such as "pcitgt1012:8080" for example;			
			EndpointReferenceType endpoint = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint("gcube/datatransfer/scheduler/Scheduler");
			String address = endpoint.getAddress().toString();
			String sourceEndpoint=address;
			//we keep only the host name and the port
			String[] parts = address.split("/");
			if(parts.length>=3){
				sourceEndpoint = parts[0]+"//"+parts[2];
			}
			//destEndpoint
			String destEndpoint = "http://"+hostAgent+":"+portAgent;

			TransferRequestMessage transferRequestMessage = new TransferRequestMessage();
			transferRequestMessage.setTransferId(transferId);
			transferRequestMessage.setSourceEndpoint(sourceEndpoint);
			transferRequestMessage.setDestEndpoint(destEndpoint);

			transferRequestMessage.setScope(scope.toString());

			transferRequestMessage.setTreeSourceID(inputSourceID);
			transferRequestMessage.setTreeDestID(outputStorageId);
			transferRequestMessage.setTreePattern(patternInputString);
			transferRequestMessage.createTopicName(GCUBEScope.getScope(scope));

			// SEND IT 
			try {
				msgClient.sendRequestMessage(context, transferRequestMessage, GCUBEScope.getScope(scope));
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("TransferHandler -- (tree-based) Exception when call msgClient.sendRequestMessage ...");
				errorsInTransfer.add("TransferHandler --(tree-based) IllegalArgumentException when call msgClient.sendRequestMessage ...");
				SchedulerUtils.updateStatusAndErrors(this.transferId,agent, "FAILED", errorsInTransfer);
				return;
			}		
			logger.debug("TransferHandler -- (tree-based) Message sent...");
			//update start time
			updateStartTimeInTransfer();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("TransferHandler -- (tree-based) Exception...");
		}	
	}



	public void updateStartTimeInTransfer(){
		long startTime = System.currentTimeMillis();
		try {
			this.dbManager.updateStartTimeInTransfer(transferId, startTime);
		} catch (Exception e) {
			logger.debug("TransferHandler -- Exception when updating the start time ...");
			e.printStackTrace();
		}
	}
	public void sleepSixSec(){
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			logger.error("\nTransferHandler (sleepFiveSec)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}

}
