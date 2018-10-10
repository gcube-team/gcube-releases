package org.gcube.common.core.informationsystem.notifier;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.globus.wsrf.Topic;

/**
 * 
 * Defines the local interface to the subscription/notification mechanism of an Information System in a gCube infrastructure.
 * 
 * @author Manuele Simi(ISTI-CNR)
 *
 */

public interface ISNotifier {

	/**
	 * Registers the consumer to the given list of topics
	 * 
	 * @param notifications the list of GCUBENotificationTopic to register to
	 * @param consumer the {@link BaseNotificationConsumer} to callback whenever a notification is received
	 * @param manager the {@link GCUBESecurityManager} of the consumer 
	 * @param scope the {@link GCUBEScope}s in which to register the consumer, it is used instead of the one included in the context
	 * 
	 * @throws ISNotifierException if the registration fails
	 */
	 public <T extends BaseNotificationConsumer> void registerToISNotification(T consumer,List<GCUBENotificationTopic> notifications, GCUBESecurityManager manager, GCUBEScope ... scope) throws ISNotifierException;
	
	
	 
	/**
	 * Unregisters the consumer from the given list of topics 
	 * 
	 * @param notifications the list of IS Notifications from which to unregister the {@link EndpointReferenceType}
	 * @param manager the {@link GCUBESecurityManager} of the consumer
	 * @param scope the {@link GCUBEScope}s in which to register the consumer, replacing the one included in the context
	 *  
	 * @throws ISNotifierException if the unregistration fails
	 */
	public void unregisterFromISNotification( GCUBESecurityManager manager, List<GCUBENotificationTopic> notifications, GCUBEScope ... scope) throws ISNotifierException ;

	
	
	/**
	 * Registers a list of topics as IS Notifications
	 * 
	 * @param producerEPR the {@link GCUBEServiceContext} of the producer 
	 * @param notifications the list of IS Notifications to register
	 * @param manager the {@link GCUBESecurityManager} of the producer
	 * @param scope the {@link GCUBEScope}s in which to register the IS notification, it is used instead of the one included in the context
	 * 
	 * @throws ISNotifierException if the registration fails
	 */
	public void registerISNotification (EndpointReferenceType producerEPR, List<? extends Topic> notifications, 
			GCUBESecurityManager manager, GCUBEScope ... scope) throws ISNotifierException ;

	/**
	 * Unregisters a list of new topics as source of IS Notifications
	 * 
	 * @param producerEPR the {@link EndpointReferenceType} of the producer
	 * @param notifications the list of IS Notification to unregister
	 * @param manager the {@link GCUBESecurityManager} of the producer
	 * @param scope the {@link GCUBEScope}s in which to unregister the IS notification, it is used instead of the one included in the context
	 *  
	 * @throws ISNotifierException if the unregistration fails
	 */
	public void unregisterISNotification (EndpointReferenceType producerEPR, List<? extends Topic> notifications,
			GCUBESecurityManager manager,  GCUBEScope ... scope) throws ISNotifierException;

	/**
	 * Checks if the producer registration is completed for a list of given topics
	 * 
	 * @param manager the {@link GCUBESecurityManager} of the producer
	 * @param scope the {@link GCUBEScope}s in which to register the IS notification
	 * @param topics list of topics to check
	 * @return for each input topic, a boolean value: true if the registration is completed, false otherwise
	 * 
	 * @throws ISNotifierException if it is impossible to perform the check for any topic
	 */
	public boolean[] isTopicRegistered(GCUBESecurityManager securityManager, GCUBEScope scope, List<TopicData> topics)	throws ISNotifierException;
	
	
	/** A {@link NotificationTopic} for lifetime events .*/
	enum NotificationTopic implements GCUBETopic {NOTIFICATIONRECEIVED};
		
	/** Generic {@link GCUBEEvent} for notification. */
	public class NotificationEvent extends GCUBEEvent<NotificationTopic,NotificationMessage> { 
		public NotificationEvent(NotificationMessage message) {this.payload = message;}
	}
	
	/** Base implementation of a {@link GCUBEConsumer}.*/
	public class BaseNotificationConsumer implements GCUBEConsumer<NotificationTopic,Object> {
		
