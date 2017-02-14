package org.gcube.portlets.user.messages.client.event;

import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.event.shared.GwtEvent;

public class PreviewMessageEvent extends GwtEvent<PreviewMessageEventHandler> implements GuiEventInterface{
	public static Type<PreviewMessageEventHandler> TYPE = new Type<PreviewMessageEventHandler>();
	
	private String messageIdentifier = null; //Report template
	private MessageModel message;
	private String messageType;

	
	public PreviewMessageEvent(String messageIdentifier, String messageType) {
		this.messageIdentifier = messageIdentifier;
		this.messageType = messageType;
	}

	@Override
	public Type<PreviewMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PreviewMessageEventHandler handler) {
		handler.onPreviewMessage(this);
		
	}

	public String getMessageIdentifier() {
		return messageIdentifier;
	}

	public void setMessage(MessageModel message) {
		this.message = message;
		
	}

	public MessageModel getMessage() {
		return message;
	}

	@Override
	public EventsTypeEnum getKey() {
		return  EventsTypeEnum.SELECTED_MESSAGE;
	}

	public String getMessageType() {
		return messageType;
	}
}
