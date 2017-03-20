package org.gcube.portlets.user.messages.client.event;

import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class OpenMessageEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 6, 2015
 */
public class OpenMessageEvent extends GwtEvent<OpenMessageEventHandler> implements GuiEventInterface {
	public static Type<OpenMessageEventHandler> TYPE = new Type<OpenMessageEventHandler>();
	
	private String messageIdentifier = null; //Report template
	private OpenType openType;
	private String messageType;
	private MessageModel message;//Added for Massi
	
	/**
	 * The Enum OpenType.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Oct 6, 2015
	 */
	public enum OpenType {REPLY, REPLYALL, FORWARD}//Refactor for Massi
	
	/**
	 * Instantiates a new open message event.
	 *
	 * @param messageIdentifier the message identifier
	 * @param openType the open type
	 * @param messageType the message type
	 */
	public OpenMessageEvent(String messageIdentifier, OpenType openType, String messageType) {
		this.messageIdentifier = messageIdentifier;
		this.openType = openType;
		this.messageType = messageType;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<OpenMessageEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(OpenMessageEventHandler handler) {
		handler.onOpenMessage(this);
	}

	/**
	 * Gets the message identifier.
	 *
	 * @return the message identifier
	 */
	public String getMessageIdentifier() {
		return messageIdentifier;
	}

	/**
	 * Gets the open type.
	 *
	 * @return the open type
	 */
	public OpenType getOpenType() {
		return openType;
	}
	
	/**
	 * Gets the open type to string.
	 *
	 * @return the open type to string
	 */
	public String getOpenTypeToString() {
		return openType.toString();
	}

	/**
	 * Gets the message type.
	 *
	 * @return the message type
	 */
	public String getMessageType() {
		return messageType;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.REPLY_FORWARD_MESSAGE;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public MessageModel getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(MessageModel message) {
		this.message = message;
	}
	
	
}
