package org.gcube.datatransfer.common.messaging.listeners;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.messaging.checkers.TransferResponseChecker;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseListener implements MessageListener{

	public  static GCUBELog logger = new GCUBELog(TransferResponseListener.class);

	private GCUBEScope scope = null;
	private String subscriberEndpoint=null;
	private TransferResponseChecker responseChecker;

	public TransferResponseListener(){}

	public TransferResponseListener (GCUBEScope scope, String subscriberEndpoint){
		logger.debug("The TransferResponseListener has been created ...");
		this.scope = scope;
		this.subscriberEndpoint=subscriberEndpoint;
		responseChecker= new TransferResponseChecker(this.scope,this.subscriberEndpoint);

	}

	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage ) message;
				if (msg.getObject() instanceof TransferResponseMessage){
					//check messages
					logger.debug("TransferResponseListener (onMessage) - a new message has just arrived...");
					TransferResponseMessage res_msg = (TransferResponseMessage ) msg.getObject();
					responseChecker.check(res_msg);
				}
				else logger.debug("TransferResponseListener (onMessage) - a new message has just arrived but its not TransferResponseMessage..");
			} 
			else logger.debug("TransferResponseListener (onMessage) - a new message has just arrived but its not ObjectMessage...");
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
}
