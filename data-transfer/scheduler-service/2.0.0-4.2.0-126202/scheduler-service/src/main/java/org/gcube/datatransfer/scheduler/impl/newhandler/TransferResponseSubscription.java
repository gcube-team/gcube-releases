package org.gcube.datatransfer.scheduler.impl.newhandler;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.messaging.BrokerSubscription;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;
import org.gcube.datatransfer.common.messaging.utils.Utils;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseSubscription extends BrokerSubscription<TransferResponseListener>{
	private GCUBELog logger = new GCUBELog(this.getClass());

	
	public TransferResponseSubscription(String subscriberEndpoint) {
		super(subscriberEndpoint);
	}

	@Override
	public void setScope(GCUBEScope scope) {
		try{
		DestinationPair pair = new DestinationPair();
		pair.setScope(scope);
		
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
		pair.setQueue(true);
		setDestinationPair(pair);
		logger.debug("TransferResponseSubscription - topic="+topic);
		listener = new TransferResponseListener(scope,subscriberEndpoint);
		
		}catch(Exception e){
			e.printStackTrace();
			logger.error("TransferResponseSubscription - Exception...");
		}
	}

}
