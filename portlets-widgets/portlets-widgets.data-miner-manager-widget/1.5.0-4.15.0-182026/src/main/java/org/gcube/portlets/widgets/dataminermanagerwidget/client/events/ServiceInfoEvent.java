package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfo;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Service Info Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceInfoEvent extends GwtEvent<ServiceInfoEvent.ServiceInfoEventHandler> {

	public static Type<ServiceInfoEventHandler> TYPE = new Type<ServiceInfoEventHandler>();
	private ServiceInfo serviceInfo;

	public interface ServiceInfoEventHandler extends EventHandler {
		void onRequest(ServiceInfoEvent event);
	}

	public interface HasServiceInfoEventHandler extends HasHandlers {
		public HandlerRegistration addServiceInfoEventHandler(ServiceInfoEventHandler handler);
	}

	public ServiceInfoEvent(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	@Override
	protected void dispatch(ServiceInfoEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<ServiceInfoEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ServiceInfoEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ServiceInfoEvent event) {
		source.fireEvent(event);
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	@Override
	public String toString() {
		return "ServiceInfoEvent [serviceInfo=" + serviceInfo + "]";
	}

}
