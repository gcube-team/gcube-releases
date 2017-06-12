package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Input Save Ready Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InputReadyEvent extends
		GwtEvent<InputReadyEvent.InputReadyEventHandler> {

	public static Type<InputReadyEventHandler> TYPE = new Type<InputReadyEventHandler>();
	private InputData inputData;

	public interface InputReadyEventHandler extends EventHandler {
		void onInputReady(InputReadyEvent event);
	}

	public interface HasInputReadyEventHandler extends HasHandlers {
		public HandlerRegistration addInputReadyEventHandler(
				InputReadyEventHandler handler);
	}

	public InputReadyEvent(InputData inputData) {
		this.inputData = inputData;
	}

	@Override
	protected void dispatch(InputReadyEventHandler handler) {
		handler.onInputReady(this);
	}

	@Override
	public Type<InputReadyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<InputReadyEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			InputReadyEvent inputSaveReadyEvent) {
		source.fireEvent(inputSaveReadyEvent);
	}

	public InputData getInputData() {
		return inputData;
	}

	@Override
	public String toString() {
		return "InputReadyEvent [inputData=" + inputData + "]";
	}

	
}
