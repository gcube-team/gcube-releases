package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;



public class RenderForm  extends GwtEvent<RenderFormEventHandler> {
	public static Type<RenderFormEventHandler> TYPE = new Type<RenderFormEventHandler>();
	
	private boolean result;
	

	public boolean isSuccess() {
		return result;
	}
	public RenderForm(boolean result) {
		this.result = result;
	}

	@Override
	public Type<RenderFormEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenderFormEventHandler handler) {
		handler.onRenderForm(this);
	}
}
