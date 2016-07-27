package org.gcube.portlets.user.messages.client.event;

import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MarkMessageEvent extends GwtEvent<MarkMessageEventHandler> implements GuiEventInterface{
	public static Type<MarkMessageEventHandler> TYPE = new Type<MarkMessageEventHandler>();
	
	private MessageModel messageTarget = null;
	private boolean boolMark;

	private MarkType markType;
	public enum MarkType {READ, OPEN, BOTH}
	
	public MarkMessageEvent(MessageModel messageModel, boolean boolMark, MarkType markType) {
		this.messageTarget = messageModel;
		this.boolMark = boolMark;
		this.markType = markType;
	}

	@Override
	public Type<MarkMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MarkMessageEventHandler handler) {
		handler.onMark(this);
		
	}

	public MessageModel getMessageTarget() {
		return messageTarget;
	}

	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.MARK_MESSAGE_AS_READ;
	}

	public boolean getBoolMark() {
		return boolMark;
	}

	public MarkType getMarkType() {
		return markType;
	}
	
	
	public String getMarkTypeToString() {
		return markType.toString();
	}
	
}
