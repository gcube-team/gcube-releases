package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;



/**
 * The Class PerformFishFieldFormChangedEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 27, 2019
 */
public class PerformFishFieldFormChangedEvent extends GwtEvent<PerformFishFieldFormChangedEventHandler> {
	public static Type<PerformFishFieldFormChangedEventHandler> TYPE = new Type<PerformFishFieldFormChangedEventHandler>();

	private Widget sourceWidget;


	/**
	 * Instantiates a new perform fish field form changed event.
	 *
	 * @param sourceWidget the source widget
	 */
	public PerformFishFieldFormChangedEvent(Widget sourceWidget) {

		this.sourceWidget = sourceWidget;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PerformFishFieldFormChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	protected void dispatch(PerformFishFieldFormChangedEventHandler handler) {
		handler.onFieldFormChanged(this);
	}


	/**
	 * Gets the source widget.
	 *
	 * @return the sourceWidget
	 */
	public Widget getSourceWidget() {

		return sourceWidget;
	}

}
