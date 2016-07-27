package org.gcube.data.spd.notifications;

import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.data.spd.stubs.UpdateNotificationType;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.w3c.dom.Element;

public class TestConsumer extends BaseNotificationConsumer {
	 
	//private GCUBEClientLog log;
 
	public void onNotificationReceived(NotificationEvent event){
		//log.debug(this.logid+"| notification received");
		try{
			UpdateNotificationType untw= (UpdateNotificationType) ObjectDeserializer.toObject((Element)event.getPayload().getMessageObject(),UpdateNotificationType.class);
			System.out.println(untw.getNode()+" "+untw.getOperation()+" "+untw.getScope() );
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}