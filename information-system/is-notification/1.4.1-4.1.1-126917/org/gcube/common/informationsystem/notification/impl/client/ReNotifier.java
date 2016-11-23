package org.gcube.common.informationsystem.notification.impl.client;

import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.wsrf.WSNConstants;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.oasis.wsn.GetCurrentMessage;
import org.oasis.wsn.GetCurrentMessageResponse;
import org.oasis.wsn.NotificationProducer;
import org.oasis.wsn.TopicExpressionType;
import org.oasis.wsn.WSBaseNotificationServiceAddressingLocator;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;
import org.xml.sax.InputSource;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationMessage;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;



/**
 * This class is used to fetch past messages about a topic and to notify
 * a specific subscriber (in case there are messages)
 * @author Christoph Langguth
 *
 */
class ReNotifier implements Runnable {
	
	/**
	 * Object logger.
	 */
    protected static  final GCUBELog log=new GCUBELog(ReNotifier.class);
	/** notification topic */
	private GCUBENotificationTopic topic;
	
	private NotificationBroker broker;
	
	private GCUBEScope scope;

	/** notification producer EPR */
	private EndpointReferenceType epr;

	/** receiver for the notification */
	//private  BaseNotificationConsumer receiver;

	/**
	 * Creates a new ReNotifier object, specifying topic, producer and consumer.
	 * @param topic Topic to retrieve messages about
	 * @param epr EPR where to ask for last message
	 * @param receiver receiver of notifications
	 */
	public ReNotifier(GCUBENotificationTopic topic, EndpointReferenceType epr, GCUBEScope scope, NotificationBroker broker) {
		this.topic = topic;
		this.epr = epr;
		this.scope = scope;
		this.broker= broker;
	}

	/**
	 * Method performing the actual notification, in a new thread.
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		log.trace("About to retrieve past messages about " + topic.getTopicQName() + " from "
				+ epr.getAddress());
		try {
			TopicExpressionType topicExpression = new TopicExpressionType();
			topicExpression.setDialect(WSNConstants.SIMPLE_TOPIC_DIALECT);
			topicExpression.setValue(topic.getTopicQName());
			
			
			GetCurrentMessage request = new GetCurrentMessage(topicExpression);
			
			
			
			GetCurrentMessageResponse response = null;

			// this inside try block should hopefully only throw something when
			// there is a network error
			// or something like that.
			
			/*FIXME: this should handle the exception that is thrown when no current message is
			* present yet. It might also be helpful to either keep a "failed count" for a producer
			* instead of immediately removing it from the producers list, to cater for temporary
			* failures. Or (probably better) one could leave this thread running, retrying 
			* every now and then, before finally giving up.
			* 
			* 
			* A possible implementation could be something like
			* //if error:
			* for (int i=0; i <= 10; i++) {
			* 	// you'd also need some kind of break condition if a "real" notification is
			*	// received, because then there's no more need for the getLastMessage.
			* 	Thread.sleep(60000 * 2**i);
			* 	retry; if (success) break;
			* }
			* if (!success) removeProducer // this producer has failed for more than 24 hours
			* 
			* 
			* Anyway, the producer list is just a
			* list of producers which are contacted when a new local client subscribes with
			* getPast == true. A producer is automatically re-added to the known producers list
			* when a new notification from it is received.
			*/
			
			try {
				
				WSBaseNotificationServiceAddressingLocator notificationLocator = new WSBaseNotificationServiceAddressingLocator();
				NotificationProducer producer = notificationLocator.getNotificationProducerPort(epr);
				response = producer.getCurrentMessage(request);
			} catch (Exception e) {
				log.error("Error while invoking getLastMessage() on "
						+ epr.getAddress() + "; removing producer from list. ",
						e);
				/*try {
					NotificationBroker.getInstance(scope)
							.removeKnownProducer(topic.getTopicQName(), epr);
				} catch (Exception e1) {
					log.warn("Unexpected expection;",e1);
				}*/
				return;
			}
			
			if (response==null)
				throw new NoNotificationMessageException("no past notification for the topic: "+this.topic);
			
			MessageElement[] tmpElement=response.get_any();
			if (tmpElement==null)
				throw new NoNotificationMessageException("no past notfication for the topic: "+this.topic.getTopicQName());
			
			log.trace("the mesage Element is retrieved");
			String xml = tmpElement[0].getAsString();
			
			try {
				log.trace("starting renotification"); 
				ResourcePropertyValueChangeNotificationType notification;
				//log.trace("the message is delivered" +xml);
				InputSource stringSource = new InputSource(
						new StringReader(xml));
				notification = (ResourcePropertyValueChangeNotificationType) ObjectDeserializer.deserialize(
								stringSource,
								ResourcePropertyValueChangeNotificationType.class);
				log.trace("message deserialized");
				MessageElement[] message = notification.getNewValue().get_any();

				//evaluating the precondition if exists
				if (topic.getPrecondition()!=null){
					XPath xpath = XPathFactory.newInstance().newXPath();
					//log.trace("message to evaluate is "+message[0].getAsString());
					if (!(Boolean) xpath.evaluate(topic.getPrecondition(), new InputSource(new StringReader(message[0].getAsString())),XPathConstants.BOOLEAN))
						throw new NoNotificationMessageException("no past notfication for the topic: "+this.topic.getTopicQName()+" with precondition "+topic.getPrecondition());
				}
				// notify the client
				log.trace("preparing the event");
				NotificationEvent event = new NotificationEvent(new NotificationMessage(topic.getTopicQName(), message, epr));
				broker.producer.notify(NotificationTopic.NOTIFICATIONRECEIVED, event );
			}catch(DeserializationException e){
				//handles re-notification message different by ResourcePropertyValueChangeNotificationType (customized message)  
				log.info("Re-notifying Generic Notfication");
				
				//evaluating the precondition if exists
				if (topic.getPrecondition()!=null){
					XPath xpath = XPathFactory.newInstance().newXPath();
					if (!(Boolean) xpath.evaluate(topic.getPrecondition(), new InputSource(new StringReader(tmpElement[0].getAsString())),XPathConstants.BOOLEAN))
						throw new NoNotificationMessageException("no past notfication for the topic: "+this.topic.getTopicQName()+" with precondition "+topic.getPrecondition());
				}
				
				NotificationEvent event = new NotificationEvent(new NotificationMessage(topic.getTopicQName(), tmpElement[0], epr));
				broker.producer.notify(NotificationTopic.NOTIFICATIONRECEIVED, event );
			} 
		}catch(NoNotificationMessageException e){
			log.warn("no message found ",e);
		}catch (Exception e) {
			log.error("Error while trying to retrieve last message from "
					+ epr.getAddress() + " about " + topic, e);
		}
	}

	
}


