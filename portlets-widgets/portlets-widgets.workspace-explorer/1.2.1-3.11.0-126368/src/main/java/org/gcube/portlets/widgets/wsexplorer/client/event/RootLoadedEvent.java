package org.gcube.portlets.widgets.wsexplorer.client.event;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class LoadRootEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class RootLoadedEvent extends GwtEvent<RootLoadedEventHandler> {
	
	public static Type<RootLoadedEventHandler> TYPE = new Type<RootLoadedEventHandler>();
	private Item root;

	public RootLoadedEvent(Item root) {
		this.root  = root;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<RootLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(RootLoadedEventHandler handler) {
		handler.onRootLoaded(this);
	}

	/**
	 * @return the root
	 */
	public Item getRoot() {
		return root;
	}
	
}
