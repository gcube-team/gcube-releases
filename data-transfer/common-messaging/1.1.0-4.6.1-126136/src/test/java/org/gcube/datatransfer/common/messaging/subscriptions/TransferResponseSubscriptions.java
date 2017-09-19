package org.gcube.datatransfer.common.messaging.subscriptions;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.messaging.BrokerSubscription;
import org.gcube.datatransfer.common.messaging.listeners.TransferResponseListener;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;
import org.gcube.datatransfer.common.messaging.utils.Utils;


/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseSubscriptions extends BrokerSubscription<TransferResponseListener>{
	public TransferResponseSubscriptions(String subscriberEndpoint) {
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
			"."+TransferResponseMessage.dataTransferLabel +
			".*";
		}
		
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
			topic = Utils.replaceUnderscore(scope.getInfrastructure().getName())+
				"."+voName+
				"."+TransferResponseMessage.dataTransferLabel +
				".*";
		}
		pair.setDestinationName(topic);
		setDestinationPair(pair);

		listener = new TransferResponseListener(scope,subscriberEndpoint);
	}

}
