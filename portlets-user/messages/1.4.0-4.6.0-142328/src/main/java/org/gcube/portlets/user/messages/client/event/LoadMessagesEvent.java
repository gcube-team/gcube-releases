package org.gcube.portlets.user.messages.client.event;

import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

public class LoadMessagesEvent extends GwtEvent<LoadMessagesEventHandler> implements GuiEventInterface{
	public static Type<LoadMessagesEventHandler> TYPE = new Type<LoadMessagesEventHandler>();
	
	private String typeMessages;

	private boolean isPolling;
	
	public LoadMessagesEvent(String typeMessages, boolean isPolling) {
		this.typeMessages = typeMessages;
		this.isPolling = isPolling;
	}

	@Override
	public Type<LoadMessagesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadMessagesEventHandler handler) {
		handler.onLoadMessages(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.LOAD_MESSAGES_EVENT;
	}

	public String getTypeMessages() {
		return typeMessages;
	}

	public boolean isPolling() {
		return isPolling;
	}

}
