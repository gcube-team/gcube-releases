package org.gcube.common.informationsystem.notification.impl.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationMessage;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.notification.impl.EPR;
import org.gcube.common.informationsystem.notification.impl.client.ISNotificationManager;
import org.gcube.common.informationsystem.notification.impl.client.ReNotifier;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.container.ContainerException;
import org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType;
import org.globus.wsrf.impl.notification.ClientNotificationConsumerManager;
import org.globus.wsrf.impl.notification.ServerNotificationConsumerManager;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;

/**
 * This class can be used to subscribe to process-instance-related events.
 * 
 * @author Christoph Langguth (UNIBAS),  Andrea Manzi (ISTI-CNR)
 *
 */
public class NotificationBroker implements NotifyCallback {
	
	private GCUBELog logger= new GCUBELog(NotificationBroker.class);
	
	
	/**
	 * Object logger.
	 */
    protected static  final GCUBELog log=new GCUBELog(NotificationBroker.class);
		
	/** The Globus consumer manager handling the requests */
	private NotificationConsumerManager consumerManager;
	
	/** own EPR for receiving notifications */
	private EndpointReferenceType myEpr;
	
	/** Map of a set of subscribers for each topic */
	private Map<QName, BaseNotificationConsumer> subscriber = Collections.synchronizedMap(new HashMap<QName, BaseNotificationConsumer>());
	
	protected List<GCUBENotificationTopic> relatedTopic= null;
	
	protected GCUBEProducer<NotificationTopic, NotificationMessage> producer;
	/** Map of a set of known producers for each topic */
	private Map<QName, Set<EPR>> knownProducers = Collections.synchronizedMap(new HashMap<QName, Set<EPR>>());
	
	
	/**
	 * Private constructor.
	 */
	protected NotificationBroker() throws Exception{
		try{
			this.relatedTopic= new ArrayList<GCUBENotificationTopic>();
			log.trace("Getting a notification consumerManager manager instance...");
			if (!GHNContext.getContext().isClientMode()){
				logger.debug("the GHN is server mode");
				consumerManager = new ServerNotificationConsumerManager();
			}
			else{
				logger.debug("the GHN is client mode");
				consumerManager= new ClientNotificationConsumerManager();
			}
			consumerManager.startListening();
						
			myEpr = consumerManager.createNotificationConsumer(this);
			log.trace("instanciated the NotificationBroker with epr  "+myEpr);
			producer= new GCUBEProducer<NotificationTopic, NotificationMessage>();
												
			log.info("Notification Broker is ready for action with epr "+myEpr);
		} catch (Exception e) {
			log.fatal("Error while initializing the Notification subscriber!",e);
			throw e;
		}
	}
	

	
	/**
	 * Subscribes for a topic using the Collective Layer Brokered Notification.
	 * 
	 * @param receiver a NotificationSubscriber that will receive the notifications
	 * @param topic the QName the subscriber is interested in
	 * @param getPastNotifications set to true to also retrieve past notifications
	 * @param manager GCUBESecurityManager 
	 */
	@SuppressWarnings("unchecked")
	public void subscribeForAnyTopic(BaseNotificationConsumer receiver, GCUBENotificationTopic topic, GCUBESecurityManager manager, GCUBEScope scope) {
		
		log.debug("subscribe for any topic");
		try {
			this.relatedTopic.add(topic);
			BaseNotificationConsumer topicSubscriber = subscriber.get(topic);
				
			if (topicSubscriber == null) {
					// we only need to set up our subscription once, since we're again an intermediate broker.
										
				subscriber.put(topic.getTopicQName(), receiver);
					
				log.trace("Subscribing to IS about topic "+topic);
					
					
				ISNotificationManager managerIS = new ISNotificationManager(scope);
											
				EndpointReferenceType[] producers = managerIS.subscribeEPRToTopic(myEpr,topic,manager);
				if (producers == null) producers = new EndpointReferenceType[0];
				for (int i= 0; i < producers.length; i++) {
						insertKnownProducer(topic.getTopicQName(), producers[i]);
				}
			}
				
				
			// if the subscriber wants to receive past notifications, create a thread
			// which requests them and returns them to the client
			if (topic.isUseRenotifier()) {
				Set<EPR> producers = getProducers(topic.getTopicQName());
				Iterator<EPR> it = producers.iterator();
				while (it.hasNext()) {
					EndpointReferenceType epr2 = it.next();
					ReNotifier renotifier = new ReNotifier(topic, epr2, scope, this);
					new Thread(renotifier).start();
				}
			}
				
				
			receiver.notifications.add(topic.getTopicQName());
			producer.subscribe((GCUBEConsumer)receiver, NotificationTopic.NOTIFICATIONRECEIVED);
			log.trace("the subscription is done");
			
		} catch (Exception e) {
			log.warn("error in subscribe for any topic",e);
		}
	}
	
	
	
	
	/**
	 * Unsubscribes a subscriber from a previously subscribed topic.
	 * @param subscriber the subscriber no more interested in a topic
	 * @param topic the topic to un-subscribe from
	 * @param manager GCUBESecurityManager
	 */
	@SuppressWarnings("unchecked")
	public void unsubscribeFromAnyTopic(GCUBENotificationTopic topic, GCUBESecurityManager manager, GCUBEScope scope) {
		BaseNotificationConsumer topicSubscriber = getSubscribers(topic.getTopicQName());
		if (topicSubscriber == null) return;
		log.trace("Unsubscribing client from topic "+topic);
			
		log.trace("unregistering topic is successfull? "+this.relatedTopic.remove(topic));
					
		if (topicSubscriber.notifications.contains(topic.getTopicQName())){
			log.trace("the consumer contains the topic, has been removed ?"+topicSubscriber.notifications.remove(topic.getTopicQName()));
			if (topicSubscriber.notifications.size()==0){
				producer.unsubscribe((GCUBEConsumer)topicSubscriber, NotificationTopic.NOTIFICATIONRECEIVED);
				log.trace("the notification size of the consumer is 0 so we can unsubscrive it");}
		}
		subscriber.remove(topic);
		try {
			ISNotificationManager managerIS = new ISNotificationManager(scope);
			managerIS.unsubscribeEPRFromTopic(myEpr, topic,manager);
		} catch (Exception e) {
			log.warn("some error occurrs on topic unregistration"+e.getMessage());
		}
	}

	
	
	
	
