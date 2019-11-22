package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 4, 2013
 *
 */
public class SessionExpiredEvent extends GwtEvent<SessionExpiredEventHandler> implements GuiEventInterface {
	public static Type<SessionExpiredEventHandler> TYPE = new Type<SessionExpiredEventHandler>();


	@Override
	public Type<SessionExpiredEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SessionExpiredEventHandler handler) {
		handler.onSessionExpired(this);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SESSION_EXPIRED;
	}

}
