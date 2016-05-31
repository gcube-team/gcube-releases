package org.gcube.datatransfer.agent.impl.event;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.utils.Utils;
import org.gcube.datatransfer.common.messaging.BrokerSubscription;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferRequestSubscription extends BrokerSubscription<TransferRequestListener>{
	private GCUBELog logger = new GCUBELog(TransferRequestSubscription.class);

	public TransferRequestSubscription(String subscriberEndpoint) {
		super(subscriberEndpoint);
	}

	@Override
	public void setScope(GCUBEScope scope) {
		DestinationPair pair = new DestinationPair();
		pair.setScope(scope);
		
		//changed !! we have a different queue for each agent
		String topic = null;
		if (scope.isInfrastructure()){
		topic = Utils.replaceUnderscore(scope.getName())+
			"."+TransferRequestMessage.dataTransferLabel +
			"."+subscriberEndpoint +
			".*";
		}
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
		    topic = Utils.replaceUnderscore(scope.getInfrastructure().getName())+
		    	"."+voName +
				"."+TransferRequestMessage.dataTransferLabel +
				"."+subscriberEndpoint +
				".*";
		}	
		
		pair.setDestinationName(topic);
		pair.setQueue(true);
		setDestinationPair(pair);
		
		logger.debug("TransferRequestSubscription - topic="+topic);
		listener = new TransferRequestListener(scope,subscriberEndpoint);
	}
}
