package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SaveAttachmentsEvent extends GwtEvent<SaveAttachmentsEventHandler> {
	public static Type<SaveAttachmentsEventHandler> TYPE = new Type<SaveAttachmentsEventHandler>();
	
	private String messageIdentifier = null;

	private String messageType; 
	
	public SaveAttachmentsEvent(String messageIdentifier, String messageType) {
		this.messageIdentifier = messageIdentifier;
		this.messageType = messageType;
	}

	@Override
	public Type<SaveAttachmentsEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(SaveAttachmentsEventHandler handler) {
		handler.onSaveAttachments(this);
		
	}

	public String getMessageIdentifier() {
		return messageIdentifier;
	}

	public String getMessageType() {
		return messageType;
	}
}
