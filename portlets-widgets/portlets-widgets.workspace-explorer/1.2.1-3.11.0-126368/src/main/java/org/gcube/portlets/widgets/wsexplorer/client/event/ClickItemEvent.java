package org.gcube.portlets.widgets.wsexplorer.client.event;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ClickItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class ClickItemEvent extends GwtEvent<ClickItemEventHandler> {
	public static Type<ClickItemEventHandler> TYPE = new Type<ClickItemEventHandler>();
	private Item item;


	/**
	 * Instantiates a new click item event.
	 *
	 * @param item the item
	 */
	public ClickItemEvent(Item item) {
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
	public Item getItem() {
		return item;
	}
}
