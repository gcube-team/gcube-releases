package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * External Execution Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExternalExecutionEvent extends
		GwtEvent<ExternalExecutionEvent.ExternalExecutionEventHandler> {

	public static Type<ExternalExecutionEventHandler> TYPE = new Type<ExternalExecutionEventHandler>();
	private Operator op;

	public interface ExternalExecutionEventHandler extends EventHandler {
		void onSubmit(ExternalExecutionEvent event);
	}

	public interface HasExternalExecutionEventHandler extends
			HasHandlers {
		public HandlerRegistration addExternalExecutionEventHandler(
				ExternalExecutionEventHandler handler);
	}

	public ExternalExecutionEvent(Operator op) {
		this.op = op;
	}

	@Override
	protected void dispatch(ExternalExecutionEventHandler handler) {
		handler.onSubmit(this);
	}

	@Override
	public Type<ExternalExecutionEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ExternalExecutionEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ExternalExecutionEvent event) {
		source.fireEvent(event);
	}

	public Operator getOp() {
		return op;
	}

	@Override
	public String toString() {
		return "ExternalExecutionEvent [op=" + op + "]";
	}

	
}
