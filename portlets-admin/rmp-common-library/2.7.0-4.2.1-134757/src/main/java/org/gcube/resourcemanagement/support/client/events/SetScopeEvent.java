package org.gcube.resourcemanagement.support.client.events;

import com.google.gwt.event.shared.GwtEvent;



public class SetScopeEvent  extends GwtEvent<SetScopeEventHandler> {
	public static Type<SetScopeEventHandler> TYPE = new Type<SetScopeEventHandler>();
	
	private String scope2Set;

	public String getScope() {
		return scope2Set;
	}
	public SetScopeEvent(String scope2Set) {
		this.scope2Set = scope2Set;
	}

	@Override
	public Type<SetScopeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetScopeEventHandler handler) {
		handler.onSetScope(this);
	}
}
