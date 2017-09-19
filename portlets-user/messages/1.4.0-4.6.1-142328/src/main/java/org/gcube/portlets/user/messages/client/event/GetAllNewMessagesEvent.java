package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetAllNewMessagesEvent extends GwtEvent<GetAllNewMessagesEventHandler> {
	public static Type<GetAllNewMessagesEventHandler> TYPE = new Type<GetAllNewMessagesEventHandler>();
	
	private String messageType;
	private boolean isPolling;
	
	public GetAllNewMessagesEvent(String messageType, boolean isPolling) {
		this.messageType = messageType;
		this.isPolling = isPolling;
	}

	public GetAllNewMessagesEvent() {
	}

	@Override
	public Type<GetAllNewMessagesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GetAllNewMessagesEventHandler handler) {
		handler.onNewMessagesEvent(this);
		
	}

	public String getMessageType() {
		return messageType;
	}

	public boolean isPolling() {
		return isPolling;
	}

}
