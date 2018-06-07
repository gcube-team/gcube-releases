package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Start Computation Execution Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StartComputationExecutionEvent
		extends
		GwtEvent<StartComputationExecutionEvent.StartComputationExecutionEventHandler> {

	public static Type<StartComputationExecutionEventHandler> TYPE = new Type<StartComputationExecutionEventHandler>();
	private ComputationId computationId;
	private int computationStatusPanelIndex;

	public interface StartComputationExecutionEventHandler extends EventHandler {
		void onStart(StartComputationExecutionEvent event);
	}

	public interface HasStartComputationExecutionEventHandler extends
			HasHandlers {
		public HandlerRegistration addStartComputationExecutionEventHandler(
				StartComputationExecutionEventHandler handler);
	}

	public StartComputationExecutionEvent(ComputationId computationId,
			int computationStatusPanelIndex) {
		this.computationId = computationId;
		this.computationStatusPanelIndex = computationStatusPanelIndex;
	}

	@Override
	protected void dispatch(StartComputationExecutionEventHandler handler) {
		handler.onStart(this);
	}

	@Override
	public Type<StartComputationExecutionEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<StartComputationExecutionEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			StartComputationExecutionEvent event) {
		source.fireEvent(event);
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	public int getComputationStatusPanelIndex() {
		return computationStatusPanelIndex;
	}

	@Override
	public String toString() {
		return "StartComputationExecutionEvent [computationId=" + computationId
				+ ", computationStatusPanelIndex="
				+ computationStatusPanelIndex + "]";
	}

}
