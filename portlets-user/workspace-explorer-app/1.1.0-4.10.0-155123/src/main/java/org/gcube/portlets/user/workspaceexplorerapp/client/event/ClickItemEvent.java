package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ClickItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class ClickItemEvent extends GwtEvent<ClickItemEventHandler> {
	public static Type<ClickItemEventHandler> TYPE = new Type<ClickItemEventHandler>();
	private Set<Item> items;


	/**
	 * Instantiates a new click item event.
	 *
	 * @param selectedObject the item
	 */
	public ClickItemEvent(Set<Item> selectedObject) {
		this.items = selectedObject;
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
	 * Gets the items.
	 *
	 * @return the items
	 */
	public  Set<Item> getItems() {
		return items;
	}
}
