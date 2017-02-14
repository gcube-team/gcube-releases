package org.gcube.datatransfer.common.messaging.checkers;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.MessageChecker;
import org.gcube.datatransfer.common.messaging.listeners.TransferRequestListener;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class TransferRequestChecker extends MessageChecker<TransferRequestMessage>{

	private MSGClient client=null;
	public GCUBELog logger = new GCUBELog(TransferRequestChecker.class);

	public TransferRequestChecker (GCUBEScope scope, String subscriberEndpoint,MSGClient client ){
		super(scope,subscriberEndpoint);
		this.client=client;
	}

	public void check(TransferRequestMessage message){
		logger.debug("TransferRequestChecker - subscriber:"+subscriberEndpoint);
		// checking for the right subscriber
		String destEndpoint=message.getDestEndpoint();
		if(destEndpoint==null)return;
		else if(destEndpoint.compareTo(subscriberEndpoint)==0){
			logger.debug("TransferRequestChecker - subscriber:"+subscriberEndpoint+" - this msg is for me!"+
		"\ntest storage type parameter ="+message.getTransferOptions().getStorageType());
			//handle the msg and send response
			TransferResponseMessage msg = new TransferResponseMessage();
			msg.setSourceEndpoint(subscriberEndpoint);
			msg.setDestEndpoint(message.getSourceEndpoint());
			msg.setScope(scope.toString());
			MonitorTransferReportMessage resp = new MonitorTransferReportMessage();
			resp.setTransferStatus("TEST_DONE");
			msg.setMonitorResponse(resp);
			try {
				client.sendResponseMessage(null, msg, scope);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else{
			logger.debug("TransferRequestChecker - subscriber:"+subscriberEndpoint+" - this msg is for "+
		message.getDestEndpoint());
		}
		
	
	}
}