	/**
	 * Convenience method which always returns a valid set, without having to worry about null values
	 * @param topic the topic
	 * @return a set  :-) 
	 */
	private Set<EPR> getProducers(QName topic) {

		Set<EPR> producers = knownProducers.get(topic);
		if (producers != null) return producers;

		return Collections.synchronizedSet(new HashSet<EPR>());
	}
	
	
	
	
	
	/**
	 * Convenience method which always returns a valid set, without having to worry about null values
	 * @param topic the topic
	 * @return a set  :-) 
	 */
	private BaseNotificationConsumer getSubscribers(QName topic) {

		BaseNotificationConsumer topicSubscriber = subscriber.get(topic);
		if (topicSubscriber != null) return topicSubscriber;

		return topicSubscriber;
	}
	
	
	
	
	
	
	/**
	 * Inserts a new known producer into the known producers set for a specific topic.
	 * @param topic the topic this producer sent a message about
	 * @param producer the producer EPR
	 */
	private void insertKnownProducer(QName topic, EndpointReferenceType producer) {
		synchronized (knownProducers) {
			Set<EPR> topicProducers = knownProducers.get(topic);
			if (topicProducers == null) {
				topicProducers = Collections.synchronizedSet(new HashSet<EPR>());
				knownProducers.put(topic, topicProducers);
			}
			EPR wrapped = new EPR(producer);
			if (topicProducers.contains(wrapped)) return;
			topicProducers.add(wrapped);
		}
		log.trace("Inserted a new known producer for topic "+topic+" at "+producer.getAddress());
	}
	
	
	
	
	/**
	 * Removes a known producer for a topic. This can happen when we try to
	 * call getCUrrentMessage() on an EPR, and it fails.
	 * @param topic the topic
	 * @param producer the producer's EPR.
	 */
	void removeKnownProducer(QName topic, EndpointReferenceType producer) {
		synchronized (knownProducers) {
			Set<EPR> topicProducers = knownProducers.get(topic);
			if (topicProducers == null) {
				// this should not happen.
				log.warn("Unexpected condition while removing producer for "+topic+" - producer list for topic was null!");
				return;
			}
			topicProducers.remove(new EPR(producer));
			log.trace("Removed producer at "+producer.getAddress()+" for topic "+topic+" from known producers list.");
			if (topicProducers.isEmpty()) {
				log.trace("No more known producers for topic "+topic+", removing the list");
				knownProducers.remove(topicProducers);
				
			}
		}
	}
	
	

	
	/**
	 * Method which receives the notifications.
	 * @param topics a list of topics
	 * @param producer the EPR producing the notification
	 * @param content the actual notification
	 * @see org.globus.wsrf.NotifyCallback#deliver(java.util.List, org.apache.axis.message.addressing.EndpointReferenceType, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void deliver(List topics, EndpointReferenceType producer, Object content) {
		Iterator topicsIterator = topics.iterator();
		while (topicsIterator.hasNext()) {
			try {
				// we know that we're expecting a list of QNames.
				// Well, actually, we're expecting a list with exactly one QName. But hey, who knows? 
				QName topic = (QName) topicsIterator.next();
				log.debug("Received a new notification from "+producer.getAddress()+" about topic "+topic);
				try{
					ResourcePropertyValueChangeNotificationElementType notificationElement;
					notificationElement = (ResourcePropertyValueChangeNotificationElementType) content;
					ResourcePropertyValueChangeNotificationType notification = notificationElement.getResourcePropertyValueChangeNotification();
					/*
					MessageElement msg= new MessageElement();
					Object deserializedObject = ObjectDeserializer.toObject((MessageElement) msg,content.getClass() );
					deserializedObject.getClass().cast(content.getClass());
					*/
					if (notification != null) {
						MessageElement[] message = notification.getNewValue().get_any();
						// Handle the notification
						NotificationEvent event = new NotificationEvent(new NotificationMessage(topic, message, producer));
						this.producer.notify(NotificationTopic.NOTIFICATIONRECEIVED, event );
					}
				}catch(ClassCastException e){
					log.info("Generic Notfication received");
					NotificationEvent event = new NotificationEvent(new NotificationMessage(topic, content, producer));
					this.producer.notify(NotificationTopic.NOTIFICATIONRECEIVED, event );
				}
			} catch (Exception t) { // you never know...
				log.error("Exception while receiving notification message!",t);
			}
		}
		
	}
	
	
	
	/*
	 * ============ UTILITY FUNCTIONS ================
	 */
	
	
	/**
	 * Returns a specific String from a MessageElement array.
	 * @param index the index of the string
	 * @param message the message elements array
	 * @return the String at the given index in the array
	 */
	public static String getStringAtIndex(int index, MessageElement[] message) {
		return message[index].getValue();
	}
	
	/**
	 * Returns the last message element as a String.
	 * @param message the MessageElement[] array, as received by the receiveNotification() method
	 * @return The last element in the message as a String; null when the message is empty
	 */
	public static String getLastString(MessageElement[] message) {
		if (message == null || message.length < 1) return null;
		return getStringAtIndex(message.length-1, message);
	}
	
	/**
	 * Returns the first message element as a String.
	 * @param message the MessageElement[] array, as received by the receiveNotification() method
	 * @return The first element in the message as a String; null when the message is empty
	 */
	public static String getFirstString(MessageElement[] message) {
		if (message == null || message.length < 1) return null;
		return getStringAtIndex(0, message);
	}
	
	/**
	 * Returns all message elements, as a String array.
	 * @param message the MessageElement[] array, as received by the receiveNotification() method
	 * @return The elements in the message as a String array; a String[0] object when the message is empty
	 */
	public static String[] getStringArray(MessageElement[] message) {
		if (message == null || message.length < 1) return new String[0];
		
		String[] ret = new String[message.length];
		for (int i = 0; i < message.length; i++) {
			ret[i] = getStringAtIndex(i, message);
		}
		return ret;
	}

	
	protected boolean containsTopic(QName topic){
		for (GCUBENotificationTopic t:this.relatedTopic)
			if (t.getTopicQName().toString().compareTo(topic.toString())==0) return true;
		return false;
	}
	
	protected boolean containsTopic(GCUBENotificationTopic topic){
		return this.relatedTopic.contains(topic);
	}
	
	protected void stopListening(){
		try {
			this.consumerManager.stopListening();
		} catch (ContainerException e) {
			log.warn("impossible to stop receiver",e);
		}
	}
}


