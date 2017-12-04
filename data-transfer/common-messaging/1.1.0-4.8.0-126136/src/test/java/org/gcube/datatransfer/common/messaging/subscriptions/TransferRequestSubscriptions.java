package org.gcube.datatransfer.common.messaging.subscriptions;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.messaging.BrokerSubscription;
import org.gcube.datatransfer.common.messaging.listeners.TransferRequestListener;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;

import org.gcube.datatransfer.common.messaging.utils.Utils;

/**
 * 
 * @author Andrea Manzi
 *
 */
public class TransferRequestSubscriptions extends BrokerSubscription<TransferRequestListener>{
		
	public TransferRequestSubscriptions(String subscriberEndpoint) {
		super(subscriberEndpoint);
	}

	@Override
	public void setScope(GCUBEScope scope) {
		DestinationPair pair = new DestinationPair();
		pair.setScope(scope);
		pair.setQueue(true);
		
		String topic = null;
		if (scope.isInfrastructure()){
		topic = Utils.replaceUnderscore(scope.getName())+
			"."+TransferRequestMessage.dataTransferLabel +
			"."+subscriberEndpoint+
			".*";
		}
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
		    topic = Utils.replaceUnderscore(scope.getInfrastructure().getName())+
		    	"."+voName +
		    	"."+subscriberEndpoint+
				"."+TransferRequestMessage.dataTransferLabel +
				".*";
		}
		pair.setDestinationName(topic);
		setDestinationPair(pair);
	
		listener = new TransferRequestListener(scope,subscriberEndpoint);
	}

}
