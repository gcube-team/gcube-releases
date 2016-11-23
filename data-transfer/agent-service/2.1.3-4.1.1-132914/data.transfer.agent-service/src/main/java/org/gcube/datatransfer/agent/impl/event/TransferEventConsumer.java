package org.gcube.datatransfer.agent.impl.event;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;
import org.gcube.datatransfer.common.messaging.messages.TransferMessage;

/**
 * 
 * @author Andrea Manz(CERN)
 *
 */
public class TransferEventConsumer implements GCUBEConsumer<TransferTopics,Object> {
	
	GCUBELog logger = new GCUBELog(this.getClass());

	public <T1 extends TransferTopics, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
		if (events==null) return;
		for (GCUBEEvent<T1,P1> event : events) {
			TransferTopics topic = event.getTopic();
			switch (topic) {
				case TRANSFER_END : this.onTransfer(event);break;
				case TRANSFER_START : this.onTransfer(event);break;
				case TRANSFER_FAIL : this.onTransfer(event);break;
				case TRANSFER_CANCEL : this.onTransfer(event);break;

			}
		}			
	}

	synchronized protected <P1, T1 extends GCUBETopic> void onTransfer(GCUBEEvent<T1 , P1> event) {
		logger.debug(((TransferOutcome)event.getPayload()).getOutcome());
		if (ServiceContext.getContext().getUseMessaging())
			sendToMSG(event);
		
	}
	

	
	private  <P1, T1 extends GCUBETopic> void sendToMSG(GCUBEEvent<T1 , P1> event){
		MSGClient  client =null;	
		try {
			  client = MSGClientFactory.getMSGClientInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TransferOutcome outcome = ((TransferOutcome)event.getPayload());
		
		TransferMessage message = new TransferMessage();
		message.setTransferId(outcome.getTransferId());
		message.setDestID(outcome.getDestID());
		message.setSourceGHN(GHNContext.getContext().getHostnameAndPort());
		message.setSourceID(outcome.getSourceID());
		message.setTopic(TransferMessage.dataTransferLabel);
		message.setTransferOutcome(outcome.getOutcome());
		message.setTransferPhase(outcome.getTransferPhase());
		message.setTransferType(outcome.getTransferType().getValue());

		try {
			client.sendMessage(ServiceContext.getContext(), message, outcome.getScope());
		} 
		 catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
}