		public BaseNotificationConsumer() {
			super();
			this.notifications = new ArrayList<QName>();
		}
		/** List of notification to filter for this consumer*/
		public List<QName> notifications = null; 
		
		/**Receives RI notification events and dispatches them to topic-specific callbacks.*/
		public <T1 extends NotificationTopic, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
			if (events==null) return;
			for (GCUBEEvent<T1,P1> event : events) {
				if ((this.notifications != null) && (this.notifications.contains(((NotificationMessage)event.getPayload()).getTopic()))){
					NotificationTopic topic = event.getTopic();
					switch (topic) {
						case NOTIFICATIONRECEIVED : this.onNotificationReceived((NotificationEvent)event);break;					
					}
				}
			}			
		}
		/**Notification event callback.
		 * @param event the event.*/
		protected void onNotificationReceived(NotificationEvent event) {}
	}
	
	/**
	 * Delivered Notification
	 * 
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	public class NotificationMessage {
		
		private QName topic;
		private Object message;
		private EndpointReferenceType producer;
		private boolean isRP;
		
		
		public NotificationMessage (QName topic, MessageElement[] message, EndpointReferenceType producer) {
			this.topic = topic;
			this.message = message;
			this.producer = producer;
			this.isRP= true;
		}
		
		public NotificationMessage (QName topic, Object message, EndpointReferenceType producer) {
			this.topic = topic;
			this.message = message;
			this.producer = producer;
			this.isRP= false;
		}
		
		/**
		 * @return the topic
		 */
		public QName getTopic() {
			return topic;
		}
		
		
		/**
		 * Used to return a RP Notfication Message
		 * 
		 * @return the message
		 */
		public MessageElement[] getMessage() {
			if (isRP) return (MessageElement[])message;
			else return null;
		}
		
		/**
		 * Used to return a not RP Notfication Message
		 * 
		 * @return the message
		 */
		public Object getMessageObject() {
			if (isRP) return null;
			else return message;
		}
		
		/**
		 * @return the producer
		 */
		public EndpointReferenceType getProducer() {
			return producer;
		}
	}
	
	/**
	 * Topic registration data
	 * 
	 * @author Lucio Lelii (ISTI-CNR)
	 *
	 */
	public class TopicData{
		
		private QName topic;
		private EndpointReferenceType epr;
		
		public TopicData(QName topic, EndpointReferenceType epr){
			this.topic= topic;
			this.epr=epr;
		}

		public QName getTopic() {
			return topic;
		}

		public void setTopic(QName topic) {
			this.topic = topic;
		}

		public EndpointReferenceType getEpr() {
			return epr;
		}

		public void setEpr(EndpointReferenceType epr) {
			this.epr = epr;
		}
		
	}
	
	public class GCUBENotificationTopic{
		
		private QName topicQName;
		private String precondition=null;
		private String selector=null;
		private boolean useRenotifier=false;
				
		public GCUBENotificationTopic(QName topicQName) {
			this.topicQName = topicQName;
		}

		public QName getTopicQName() {
			return topicQName;
		}

		public void setTopicQName(QName topicQName) {
			this.topicQName = topicQName;
		}

		public String getPrecondition() {
			return precondition;
		}

		public void setPrecondition(String precondition) {
			this.precondition = precondition;
		}

		public String getSelector() {
			return selector;
		}

		public void setSelector(String selector) {
			this.selector = selector;
		}

		public boolean isUseRenotifier() {
			return useRenotifier;
		}

		public void setUseRenotifier(boolean useRenotifier) {
			this.useRenotifier = useRenotifier;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((precondition == null) ? 0 : precondition.hashCode());
			result = prime * result
					+ ((selector == null) ? 0 : selector.hashCode());
			result = prime * result
					+ ((topicQName == null) ? 0 : topicQName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GCUBENotificationTopic other = (GCUBENotificationTopic) obj;
			if (precondition == null) {
				if (other.precondition != null)
					return false;
			} else if (!precondition.equals(other.precondition))
				return false;
			if (selector == null) {
				if (other.selector != null)
					return false;
			} else if (!selector.equals(other.selector))
				return false;
			if (topicQName == null) {
				if (other.topicQName != null)
					return false;
			} else if (!topicQName.equals(other.topicQName))
				return false;
			return true;
		}
	}

}
