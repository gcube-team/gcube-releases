/**
 * 
 */
package org.gcube.data.tmf.api;


/**
 * 
 * An event in the lifetime of a {@link Source}.
 * <p>
 * There are two pre-defined lifetime events for which the service subscribes (
 * {@link SourceEvent#CHANGE},{@link SourceEvent#REMOVE}). Plugins notify these
 * events to {@link SourceNotifier}s so as to inform the service of changes in the
 * lifetime of data sources. In turn, the service broadcasts them to clients
 * that have expressed an interest in them.
 * <p>
 * Plugins that desire increased decoupling between some of their components may
 * define additional source events, subscribe some components as consumers of these
 * events, and notify these events from yet other components.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @see SourceNotifier
 * @see Source#notifier()
 * @see SourceConsumer
 */
public interface SourceEvent {

	/**
	 * Signals a change in the data source.
	 * <p>
	 * A notification of this event tells the service to broadcast the change to
	 * its clients.
	 */
	public static final SourceEvent CHANGE = new SourceEvent() {
	};

	/**
	 * Signals that the data source is no longer accessible to the plugin.
	 * <p>
	 * A notification of this event tells the service to stop exposing the data
	 * source to its client and to reclaim resources allocated to its
	 * management.
	 */
	public static final SourceEvent REMOVE = new SourceEvent() {
	};
}
