package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;


import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.SessionExpiredType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class SessionExpiredEvent extends GwtEvent<SessionExpiredEvent.SessionExpiredEventHandler> {

	public static Type<SessionExpiredEventHandler> TYPE = new Type<SessionExpiredEventHandler>();
	private SessionExpiredType sessionExpiredType;
	
	public interface SessionExpiredEventHandler extends EventHandler {
		void onSessionExpired(SessionExpiredEvent event);
	}
	
	public interface HasSessionExpiredEventHandler extends HasHandlers{
		public HandlerRegistration addSessionExpiredEventHandler(SessionExpiredEventHandler handler);
	}

	public SessionExpiredEvent(SessionExpiredType sessionExpiredType) {
		this.sessionExpiredType = sessionExpiredType;
	}
	
	@Override
	protected void dispatch(SessionExpiredEventHandler handler) {
		handler.onSessionExpired(this);
	}

	@Override
	public Type<SessionExpiredEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SessionExpiredEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, SessionExpiredType sessionExpiredType) {
		source.fireEvent(new SessionExpiredEvent(sessionExpiredType));
	}

	public SessionExpiredType getSessionExpiredType() {
		return sessionExpiredType;
	}
	
	@Override
	public String toString() {
		return "SessionExpiredEvent [sessionExpiredType=" + sessionExpiredType
				+ "]";
	}
	
	

}
