package org.gcube.portlets.widgets.netcdfbasicwidgets.client.event;

import java.util.ArrayList;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Select Area Dialog Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SelectVariableEvent extends GwtEvent<SelectVariableEvent.SelectVariableEventHandler> {

	public static Type<SelectVariableEventHandler> TYPE = new Type<SelectVariableEventHandler>();
	private SelectVariableEventType selectVariableEventType;
	private ArrayList<VariableData> variables;
	private String errorMessage;
	private Exception exception;

	public interface SelectVariableEventHandler extends EventHandler {
		void onResponse(SelectVariableEvent event);
	}

	public interface HasSelectVariableEventHandler extends HasHandlers {
		public HandlerRegistration addSelectVariableEventHandler(SelectVariableEventHandler handler);
	}

	public SelectVariableEvent(SelectVariableEventType selectVariableEventType) {
		this.selectVariableEventType = selectVariableEventType;
	}

	@Override
	protected void dispatch(SelectVariableEventHandler handler) {
		handler.onResponse(this);
	}

	@Override
	public Type<SelectVariableEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SelectVariableEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, SelectVariableEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public SelectVariableEventType getSelectVariableEventType() {
		return selectVariableEventType;
	}


	public ArrayList<VariableData> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<VariableData> variables) {
		this.variables = variables;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return "SelectVariableEvent [selectVariableEventType=" + selectVariableEventType + ", variables=" + variables
				+ ", errorMessage=" + errorMessage + ", exception=" + exception + "]";
	}

	
}
