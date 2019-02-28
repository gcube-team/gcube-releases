package org.gcube.portlets.user.dataminerexecutor.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InvocationModelRequestEvent
		extends GwtEvent<InvocationModelRequestEvent.InvocationModelRequestEventHandler> {

	public static Type<InvocationModelRequestEventHandler> TYPE = new Type<InvocationModelRequestEventHandler>();

	public interface InvocationModelRequestEventHandler extends EventHandler {
		void onInvocationRequest(InvocationModelRequestEvent event);
	}

	public interface HasInvocationModelEventHandler extends HasHandlers {
		public HandlerRegistration addInvocationModelEventHandler(InvocationModelRequestEventHandler handler);
	}

	public InvocationModelRequestEvent() {
	}

	@Override
	protected void dispatch(InvocationModelRequestEventHandler handler) {
		handler.onInvocationRequest(this);
	}

	@Override
	public Type<InvocationModelRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<InvocationModelRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, InvocationModelRequestEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "InvocationModelRequestEvent []";
	}

}
