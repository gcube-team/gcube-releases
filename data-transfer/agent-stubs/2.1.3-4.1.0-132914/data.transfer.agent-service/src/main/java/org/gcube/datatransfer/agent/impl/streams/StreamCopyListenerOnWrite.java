package org.gcube.datatransfer.agent.impl.streams;


import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.delegates.StreamListenerAdapter;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.common.outcome.TransferStatus;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class StreamCopyListenerOnWrite extends StreamListenerAdapter{

	protected GCUBELog logger = new GCUBELog(this.getClass());
	Counter writtenTreesCounter;
	String transferId;

	public StreamCopyListenerOnWrite(String transferId, Counter writtenTreesCounter){
		this.writtenTreesCounter= writtenTreesCounter;
		this.transferId=transferId;
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
			int writtenTrees = writtenTreesCounter.total;
			if(topic==null)logger.error("transfer topic == null");
			else if(topic.equals(TransferTopics.TRANSFER_FAIL)){				
				ServiceContext.getContext().getDbManager().updateWrittenTreesInTransfer(transferId,writtenTrees);
				logger.debug("total written trees = "+writtenTrees);
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.FAILED.toString() );
			}
			else if(topic.equals(TransferTopics.TRANSFER_CANCEL)){
				ServiceContext.getContext().getDbManager().updateWrittenTreesInTransfer(transferId,writtenTrees);
				logger.debug("total written trees = "+writtenTrees);
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.CANCEL.toString() );
			}
			else if(topic.equals(TransferTopics.TRANSFER_END)){
				ServiceContext.getContext().getDbManager().updateWrittenTreesInTransfer(transferId,writtenTrees);
				logger.debug("total written trees = "+writtenTrees);
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE.toString() );
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}




}