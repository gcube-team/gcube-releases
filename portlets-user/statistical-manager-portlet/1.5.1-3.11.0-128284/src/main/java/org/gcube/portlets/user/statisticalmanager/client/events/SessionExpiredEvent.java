package org.gcube.portlets.user.statisticalmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HasHandlers;

public class SessionExpiredEvent extends
		GwtEvent<SessionExpiredEvent.SessionExpiredHandler> {

	public static Type<SessionExpiredHandler> TYPE = new Type<SessionExpiredHandler>();

	public interface SessionExpiredHandler extends EventHandler {
		void onSessionExpired(SessionExpiredEvent event);
	}

	public SessionExpiredEvent() {
	}

	@Override
	protected void dispatch(SessionExpiredHandler handler) {
		handler.onSessionExpired(this);
	}

	@Override
	public Type<SessionExpiredHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SessionExpiredHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source) {
		source.fireEvent(new SessionExpiredEvent());
	}
}
