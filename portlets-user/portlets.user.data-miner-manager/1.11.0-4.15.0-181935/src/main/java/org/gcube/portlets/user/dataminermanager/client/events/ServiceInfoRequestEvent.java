package org.gcube.portlets.user.dataminermanager.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Service Info Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceInfoRequestEvent extends GwtEvent<ServiceInfoRequestEvent.ServiceInfoRequestEventHandler> {

	public static Type<ServiceInfoRequestEventHandler> TYPE = new Type<ServiceInfoRequestEventHandler>();

	public interface ServiceInfoRequestEventHandler extends EventHandler {
		void onRequest(ServiceInfoRequestEvent event);
	}

	public interface HasServiceInfoRequestEventHandler extends HasHandlers {
		public HandlerRegistration addServiceInfoRequestEventHandler(ServiceInfoRequestEventHandler handler);
	}

	public ServiceInfoRequestEvent() {

	}

	@Override
	protected void dispatch(ServiceInfoRequestEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<ServiceInfoRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ServiceInfoRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ServiceInfoRequestEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "ServiceInfoRequestEvent []";
	}

	
}
