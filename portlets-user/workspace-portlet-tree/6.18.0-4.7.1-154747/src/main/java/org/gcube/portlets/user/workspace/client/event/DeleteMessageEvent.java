package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.MessageModel;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteMessageEvent extends GwtEvent<DeleteMessageEventHandler> implements GuiEventInterface{
	public static Type<DeleteMessageEventHandler> TYPE = new Type<DeleteMessageEventHandler>();
	
	private MessageModel messageTarget = null;
	
	public DeleteMessageEvent(MessageModel messageModel) {
		this.messageTarget = messageModel;
	}

	@Override
	public Type<DeleteMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteMessageEventHandler handler) {
		handler.onDeleteMessage(this);
		
	}

	public MessageModel getMessageTarget() {
		return messageTarget;
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.DELETED_MESSAGE;
	}
}
