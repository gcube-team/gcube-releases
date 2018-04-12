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
public class StartComputationExecutionRequestEvent
		extends
		GwtEvent<StartComputationExecutionRequestEvent.StartComputationExecutionRequestEventHandler> {

	public static Type<StartComputationExecutionRequestEventHandler> TYPE = new Type<StartComputationExecutionRequestEventHandler>();
	private Operator op;
	private int computationStatusPanelIndex;

	public interface StartComputationExecutionRequestEventHandler extends
			EventHandler {
		void onStart(StartComputationExecutionRequestEvent event);
	}

	public interface HasStartComputationExecutionRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addStartComputationExecutionRequestEventHandler(
				StartComputationExecutionRequestEventHandler handler);
	}

	public StartComputationExecutionRequestEvent(Operator op,
			int computationStatusPanelIndex) {
		this.op = op;
		this.computationStatusPanelIndex = computationStatusPanelIndex;

	}

	@Override
	protected void dispatch(StartComputationExecutionRequestEventHandler handler) {
		handler.onStart(this);
	}

	@Override
	public Type<StartComputationExecutionRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<StartComputationExecutionRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			StartComputationExecutionRequestEvent event) {
		source.fireEvent(event);
	}

	public Operator getOp() {
		return op;
	}

	public int getComputationStatusPanelIndex() {
		return computationStatusPanelIndex;
	}

	@Override
	public String toString() {
		return "StartComputationExecutionRequestEvent [op=" + op
				+ ", computationStatusPanelIndex="
				+ computationStatusPanelIndex + "]";
	}

}
