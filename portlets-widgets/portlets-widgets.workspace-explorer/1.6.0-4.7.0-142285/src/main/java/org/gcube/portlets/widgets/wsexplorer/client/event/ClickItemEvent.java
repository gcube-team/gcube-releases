package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ClickItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class ClickItemEvent<T> extends GwtEvent<ClickItemEventHandler> {
	public static Type<ClickItemEventHandler> TYPE = new Type<ClickItemEventHandler>();
	private T item;


	/**
	 * Instantiates a new click item event.
	 *
	 * @param item the item
	 */
	public ClickItemEvent(T item) {
		this.item = item;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ClickItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ClickItemEventHandler handler) {
		handler.onClick(this);
	}

	/**
	 * Gets the item.
	 *
	 * @return the item
	 */
	public T getItem() {
		return item;
	}
}
