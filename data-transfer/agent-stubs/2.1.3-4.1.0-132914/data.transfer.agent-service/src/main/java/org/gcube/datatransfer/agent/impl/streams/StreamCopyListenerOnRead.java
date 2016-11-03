package org.gcube.datatransfer.agent.impl.streams;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.delegates.StreamListenerAdapter;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.agent.impl.event.TransferOutcome;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.common.outcome.TransferStatus;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class StreamCopyListenerOnRead extends StreamListenerAdapter{

	protected GCUBELog logger = new GCUBELog(this.getClass());
	SourceData sourceParameters;
	DestData destParameters;
	String transferId;
	Counter readTreesCounter;
	
	GCUBEEvent<TransferTopics,TransferOutcome> event = null;

	public StreamCopyListenerOnRead(String transferID,SourceData source, DestData dest, Counter readTreesCounter){
		this.transferId= transferID;
		this.sourceParameters = source;
		this.destParameters = dest;		
		this.readTreesCounter= readTreesCounter;
	}
	
	@Override
	public void onEnd() {
		sendEvent(TransferTopics.TRANSFER_END,"Transfer Completed");
	}
	
	@Override
	public void onStart() {
		sendEvent(TransferTopics.TRANSFER_START,"Transfer Started");	
	}


	public void sendEvent(TransferTopics topic ,String message){
		try{
			if(topic==null)logger.error("transfer topic == null");
			else{
				logger.debug("transfer topic = "+topic.name()+" -  read trees = "+readTreesCounter.total);
				int readTrees=readTreesCounter.total;
				if(topic.equals(TransferTopics.TRANSFER_START)){
					ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.STARTED.toString() );
				}
				else if(topic.equals(TransferTopics.TRANSFER_FAIL)){
					ServiceContext.getContext().getDbManager().updateReadTreesInTransfer(transferId,readTrees);
					logger.debug("total read trees = "+readTrees);
				}
				else if(topic.equals(TransferTopics.TRANSFER_CANCEL)){
					ServiceContext.getContext().getDbManager().updateReadTreesInTransfer(transferId,readTrees);
					logger.debug("total read trees = "+readTrees);
				}
				else if(topic.equals(TransferTopics.TRANSFER_END)){
					ServiceContext.getContext().getDbManager().updateReadTreesInTransfer(transferId,readTrees);
					logger.debug("total read trees = "+readTrees);
				}
			}
			
			event = new GCUBEEvent<TransferTopics,TransferOutcome> ();
			TransferOutcome outcome = new TransferOutcome();
			outcome.setTransferId(transferId);
			outcome.setDestID(destParameters.getOutSourceId());
			outcome.setOutcome(message);
			outcome.setScope(GCUBEScope.getScope(destParameters.getScope()));
			outcome.setSourceID(sourceParameters.getInputSource().getSourceId());
			outcome.setTransferPhase(topic.name());
			outcome.setTransferType(sourceParameters.getType());
			event.setPayload(outcome);
			ServiceContext.transferEventproducer.notify(topic, event);		
		}catch(Exception e){
			e.printStackTrace();
		}
	}




}