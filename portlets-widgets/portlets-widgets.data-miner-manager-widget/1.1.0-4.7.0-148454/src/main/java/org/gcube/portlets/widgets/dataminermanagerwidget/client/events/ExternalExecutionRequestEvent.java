package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Start Computation Execution Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExternalExecutionRequestEvent extends
		GwtEvent<ExternalExecutionRequestEvent.ExternalExecutionRequestEventHandler> {

	public static Type<ExternalExecutionRequestEventHandler> TYPE = new Type<ExternalExecutionRequestEventHandler>();
	private Operator op;

	public interface ExternalExecutionRequestEventHandler extends EventHandler {
		void onSubmit(ExternalExecutionRequestEvent event);
	}

	public interface HasExternalExecutionRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addExternalExecutionRequestEventHandler(
				ExternalExecutionRequestEventHandler handler);
	}

	public ExternalExecutionRequestEvent(Operator op) {
		this.op = op;
	}

	@Override
	protected void dispatch(ExternalExecutionRequestEventHandler handler) {
		handler.onSubmit(this);
	}

	@Override
	public Type<ExternalExecutionRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ExternalExecutionRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ExternalExecutionRequestEvent event) {
		source.fireEvent(event);
	}

	public Operator getOp() {
		return op;
	}

	@Override
	public String toString() {
		return "ExternalExecutionRequestEvent [op=" + op + "]";
	}

}
