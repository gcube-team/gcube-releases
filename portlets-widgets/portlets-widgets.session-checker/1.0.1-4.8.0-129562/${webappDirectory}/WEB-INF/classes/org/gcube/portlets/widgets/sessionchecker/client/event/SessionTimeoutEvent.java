package org.gcube.portlets.widgets.sessionchecker.client.event;

import org.gcube.portlets.widgets.sessionchecker.shared.SessionInfoBean;

import com.google.gwt.event.shared.GwtEvent;



public class SessionTimeoutEvent  extends GwtEvent<SessionTimoutEventHandler> {
	public static Type<SessionTimoutEventHandler> TYPE = new Type<SessionTimoutEventHandler>();
		
	private SessionInfoBean sessionInfo;
	
	public SessionTimeoutEvent(SessionInfoBean sessionInfo) {
		this.sessionInfo = sessionInfo;
	}

	public SessionInfoBean getSessionInfo() {
		return sessionInfo;
	}

	@Override
	public Type<SessionTimoutEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SessionTimoutEventHandler handler) {
		handler.onSessionExpiration(this);
	}
}
