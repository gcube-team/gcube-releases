package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FilterScopeEvent extends GwtEvent<FilterScopeEventHandler> {
	public static Type<FilterScopeEventHandler> TYPE = new Type<FilterScopeEventHandler>();
	private String scopeId;


	public FilterScopeEvent(String scopeId) {
		this.scopeId = scopeId;
	}

	@Override
	public Type<FilterScopeEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(FilterScopeEventHandler handler) {
		handler.onClickScopeFilter(this);

	}

	public String getScopeId() {
		return scopeId;
	}
}
