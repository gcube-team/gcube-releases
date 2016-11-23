package org.gcube.portlets.user.messages.client.event;

import org.gcube.portlets.user.messages.client.view.message.attach.AttachOpenListner;

import com.google.gwt.event.shared.GwtEvent;

public class SaveAttachmentAndOpenEvent extends GwtEvent<SaveAttachmentAndOpenEventHandler> {
	public static Type<SaveAttachmentAndOpenEventHandler> TYPE = new Type<SaveAttachmentAndOpenEventHandler>();
	
	private String messageIdentifier = null;

	private String messageType;

	private AttachOpenListner attachOpenListner;

	private String attachmentId;
	
	public SaveAttachmentAndOpenEvent(String messageIdentifier, String messageType, String attachId, AttachOpenListner listner) {
		this.messageIdentifier = messageIdentifier;
		this.messageType = messageType;
		this.attachOpenListner = listner;
		this.setAttachmentId(attachId);
	}

	public AttachOpenListner getAttachOpenListner() {
		return attachOpenListner;
	}

	@Override
	public Type<SaveAttachmentAndOpenEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveAttachmentAndOpenEventHandler handler) {
		handler.onSaveAttachmentsAndOpen(this);
		
	}

	public String getMessageIdentifier() {
		return messageIdentifier;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
}
