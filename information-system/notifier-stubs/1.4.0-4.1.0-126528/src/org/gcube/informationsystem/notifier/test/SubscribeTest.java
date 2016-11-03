package org.gcube.informationsystem.notifier.test;

import java.util.List;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.SubscribeMessage;
import org.gcube.informationsystem.notifier.stubs.service.NotifierServiceAddressingLocator;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;

/**
 * 
 * 
 * 
 * @author Andrea 
 *
 */
public class SubscribeTest implements NotifyCallback{
	
	
	static NotifierServiceAddressingLocator brokerLocator = new NotifierServiceAddressingLocator();
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void deliver(List topicPath, EndpointReferenceType producer,
			Object message) {
		ResourcePropertyValueChangeNotificationElementType notif_elem;
		ResourcePropertyValueChangeNotificationType notif;

		notif_elem = (ResourcePropertyValueChangeNotificationElementType) message;
		notif = notif_elem.getResourcePropertyValueChangeNotification();
		System.out.println("Deliver called");

		if (notif != null) {
			System.out.println("A notification has been delivered");
			System.out.print("New value: ");
			System.out.println(notif.getNewValue().get_any()[0].getValue());
		}
	}

	
	/**
	 * run method
	 * @param brokerEPR the DIS-Broker EPR
	 * @param topic the topic
	 */
	public void run (EndpointReferenceType brokerEPR,String topic){
		
		
//		// The NotificationConsumerManager sets up an endpoint where
		// notifications will be delivered.
		try {
		
		NotificationConsumerManager consumer;
					
		consumer = NotificationConsumerManager.getInstance();
	
		consumer.startListening();
		
		EndpointReferenceType consumerEPR = consumer.createNotificationConsumer(this);
		
		SubscribeMessage mess = new SubscribeMessage();
		mess.setEndpointReference(consumerEPR);
		mess.setTopic(topic);
		
		NotifierPortType brokerPT = brokerLocator.getNotifierPortTypePort(brokerEPR);
		
		brokerPT.subscribeToTopic(mess);

		
		// Loop forever and print notifications.
			System.out.println("Waiting for notification. Ctrl-C to end.");
			
			try {
				Thread.sleep(30000);
				} catch (Exception e) {
					System.out.println("Interrupted while sleeping.");
				}
			
			brokerPT.removeSubscription(mess);
			System.out.println("Removing subscription");
			while ( true){}

		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * main 
	 * @param args args
	 */
	public static void main(String[] args) {
		SubscribeTest client = new SubscribeTest();
		EndpointReferenceType brokerEPR = new EndpointReferenceType();
		
		try {
			if (args[0].startsWith("http")) {
				// First argument contains a URI
				String serviceURI = args[0];
				// Create endpoint reference to service
				brokerEPR = new EndpointReferenceType();
				brokerEPR.setAddress(new Address(serviceURI));
			} else 
				
			{
				System.out.println("Please insert dis-broker Address");
			}
		}catch (Exception e) {
			
			System.out.println("Error setting up EPR" + e);
		}
		
		client.run(brokerEPR,args[1]);
		
	}
	

}
