package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class GridRefreshEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 5, 2018
 */
public class GridRefreshEvent extends GwtEvent<GridRefreshEventHandler> {
  public static Type<GridRefreshEventHandler> TYPE = new Type<GridRefreshEventHandler>();

	/**
	 * Instantiates a new grid refresh event.
	 */
	public GridRefreshEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<GridRefreshEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(GridRefreshEventHandler handler) {
		handler.onGridRefresh(this);

	}
}