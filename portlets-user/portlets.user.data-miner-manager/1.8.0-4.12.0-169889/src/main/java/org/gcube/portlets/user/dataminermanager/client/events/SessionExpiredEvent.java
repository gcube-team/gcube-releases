package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.client.type.SessionExpiredEventType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Session Expired Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SessionExpiredEvent extends GwtEvent<SessionExpiredEvent.SessionExpiredEventHandler> {

	public static Type<SessionExpiredEventHandler> TYPE = new Type<SessionExpiredEventHandler>();
	private SessionExpiredEventType sessionExpiredEventType;

	public interface SessionExpiredEventHandler extends EventHandler {
		void onChange(SessionExpiredEvent event);
	}

	public interface HasSessionExpiredEventHandler extends HasHandlers {
		public HandlerRegistration addSessionExpiredEventHandler(
				SessionExpiredEventHandler handler);
	}

	public SessionExpiredEvent() {
		this.sessionExpiredEventType = SessionExpiredEventType.EXPIREDONSERVER;
	}

	public SessionExpiredEvent(SessionExpiredEventType sessionExpiredEventType) {
		this.sessionExpiredEventType = sessionExpiredEventType;
	}

	@Override
	protected void dispatch(SessionExpiredEventHandler handler) {
		handler.onChange(this);
	}

	@Override
	public Type<SessionExpiredEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SessionExpiredEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, SessionExpiredEvent sessionExpieredEvent) {
		source.fireEvent(sessionExpieredEvent);
	}

	public SessionExpiredEventType getSessionExpiredEventType() {
		return sessionExpiredEventType;
	}

	@Override
	public String toString() {
		return "SessionExpiredEvent [sessionExpiredEventType="
				+ sessionExpiredEventType + "]";
	}
	
	
	
}
