package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class ShowCreationFormEvent extends GwtEvent<ShowCreationFormEventHandler> {
	public static final Type<ShowCreationFormEventHandler> TYPE=new Type<ShowCreationFormEventHandler>();
	
	private ObjectType type;
	
	
	
	public ShowCreationFormEvent(ObjectType type) {
		super();
		this.type = type;
	}

	@Override
	public Type<ShowCreationFormEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowCreationFormEventHandler handler) {
		handler.onShowCreationForm(this);
	}


	public ObjectType getType() {
		return type;
	}
}
