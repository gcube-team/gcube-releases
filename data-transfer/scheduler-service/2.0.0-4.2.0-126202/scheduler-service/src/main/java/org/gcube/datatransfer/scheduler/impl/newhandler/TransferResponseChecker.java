package org.gcube.datatransfer.scheduler.impl.newhandler;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.messaging.MessageChecker;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.db.model.TransferOutcome;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.is.ISManager;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseChecker extends MessageChecker<TransferResponseMessage>{
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	private GCUBELog logger = new GCUBELog(TransferResponseChecker.class);
	private DataTransferDBManager dbManager=ServiceContext.getContext().getDbManager();
	private List<String> objectFailedIDs;
	private List<String> objectTrasferredIDs;
	List<String> errorsInTransfer;

	public TransferResponseChecker (GCUBEScope scope, String subscriberEndpoint){
		super(scope,subscriberEndpoint);
	}
	public void check(TransferResponseMessage message){
		try{
			// checking for the right subscriber
			String destEndpoint=message.getDestEndpoint();
			if(destEndpoint==null)return;
			else if(destEndpoint.compareTo(subscriberEndpoint)==0){
				String transferIdToServe=message.getTransferId();

				//if it's not ONGOING transfer in the db we skip it
				Query query = this.dbManager.getPersistenceManager().newQuery(Transfer.class);
				query.setFilter("transferId == \""+transferIdToServe+"\"");
				List<Transfer> list = (List<Transfer>)query.execute();
				if (list == null || list.size()==0){
					logger.debug("TransferResponseChecker - source(agent):"+message.getSourceEndpoint()
							+"\n"+"Warn: transferId="+transferIdToServe+" does not exists in the db..");
					return;
				}

				String status =list.get(0).getStatus();

				if(status==null || status.compareTo("ONGOING")!=0){
					logger.debug("TransferResponseChecker - source(agent):"+message.getSourceEndpoint()
							+"\n"+"Warn: transfer with transferId="+transferIdToServe+" is not ONGOING anymore!!");
					return;
				}
				else {
					logger.debug("TransferResponseChecker - source(agent):"+message.getSourceEndpoint()
							+"\n"+"transferId: "+transferIdToServe);
					handleResult(message);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void handleResult(TransferResponseMessage message){
		try{
			errorsInTransfer = new ArrayList<String>();
			String status=message.getTransferStatus();
			String transferId=message.getTransferId();
			if(transferId==null){logger.debug("TransferResponseChecker - handleResult() - transferId=null");return;}

			Transfer transfer=dbManager.getPersistenceManager().getObjectById(Transfer.class, transferId);
			String agentId=transfer.getAgentId();
			Agent agent=null;
			if(agentId==null){logger.debug("TransferResponseChecker - handleResult() - agentId=null");return;}

			//retrieving the Agent by checking first if it exists 
			Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
			Iterator<?> iter = resultExtent.iterator();
			boolean flagExists=false;
			while (iter.hasNext()){
				Agent obj=(Agent)iter.next();
				if(obj.getAgentId().compareTo(agentId)==0){
					agent=obj;
					flagExists=true;
					break;
				}
			}
			if(flagExists==false){
				//then we should check for the agent in DB by host name because maybe it has been 
				//deleted/or deleted and then stored again(updated)
				ISManager isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
				String checkResultFromDB=null;
				String hostnameOfAgent=transfer.getAgentHostname();
				checkResultFromDB=isManagerForAgents.checkIfObjExistsInDB_ByHostname(hostnameOfAgent);
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
							flagExists=true;
							break;
						}
					}				
					if(flagExists==false){
						logger.debug("TransferResponseChecker - handleResult() - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore");
						return;
					}
				}
				else{
					logger.debug("TransferResponseChecker - handleResult() - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore");
					return;
				}
			}

			String transferType = transfer.getTransferType();
			if(status.compareTo("STARTED")==0 || status.compareTo("QUEUED")==0){
				//monitor in progress result
				MonitorTransferReportMessage result = message.getMonitorResponse();
				if(result==null){
					logger.debug("TransferResponseChecker - handleResult() - monitor response is null");
					return;
				}			
				if(transferType.compareTo("TreeBasedTransfer")==0){
					logger.debug("TransferResponseChecker -- Ongoing ("+status+" in agent)- id="+transferId+" - treeBased ..");			
					//in transfer case we do not update any 'live/in-progress' fields.. 
				}
				else{
					logger.debug("TransferResponseChecker -- Ongoing ("+status+" in agent)- id="+transferId+" - bytesTransferred="+result.bytesTransferred);			
					//update transfer db objs with the monitor in progress results
					long total_size=result.getTotalBytes();
					long transferredBytes= result.getBytesTransferred();
					try {
						dbManager.updateTransferBytes(transferId, total_size, transferredBytes);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else{
				String retrievedStatus=transfer.getStatus();

				//if the transfer has been canceled in the meantime, we update the status and return
				//even if it's a periodically transfer we cancel it 
				if(retrievedStatus.compareTo("CANCELED")==0 || status.compareTo("CANCEL")==0){
					logger.debug("TransferResponseChecker -- Canceled - id="+transferId);			
					SchedulerUtils.updateStatusAndErrors(transferId,agent, "CANCELED", null);
					return;
				}

				if(transferType.compareTo("TreeBasedTransfer")!=0){
					//outcome result
					ArrayList<FileTransferOutcome> outcomes = message.getOutcomesResponse();
					if(outcomes!=null)getAndStoreOutcomes(transfer,transferId,outcomes);
					else logger.error("TransferResponseChecker - file outcome list is null! ");
				}
				else{
					TreeTransferOutcome treeOutcome = message.getTreeOutcomeResponse();
					if(treeOutcome!=null){
						try{
							//APPEND OUTCOME
							this.dbManager.updateTreeOutcomeInTransfer(transferId, treeOutcome.getException(),treeOutcome.getTotalReadTrees(), treeOutcome.getTotalWrittenTrees());
						} catch (Exception e) {
							logger.error("Exception when call dbManager.updateTreeOutcomesInTransfer ");
							errorsInTransfer.add(e.getMessage());
							e.printStackTrace();
						}	
						//in the tree based transfer we fake it ... 1 byte has been transfered just for showing that the progress
						//in the portlet is 100%
						ServiceContext.getContext().getDbManager().updateTransferBytes(transferId, 1, 1);

					}else logger.error("TransferResponseChecker - tree outcome is null! ");
				}

				if(status.compareTo("DONE")==0 ||
						status.compareTo("DONE_WITH_ERRORS")==0){
					String statusToBeStored=null;

					if(status.compareTo("DONE")==0)statusToBeStored="COMPLETED";
					else statusToBeStored="COMPLETED_WITH_ERRORS";

					SchedulerUtils.updateStatusAndErrors(transferId, agent,statusToBeStored,null);
					logger.debug("TransferResponseChecker -- "+statusToBeStored+" - "+transfer.getSubmitter()+" - id="+transferId);			

					SchedulerUtils.refreshPeriodicallyScheduledTransfer(transferId, transfer.getSubmitter(), agent);
				}
				else{
					SchedulerUtils.updateStatusAndErrors(transferId,agent, "FAILED", errorsInTransfer);
					logger.debug("TransferHandler -- Failed - "+transfer.getSubmitter()+" - id="+transferId);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					SchedulerUtils.refreshPeriodicallyScheduledTransfer(transferId, transfer.getSubmitter(), agent);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	

	}

	public void getAndStoreOutcomes(Transfer transfer,String transferId, ArrayList<FileTransferOutcome> outcomes){
		try{
			boolean flagReadyObjs= transfer.isReadyObjects();				
			int numSleeps=0;
			while(!flagReadyObjs){
				logger.debug("TransferHandler -- DB has not finished storing the objs.. sec="+numSleeps*6);
				sleepSixSec();
				transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, transferId);
				flagReadyObjs= transfer.isReadyObjects();
				numSleeps++;
				if(numSleeps>20){
					logger.debug("TransferHandler -- DB did not store the objs in 120s .. transfer completed but transfer Objs will not be updated..");
					break;
				}
			}
			//objects that have been transferred with this transfer id should be updated
			//inside the transfer objs and indicate which one have been failed or succeeded
			List<TransferObject> objectsBeenTransferred= new ArrayList<TransferObject>();

			Query  query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(TransferObject.class);
			List<TransferObject> list = (List<TransferObject>) query.execute();
			String transferIdOfSpecific;
			if(list==null)logger.debug("list with the related transfer objects is empty ");
			else{
				for(TransferObject obj : list){
					transferIdOfSpecific = obj.getTransferid();
					if(transferIdOfSpecific==null){
						logger.debug("some transfer object has null transfer id - we skip it ");
						continue;
					}
					if(transferIdOfSpecific.compareTo(transfer.getTransferId())==0){
						objectsBeenTransferred.add(obj); 				
					}
				}
			}

			objectFailedIDs=new ArrayList<String>();
			objectTrasferredIDs=new ArrayList<String>();
			int numOfObj=0;
			long total_size=0;
			long transferredBytes=0;

			List<String> tmpOutcomes=new ArrayList<String>();
			for (FileTransferOutcome outcome : outcomes){
				TransferOutcome transferOutcome = new TransferOutcome();
				String outcomeId = uuidgen.nextUUID();
				transferOutcome.setTransferOutcomesId(outcomeId);
				transferOutcome.setTransferId(transfer.getTransferId());
				transferOutcome.setSubmittedDateOfTransfer(transfer.getSubmittedDate());
				transferOutcome.setFileName(outcome.getFilename());
				transferOutcome.setException(outcome.getException());
				transferOutcome.setFailure(outcome.isFailure());
				transferOutcome.setSuccess(outcome.isSuccess());
				transferOutcome.setDest(outcome.getDest());
				transferOutcome.setTransferTime(outcome.getTransferTime()+"");
				transferOutcome.setTransferredBytesOfObj(outcome.getTransferredBytes().toString());
				transferOutcome.setSize(outcome.getTotal_size().toString());

				if(outcome.getTotal_size()!=null)total_size=total_size + outcome.getTotal_size();
				if(outcome.getTransferredBytes()!=null)transferredBytes=transferredBytes + outcome.getTransferredBytes();

				transferOutcome.setNumberOfOutcomeInThisTransfer(numOfObj);
				try {
					this.dbManager.storeTransferOutcomes(transferOutcome);
				} catch (Exception e) {
					logger.error("TransferResponseChecker - Exception when call dbManager.storeTransferOutcomes ");
					errorsInTransfer.add(e.getMessage());
					e.printStackTrace();
				}

				if(numOfObj==0)logger.debug("TransferResponseChecker -- getAndStoreOutcomes:");
				logger.debug("Exception: "+outcome.getException());
				logger.debug("FileName: "+ outcome.getFilename());
				logger.debug("Success?: "+ outcome.isSuccess());
				logger.debug("Failure?: "+ outcome.isFailure());

				if(transfer.getSourceId()==null){



					if(outcome.isFailure()==true){
						if(objectsBeenTransferred==null || objectsBeenTransferred.get(numOfObj)==null || objectsBeenTransferred.get(numOfObj).getObjectId()==null);//nothinh
						else objectFailedIDs.add(objectsBeenTransferred.get(numOfObj).getObjectId());			
					}
					else if (outcome.isSuccess()==true){
						if(objectsBeenTransferred==null || objectsBeenTransferred.get(numOfObj)==null || objectsBeenTransferred.get(numOfObj).getObjectId()==null);//nothing
						else objectTrasferredIDs.add(objectsBeenTransferred.get(numOfObj).getObjectId());			
					}
				}
				tmpOutcomes.add(outcomeId);
				numOfObj++;
			}

			//updating transferred bytes
			try {
				dbManager.updateTransferBytes(transfer.getTransferId(), total_size, transferredBytes);
			} catch (Exception e) {
				logger.error("TransferResponseChecker - Exception when call dbManager.updateTransferBytes ");
				errorsInTransfer.add(e.getMessage());
				e.printStackTrace();
			}

			List<String> listOutcomes=new ArrayList<String>();
			//APPEND OUTCOMES
			String[] arrayOutcomes=transfer.getOutcomes();
			if(arrayOutcomes!=null){
				for(String tmp:arrayOutcomes)listOutcomes.add(tmp);
			}
			
			listOutcomes.addAll(tmpOutcomes);

			try{
				this.dbManager.updateOutcomesInTransfer(transfer.getTransferId(), listOutcomes);
				
			} catch (Exception e) {
				logger.error("TransferResponseChecker - Exception when call dbManager.updateOutcomesInTransfer ");
				errorsInTransfer.add(e.getMessage());
				e.printStackTrace();
			}	
		}catch (Exception e) {
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
