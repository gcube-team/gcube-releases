package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationPanelStatusChangeEvent extends GwtEvent<NavigationPanelStatusChangeEventHandler>{

	public static Type<NavigationPanelStatusChangeEventHandler> TYPE= new Type<NavigationPanelStatusChangeEventHandler>();
	
	private ObjectType type;
	public NavigationPanelStatusChangeEvent(ObjectType type) {
		this.type=type;
	}
	
	@Override
	public Type<NavigationPanelStatusChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NavigationPanelStatusChangeEventHandler handler) {
		handler.onSelectedResourceType(this);
	}

	public ObjectType getType() {
		return type;
	}
	
	
}
