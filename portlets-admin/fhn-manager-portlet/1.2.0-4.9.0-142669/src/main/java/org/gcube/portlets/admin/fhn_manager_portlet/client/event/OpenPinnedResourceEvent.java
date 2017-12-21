package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.event.shared.GwtEvent;

public class OpenPinnedResourceEvent extends GwtEvent<OpenPinnedResourceEventHandler> {
	public static Type<OpenPinnedResourceEventHandler> TYPE=new Type<OpenPinnedResourceEventHandler>();
	
	private Storable toOpen;
	
	
	
	
	public OpenPinnedResourceEvent(Storable toOpen) {
		super();
		this.toOpen = toOpen;
	}

	@Override
	public Type<OpenPinnedResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenPinnedResourceEventHandler handler) {
		handler.onOpenPinnedResource(this);
	}

	
	public Storable getToOpen() {
		return toOpen;
	}
}
