package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowMessageEvent extends GwtEvent<ShowMessageEventHandler> {
	public static final Type<ShowMessageEventHandler> TYPE=new Type<ShowMessageEventHandler>();

	
	private String title;
	private String Message;
	
	
	
	public ShowMessageEvent(String title, String message) {
		super();
		this.title = title;
		Message = message;
	}

	@Override
	public Type<ShowMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowMessageEventHandler handler) {
		handler.onShowMesasge(this);
	}
	
	
	public String getMessage() {
		return Message;
	}
	
	
	public String getTitle() {
		return title;
	}
}

