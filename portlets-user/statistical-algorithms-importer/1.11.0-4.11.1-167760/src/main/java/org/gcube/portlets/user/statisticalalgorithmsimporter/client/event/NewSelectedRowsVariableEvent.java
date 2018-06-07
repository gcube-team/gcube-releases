package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Input Save Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NewSelectedRowsVariableEvent
		extends
		GwtEvent<NewSelectedRowsVariableEvent.NewSelectedRowsVariableEventHandler> {

	public static Type<NewSelectedRowsVariableEventHandler> TYPE = new Type<NewSelectedRowsVariableEventHandler>();
	private InputOutputVariables selectedRowsVariable;

	public interface NewSelectedRowsVariableEventHandler extends EventHandler {
		void onNewVariable(NewSelectedRowsVariableEvent event);
	}

	public interface HasNewSelectedRowsVariableEventHandler extends HasHandlers {
		public HandlerRegistration addNewSelectedRowsVariableEventHandler(
				NewSelectedRowsVariableEventHandler handler);
	}

	public NewSelectedRowsVariableEvent(
			InputOutputVariables selectedRowsVariable) {
		this.selectedRowsVariable = selectedRowsVariable;
	}

	@Override
	protected void dispatch(NewSelectedRowsVariableEventHandler handler) {
		handler.onNewVariable(this);
	}

	@Override
	public Type<NewSelectedRowsVariableEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<NewSelectedRowsVariableEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			NewSelectedRowsVariableEvent newSelectedRowsVariableEvent) {
		source.fireEvent(newSelectedRowsVariableEvent);
	}

	public InputOutputVariables getSelectedRowsVariable() {
		return selectedRowsVariable;
	}

	@Override
	public String toString() {
		return "NewSelectedRowsVariableEvent [selectedRowsVariable="
				+ selectedRowsVariable + "]";
	}

}
