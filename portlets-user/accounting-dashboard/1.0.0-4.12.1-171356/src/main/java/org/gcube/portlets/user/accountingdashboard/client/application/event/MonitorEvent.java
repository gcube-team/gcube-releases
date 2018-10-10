package org.gcube.portlets.user.accountingdashboard.client.application.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MonitorEvent extends GwtEvent<MonitorEvent.MonitorEventHandler> {
	
	private boolean enable;
	public interface MonitorEventHandler extends EventHandler {
		void onMonitor(MonitorEvent event);
	}

	public static final Type<MonitorEventHandler> TYPE = new Type<>();

	public MonitorEvent(boolean enable) {
		this.enable=enable;
	}

	public static void fire(HasHandlers source, MonitorEvent event) {
		source.fireEvent(event);
	}

	@Override
	public Type<MonitorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MonitorEventHandler handler) {
		handler.onMonitor(this);
	}

	public boolean isEnable() {
		return enable;
	}

	@Override
	public String toString() {
		return "MonitorEvent [enable=" + enable + "]";
	}

	
	
}