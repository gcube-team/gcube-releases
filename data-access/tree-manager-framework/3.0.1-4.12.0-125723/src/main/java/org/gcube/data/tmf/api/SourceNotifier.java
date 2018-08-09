package org.gcube.data.tmf.api;

import java.io.Serializable;

/**
 * Manages subscriptions for and notifications of {@link SourceEvent}s.
 * <p>
 * The service provides implementations of this interface and sets them on
 * {@link Source}s, and subscribes with them. Plugins may then use them to fire
 * events that the service should become aware of.
 * <p>
 * While the service is the primary subscriber to notifications of source events,
 * plugin components may also acts as subscribers for increased decoupling from event producers.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @see SourceEvent
 * @see Source#notifier()
 * 
 */
public interface SourceNotifier extends Serializable {

	/**
	 * Subscribes a {@link SourceConsumer} to one or more {@link SourceEvent}s.
	 * 
	 * @param consumer
	 *            the consumer
	 * @param events
	 *            the events
	 */
	void subscribe(SourceConsumer consumer, SourceEvent... events);

	/**
	 * Notifies subscribers of a {@link SourceEvent} event.
	 * 
	 * @param topic
	 *            the topic
	 */
	void notify(SourceEvent topic);
}
