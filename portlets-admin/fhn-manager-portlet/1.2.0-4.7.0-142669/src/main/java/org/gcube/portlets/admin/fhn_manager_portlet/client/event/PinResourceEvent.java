package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.event.shared.GwtEvent;

public class PinResourceEvent extends GwtEvent<PinResourceEventHandler> {
	public static Type<PinResourceEventHandler> TYPE=new Type<PinResourceEventHandler>();
	
	private Storable selectedResource;
	
	public PinResourceEvent(Storable selectedResource) {
		super();
		this.selectedResource = selectedResource;
	}

	@Override
	public Type<PinResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PinResourceEventHandler handler) {
		handler.onPinResource(this);
	}

	public Storable getSelectedResource() {
		return selectedResource;
	}
	
}
