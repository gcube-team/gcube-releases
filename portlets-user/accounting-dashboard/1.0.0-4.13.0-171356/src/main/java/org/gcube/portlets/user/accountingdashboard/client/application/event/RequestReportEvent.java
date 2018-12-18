package org.gcube.portlets.user.accountingdashboard.client.application.event;

import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class RequestReportEvent extends GwtEvent<RequestReportEvent.RequestReportEventHandler> {

	private ScopeData scopeData;

	public interface RequestReportEventHandler extends EventHandler {
		void onData(RequestReportEvent event);
	}

	public static final Type<RequestReportEventHandler> TYPE = new Type<>();

	public RequestReportEvent(ScopeData scopeData) {
		this.scopeData = scopeData;
	}

	public static void fire(HasHandlers source, RequestReportEvent event) {
		source.fireEvent(event);
	}

	@Override
	public Type<RequestReportEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RequestReportEventHandler handler) {
		handler.onData(this);
	}

	public ScopeData getScopeData() {
		return scopeData;
	}

	@Override
	public String toString() {
		return "RequestReportEvent [scopeData=" + scopeData + "]";
	}

	
}