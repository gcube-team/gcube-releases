package org.gcube.portlets.admin.accountingmanager.client.monitor;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Accounting Period Request Event
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class MonitorRequestEvent extends
		GwtEvent<MonitorRequestEvent.MonitorRequestEventHandler> {

	public static Type<MonitorRequestEventHandler> TYPE = new Type<MonitorRequestEventHandler>();
	private MonitorRequestType monitorRequestType;

	public interface MonitorRequestEventHandler extends EventHandler {
		void onMonitor(MonitorRequestEvent event);
	}

	public interface HasMonitorRequestEventHandler extends HasHandlers {
		public HandlerRegistration addMonitorRequestEventHandler(
				MonitorRequestEventHandler handler);
	}

	public MonitorRequestEvent(MonitorRequestType monitorRequestType) {
		this.monitorRequestType = monitorRequestType;
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

	public MonitorRequestType getMonitorRequestType() {
		return monitorRequestType;
	}

	@Override
	public String toString() {
		return "MonitorRequestEvent [monitorRequestType=" + monitorRequestType
				+ "]";
	}

}
