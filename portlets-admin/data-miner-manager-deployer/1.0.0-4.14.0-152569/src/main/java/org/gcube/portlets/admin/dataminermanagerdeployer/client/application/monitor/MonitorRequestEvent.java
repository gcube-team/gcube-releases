package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Monitor Request Event
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MonitorRequestEvent extends
		GwtEvent<MonitorRequestEvent.MonitorRequestEventHandler> {

	public static Type<MonitorRequestEventHandler> TYPE = new Type<MonitorRequestEventHandler>();
	

	public interface MonitorRequestEventHandler extends EventHandler {
		void onMonitor(MonitorRequestEvent event);
	}

	public interface HasMonitorRequestEventHandler extends HasHandlers {
		public HandlerRegistration addMonitorRequestEventHandler(
				MonitorRequestEventHandler handler);
	}

	public MonitorRequestEvent() {
		
	}

	@Override
	protected void dispatch(MonitorRequestEventHandler handler) {
		handler.onMonitor(this);
	}

	@Override
	public Type<MonitorRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MonitorRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MonitorRequestEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "MonitorRequestEvent []";
	}

	
	

}