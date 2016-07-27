package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Input Save Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InputRequestEvent extends
		GwtEvent<InputRequestEvent.InputRequestEventHandler> {

	public static Type<InputRequestEventHandler> TYPE = new Type<InputRequestEventHandler>();

	public interface InputRequestEventHandler extends EventHandler {
		void onInputRequest(InputRequestEvent event);
	}

	public interface HasInputRequestEventHandler extends HasHandlers {
		public HandlerRegistration addInputRequestEventHandler(
				InputRequestEventHandler handler);
	}

	public InputRequestEvent() {

	}

	@Override
	protected void dispatch(InputRequestEventHandler handler) {
		handler.onInputRequest(this);
	}

	@Override
	public Type<InputRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<InputRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, InputRequestEvent inputSaveEvent) {
		source.fireEvent(inputSaveEvent);
	}

	@Override
	public String toString() {
		return "InputRequestEvent []";
	}

	

}
