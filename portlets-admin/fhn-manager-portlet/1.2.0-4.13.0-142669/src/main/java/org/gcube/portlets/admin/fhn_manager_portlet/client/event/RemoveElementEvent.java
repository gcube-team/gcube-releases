package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class RemoveElementEvent extends GwtEvent<RemoveElementEventHandler> implements CascadedEvent{
	public static Type<RemoveElementEventHandler> TYPE=new Type<RemoveElementEventHandler>();

	private ObjectType type;
	private String toRemoveId;
	private Map<String,Boolean> flags=null; 
	
	private FutureEvent cascadeEvent=null;
	
	@Override
	public FutureEvent getCascade() {
		return cascadeEvent;
	}
	
	@Override
	public void setCascade(FutureEvent theEvent) {
		this.cascadeEvent = theEvent;
	}
	
	public RemoveElementEvent(ObjectType type, String toRemoveId) {
		super();
		this.type = type;
		this.toRemoveId = toRemoveId;
	}
	
	@Override
	public Type<RemoveElementEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(RemoveElementEventHandler handler) {
		handler.onRemoveElement(this);
	}
	
	public ObjectType getType() {
		return type;
	}
	
	public String getToRemoveId() {
		return toRemoveId;
	}
	
	public void setFlags(Map<String, Boolean> flags) {
		this.flags = flags;
	}
	
	public Map<String, Boolean> getFlags() {
		return flags;
	}
}
