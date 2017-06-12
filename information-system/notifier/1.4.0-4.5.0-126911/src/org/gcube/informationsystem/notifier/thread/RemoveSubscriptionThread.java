package org.gcube.informationsystem.notifier.thread;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.core.notification.SubscriptionManager;
import org.globus.wsrf.core.notification.service.SubscriptionManagerServiceAddressingLocator;
import org.oasis.wsrf.lifetime.Destroy;

public class RemoveSubscriptionThread extends Thread{

	private EndpointReferenceType notificationEPR;
	public static GCUBELog logger= new GCUBELog(RemoveSubscriptionThread.class);
	
	public RemoveSubscriptionThread(EndpointReferenceType notificationEPR){
		this.notificationEPR= notificationEPR;
	}
	
	public void run(){
		try{
			SubscriptionManagerServiceAddressingLocator subscriptLocator = new SubscriptionManagerServiceAddressingLocator();
			SubscriptionManager subPT= subscriptLocator.getSubscriptionManagerPort(this.notificationEPR);
			subPT.destroy(new Destroy());
		}catch (Exception e){
			logger.warn("error removing subscription");
		}
	}
	
}
