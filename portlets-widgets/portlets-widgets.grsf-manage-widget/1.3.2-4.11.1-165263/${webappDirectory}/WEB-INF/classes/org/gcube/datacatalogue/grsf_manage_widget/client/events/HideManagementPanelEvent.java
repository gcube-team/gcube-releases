package org.gcube.datacatalogue.grsf_manage_widget.client.events;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Hide management panel event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class HideManagementPanelEvent extends GwtEvent<HideManagementPanelEventHandler> {
	public static Type<HideManagementPanelEventHandler> TYPE = new Type<HideManagementPanelEventHandler>();
	
	public HideManagementPanelEvent() {
	}

	@Override
	public Type<HideManagementPanelEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideManagementPanelEventHandler handler) {
		handler.onEvent(this);
	}

}
