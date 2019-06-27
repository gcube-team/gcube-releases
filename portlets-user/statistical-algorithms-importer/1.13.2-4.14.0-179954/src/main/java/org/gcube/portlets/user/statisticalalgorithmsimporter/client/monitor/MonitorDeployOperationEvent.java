package org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor;

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
public class MonitorDeployOperationEvent
		extends GwtEvent<MonitorDeployOperationEvent.MonitorDeployOperationEventHandler> {

	public static Type<MonitorDeployOperationEventHandler> TYPE = new Type<MonitorDeployOperationEventHandler>();
	

	public interface MonitorDeployOperationEventHandler extends EventHandler {
		void onMonitor(MonitorDeployOperationEvent event);
	}

	public interface HasMonitorDeployOperationEventHandler extends HasHandlers {
		public HandlerRegistration addMonitorDeployOperationEventHandler(MonitorDeployOperationEventHandler handler);
	}

	public MonitorDeployOperationEvent() {
		
	}

	@Override
	protected void dispatch(MonitorDeployOperationEventHandler handler) {
		handler.onMonitor(this);
	}

	@Override
	public Type<MonitorDeployOperationEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MonitorDeployOperationEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MonitorDeployOperationEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "MonitorDeployOperationEvent []";
	}

	

}