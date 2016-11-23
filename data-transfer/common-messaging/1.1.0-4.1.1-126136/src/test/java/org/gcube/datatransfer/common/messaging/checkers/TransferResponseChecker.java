package org.gcube.datatransfer.common.messaging.checkers;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.messaging.MessageChecker;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseChecker extends MessageChecker<TransferResponseMessage>{
	public GCUBELog logger = new GCUBELog(TransferResponseChecker.class);

	public TransferResponseChecker (GCUBEScope scope, String subscriberEndpoint){
		super(scope,subscriberEndpoint);
	}
	public void check(TransferResponseMessage message){
		logger.debug("TransferResponseMessage - subscriber:"+subscriberEndpoint);

		// checking for the right subscriber
		String destEndpoint=message.getDestEndpoint();
		if(destEndpoint==null)return;
		else if(destEndpoint.compareTo(subscriberEndpoint)==0){
			logger.debug("TransferResponseChecker - subscriber:"+subscriberEndpoint+" - this msg is for me!"+
		"\ntest transfer status parameter ="+message.getMonitorResponse().getTransferStatus());
		}
		else{
			logger.debug("TransferResponseChecker - subscriber:"+subscriberEndpoint+" - this msg is for "+
		message.getDestEndpoint());
		}
	}
}
