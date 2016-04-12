package org.gcube.common.informationsystem.notification.impl.client;

import javax.xml.namespace.QName;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * This class is used to notify subscribers about the arrival of a notification.
 * Since we cannot predict how clients behave (especially how long they take to
 * handle message arrival), a new thread is spawned for each subscriber.
 * @author Christoph Langguth
 */
class Notifier implements Runnable {
	/**
	 * Object logger.
	 */
    protected static  final GCUBELog log=new GCUBELog(Notifier.class);
	
	/** Topic of the notification */
	private QName topic;
	/** producer of the notification */
	private EndpointReferenceType epr;
	/** receiver of the notification */
	private BaseNotificationConsumer receiver;
	/** The notification itself */
	private MessageElement[] message;

	/**
	 * Constructor to initialize all required private fields.
	 * @param topic the topic
	 * @param epr the producer EPR
	 * @param receiver the target receiver
	 * @param message the notification message
	 */
	public Notifier(QName topic, EndpointReferenceType epr, BaseNotificationConsumer receiver, MessageElement[] message) {
		this.topic = topic;
		this.epr = epr;
		this.receiver = receiver;
		this.message = message;
	}

	/**
	 * Method that performs the actual dispatching of the notification to
	 * the subscriber.
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		log.info("Notifying subscriber about "+topic+" from "+epr.getAddress());
		try {
			GCUBEEvent event= new GCUBEEvent ();
			event.setPayload(new ISNotifier.NotificationMessage(topic, message, epr));
			receiver.onEvent(event);
		} catch (Throwable t) {
			log.error("Error while notifying subscriber",t);
		}

	}

}
