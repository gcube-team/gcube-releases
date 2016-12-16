package org.gcube.datatransfer.agent.impl.event;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferRequestListener implements MessageListener{

	public  static GCUBELog logger = new GCUBELog(TransferRequestListener.class);

	private GCUBEScope scope = null;
	private String subscriberEndpoint=null;
	private TransferRequestChecker requestChecker;

	public TransferRequestListener(){}

	public TransferRequestListener (GCUBEScope scope, String subscriberEndpoint){
		logger.debug("The TransferRequestListener has been created ...");
		this.scope = scope;
		this.subscriberEndpoint=subscriberEndpoint;
		requestChecker= new TransferRequestChecker(this.scope,this.subscriberEndpoint);
	}

	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage ) message;
				if (msg.getObject() instanceof TransferRequestMessage){
					//check messages
					logger.debug("TransferRequestListener (onMessage) - a new message has just arrived...");
					TransferRequestMessage req_msg = (TransferRequestMessage ) msg.getObject();
					requestChecker.check(req_msg);
				}
				else logger.debug("TransferRequestListener (onMessage) - a new message has just arrived but its not TransferRequestMessage...");
			} 
			else logger.debug("TransferRequestListener (onMessage) - a new message has just arrived but its not ObjectMessage...");
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

}
