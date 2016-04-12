package org.gcube.datatransfer.agent.impl.context;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.impl.event.TransferDBChecker;
import org.gcube.datatransfer.agent.impl.event.TransferRequestSubscription;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class AgentRIResourceConsumer extends ResourceConsumer{

	GCUBELog logger = new GCUBELog(this);

	/**
	 * {@inheritDoc}
	 */
	protected void onAddScope(GCUBEResource.AddScopeEvent event) {

		for (GCUBEScope scope: event.getPayload())
		{
			ScopeProvider.instance.set(scope.toString());
			
			logger.debug("RIResourceConsumer - onAddScope.. scope="+scope.toString());
			logger.trace("creating transfer subscription");		
			//		for (TransferTopics topic :TransferTopics.values()) {
			//			transferEventproducer.subscribe(new TransferEventConsumer(),topic);
			//		}
			
			
			if(ServiceContext.getContext().getUseMessaging()){
				// **** Setting MSG Client ****
				try {
					ServiceContext.getContext().setMsgClient(MSGClientFactory.getMSGClientInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// **** Transfer Request Subscription ****
				
				// sourceEndpoint such as "pcitgt1012:8081" for example;			
				EndpointReferenceType endpoint = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent");	
				String address = endpoint.getAddress().toString();
				String sourceEndpoint=address;
				//we keep only the host name and the port
				String[] parts = address.split("/");
				if(parts.length>=3){
					sourceEndpoint = parts[0]+"//"+parts[2];
				}
				
				logger.trace("current address of agent service: "+sourceEndpoint);	
				TransferRequestSubscription subscriber = new TransferRequestSubscription(sourceEndpoint);
				subscriber.setScope(scope);
				try {
					subscriber.subscribe();
				} catch (Exception e) {
					e.printStackTrace();
				}				
				// **** TransferDBChecker Thread **** 
				TransferDBChecker checker = new TransferDBChecker();
				checker.run();
			}		
		}
	}
}