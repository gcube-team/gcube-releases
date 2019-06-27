package org.gcube.data.tmf.api;

/**
 * A consumer of {@link SourceEvent}s.
 * <p>
 * The service acts as the primary consumer of source events, though plugin components may wish to act
 * as event consumers for increased decoupling from event producers.
 * 
 * @author Fabio Simeoni
 *
 * @see SourceNotifier
 * @see SourceEvent
 * @see Source#notifier()
 */
public interface SourceConsumer {

	/**
	 * Callback on notification of {@link SourceEvent}s.
	 * @param events the events.
	 */
	public void onEvent(SourceEvent ...events);
	
}
