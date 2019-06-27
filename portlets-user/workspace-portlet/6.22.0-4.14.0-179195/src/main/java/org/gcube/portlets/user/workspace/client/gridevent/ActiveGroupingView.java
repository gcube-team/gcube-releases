package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class ActiveGroupingView extends GwtEvent<ActiveGroupingViewHandler> {
	public static Type<ActiveGroupingViewHandler> TYPE = new Type<ActiveGroupingViewHandler>();
	private boolean activeGrouping;

	public boolean isActiveGrouping() {
		return activeGrouping;
	}

	public ActiveGroupingView(boolean active) {
		this.activeGrouping = active;
	}

	@Override
	public Type<ActiveGroupingViewHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ActiveGroupingViewHandler handler) {
		handler.onActiveGroupingView(this);
	}
	

}
