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
public class ScopeDataEvent extends GwtEvent<ScopeDataEvent.ScopeDataEventHandler> {

	private ScopeData scopeData;

	public interface ScopeDataEventHandler extends EventHandler {
		void onData(ScopeDataEvent event);
	}

	public static final Type<ScopeDataEventHandler> TYPE = new Type<>();

	public ScopeDataEvent(ScopeData scopeData) {
		this.scopeData = scopeData;
	}

	public static void fire(HasHandlers source, ScopeDataEvent event) {
		source.fireEvent(event);
	}

	@Override
	public Type<ScopeDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ScopeDataEventHandler handler) {
		handler.onData(this);
	}

	public ScopeData getScopeData() {
		return scopeData;
	}

	@Override
	public String toString() {
		return "ScopeDataEvent [scopeData=" + scopeData + "]";
	}

}