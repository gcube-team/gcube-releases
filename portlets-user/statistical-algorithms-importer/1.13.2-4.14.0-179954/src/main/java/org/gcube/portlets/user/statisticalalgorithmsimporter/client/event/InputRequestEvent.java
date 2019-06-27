package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Input Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InputRequestEvent extends GwtEvent<InputRequestEvent.InputRequestEventHandler> {

	public static Type<InputRequestEventHandler> TYPE = new Type<InputRequestEventHandler>();
	private boolean requested;

	public interface InputRequestEventHandler extends EventHandler {
		void onInputRequest(InputRequestEvent event);
	}

	public interface HasInputRequestEventHandler extends HasHandlers {
		public HandlerRegistration addInputRequestEventHandler(InputRequestEventHandler handler);
	}

	public InputRequestEvent() {
		requested = true;
	}

	@Override
	protected void dispatch(InputRequestEventHandler handler) {
		handler.onInputRequest(this);
	}

	@Override
	public Type<InputRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public boolean isRequested() {
		return requested;
	}

	@Override
	public String toString() {
		return "InputRequestEvent [requested=" + requested + "]";
	}

}
