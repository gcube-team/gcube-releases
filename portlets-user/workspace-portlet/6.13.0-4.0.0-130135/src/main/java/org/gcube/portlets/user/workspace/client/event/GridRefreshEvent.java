package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GridRefreshEvent extends GwtEvent<GridRefreshEventHandler> {
  public static Type<GridRefreshEventHandler> TYPE = new Type<GridRefreshEventHandler>();

	public GridRefreshEvent() {

	}

	@Override
	public Type<GridRefreshEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GridRefreshEventHandler handler) {
		handler.onGridRefresh(this);
		
	}
}