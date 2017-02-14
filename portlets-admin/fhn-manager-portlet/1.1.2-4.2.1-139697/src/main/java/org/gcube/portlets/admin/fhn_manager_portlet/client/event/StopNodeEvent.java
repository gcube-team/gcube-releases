package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class StopNodeEvent extends GwtEvent<StopNodeEventHandler> implements CascadedEvent{
	public static final Type<StopNodeEventHandler> TYPE=new Type<StopNodeEventHandler>();
	
	private String toStopNodeId;
	
	private GwtEvent cascadeEvent=null;
	
	
	public StopNodeEvent(String toStopNodeId) {
		super();
		this.toStopNodeId = toStopNodeId;
	}

	@Override
	public Type<StopNodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(StopNodeEventHandler handler) {
		handler.onStopNode(this);
	}

	
	@Override
	public GwtEvent getCascade() {
		return cascadeEvent;
	}
	
	@Override
	public void setCascade(GwtEvent theEvent) {
		this.cascadeEvent = theEvent;
	}
	
	public String getToStopNodeId() {
		return toStopNodeId;
	}
}
