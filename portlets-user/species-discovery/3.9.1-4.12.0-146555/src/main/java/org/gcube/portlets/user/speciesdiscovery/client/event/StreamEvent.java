/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class StreamEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2017
 */
public class StreamEvent extends GwtEvent<StreamCompletedEventEventHandler> {

	public static final GwtEvent.Type<StreamCompletedEventEventHandler> TYPE = new Type<StreamCompletedEventEventHandler>();
	private boolean activeFilter;
	private Event event = null;

	public enum Event {STRARTED, COMPLETED}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<StreamCompletedEventEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(StreamCompletedEventEventHandler handler) {
		handler.onStreamCompleteEvent(this);
	}

	/**
	 * Instantiates a new stream event.
	 *
	 * @param activeFilter the active filter
	 * @param isCompleted the is completed
	 */
	public StreamEvent(boolean activeFilter, Event event) {
		this.activeFilter = activeFilter;
		this.event = event;
	}

	/**
	 * Checks if is active filter.
	 *
	 * @return true, if is active filter
	 */
	public boolean isActiveFilter() {
		return activeFilter;
	}


	/**
	 * @return the event
	 */
	public Event getEvent() {

		return event;
	}

}
