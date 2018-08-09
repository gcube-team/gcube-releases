package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class RemovePinnedEvent extends GwtEvent<RemovePinnedEventHandler> {
	public static final Type<RemovePinnedEventHandler> TYPE=new Type<RemovePinnedEventHandler>();
	
	
	private Widget toRemove;
	private Storable theResource;
	
	public RemovePinnedEvent(Widget toRemove,Storable theResource) {
		super();
		this.toRemove = toRemove;
		this.theResource=theResource;
	}

	@Override
	public Type<RemovePinnedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemovePinnedEventHandler handler) {
		handler.onRemovePinnedResource(toRemove,theResource);
	}

}
