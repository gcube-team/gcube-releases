package org.gcube.datatransfer.scheduler.impl.state;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.DataSource;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.impl.check.CheckIS;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.newhandler.TransferResponseListener;
import org.gcube.datatransfer.scheduler.impl.newhandler.TransferResponseSubscription;
import org.gcube.datatransfer.scheduler.is.ISManager;


public class SchedulerRIResourceConsumer extends ResourceConsumer{

	GCUBELog logger = new GCUBELog(this);

	/**
	 * {@inheritDoc}
	 */
	protected void onAddScope(GCUBEResource.AddScopeEvent event) {

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (GCUBEScope scope: event.getPayload())
		{
			ScopeProvider.instance.set(scope.toString());

			//creating the ISManagerForAgents and setting it in the ServiceContext
			ISManager isManagerForAgents = new ISManager("Agent",ServiceContext.getContext().getDbManager(), scope.toString());
			ServiceContext.getContext().setIsManagerForAgents(isManagerForAgents);
			//creating the ISManagerForSources and setting it in the ServiceContext
			ISManager isManagerForSources = new ISManager("DataSource",ServiceContext.getContext().getDbManager(), scope.toString());
			ServiceContext.getContext().setIsManagerForSources(isManagerForSources);
			//creating the ISManagerForStorages and setting it in the ServiceContext
			ISManager isManagerForStorages = new ISManager("DataStorage",ServiceContext.getContext().getDbManager(), scope.toString());
			ServiceContext.getContext().setIsManagerForStorages(isManagerForStorages);

			logger.debug("RIResourceConsumer - onAddScope("+scope.toString()+").. just created a CheckISThread and three ISManagers(for Agents-Sources-Storages) in scope="+scope.toString());

			// **** CheckIS Thread ****
			CheckIS checkIS = new CheckIS();
			checkIS.start();
			
			boolean isMessagingEnabled = ServiceContext.getContext().isMessagingEnabled();
			logger.debug("RIResourceConsumer - onAddScope("+scope.toString()+").. isMessagingEnabled="+isMessagingEnabled);
			if(isMessagingEnabled){
				// **** Setting MSG Client ****
				ScopeProvider.instance.set(scope.toString());
				try {
					ServiceContext.getContext().setMsgClient(MSGClientFactory.getMSGClientInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(ServiceContext.getContext().getMsgClient()!=null){
					logger.debug("RIResourceConsumer - onAddScope("+scope.toString()+") - MSGClient has been created...");	
				}
				else {
					logger.error("RIResourceConsumer - onAddScope("+scope.toString()+") - MSGClient is not created !!");	
				}
				// **** Transfer Response Subscription ****
				// sourceEndpoint such as "pcitgt1012:8080" for example;			
				EndpointReferenceType endpoint = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint("gcube/datatransfer/scheduler/Scheduler");
				String address = endpoint.getAddress().toString();
				String sourceEndpoint=address;
				//we keep only the host name and the port
				String[] parts = address.split("/");
				if(parts.length>=3){
					sourceEndpoint = parts[0]+"//"+parts[2];
				}				
				
				logger.debug("RIResourceConsumer - onAddScope("+scope.toString()+") - Current address of scheduler service: "+sourceEndpoint);	
				TransferResponseSubscription subscriber = new TransferResponseSubscription(sourceEndpoint);
				subscriber.setScope(scope);
				try {
					subscriber.subscribe();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
}