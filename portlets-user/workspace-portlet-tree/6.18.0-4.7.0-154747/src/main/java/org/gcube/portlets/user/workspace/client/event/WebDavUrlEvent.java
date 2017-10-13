package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class WebDavUrlEvent extends GwtEvent<WebDavUrlEventHandler> {
	public static Type<WebDavUrlEventHandler> TYPE = new Type<WebDavUrlEventHandler>();
	
	private String itemIdentifier;

	public WebDavUrlEvent(String itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Type<WebDavUrlEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(WebDavUrlEventHandler handler) {
		handler.onClickWebDavUrl(this);
		
	}

	public String getItemIdentifier() {
		return itemIdentifier;
	}

}
