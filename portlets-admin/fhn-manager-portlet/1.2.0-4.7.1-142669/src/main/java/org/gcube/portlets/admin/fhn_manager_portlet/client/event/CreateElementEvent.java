package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class CreateElementEvent extends GwtEvent<CreateElementEventHandler> implements GenericParameterEvent,CascadedEvent{
	public static final Type<CreateElementEventHandler> TYPE=new Type<CreateElementEventHandler>();
	
	private ObjectType  type;
	private Map<String,String> fields;
	private FutureEvent cascadeEvent=null;
	
	
	@Override
	public FutureEvent getCascade() {
		return cascadeEvent;
	}
	@Override
	public void setCascade(FutureEvent theEvent) {
		this.cascadeEvent=theEvent;
		
	}
	
	public CreateElementEvent(ObjectType type, Map<String, String> fields) {
		super();
		this.type = type;
		this.fields = fields;
	}
	@Override
	public Type<CreateElementEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateElementEventHandler handler) {
		handler.onCreateElement(this);
	}

	public ObjectType getType() {
		return type;
	}
	
	public Map<String, String> getFields() {
		return fields;
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		fields=parameters;
	}
}
