package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class StartNodeEvent extends GwtEvent<StartNodeEventHandler> implements CascadedEvent{

	public static final Type<StartNodeEventHandler> TYPE= new Type<StartNodeEventHandler>();

	private String nodeId;

	private FutureEvent cascadeEvent=null;
	
	public StartNodeEvent(String nodeId) {
		super();
		this.nodeId = nodeId;
	}

	@Override
	public Type<StartNodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(StartNodeEventHandler handler) {
		handler.onStartNode(this);
	}

	public String getNodeId() {
		return nodeId;
	}
	
	@Override
	public FutureEvent getCascade() {
		return cascadeEvent;
	}
	
	@Override
	public void setCascade(FutureEvent theEvent) {
		this.cascadeEvent = theEvent;
	}
}
