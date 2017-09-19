package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Resubmit Computation Execution Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ResubmitComputationExecutionEvent
		extends
		GwtEvent<ResubmitComputationExecutionEvent.ResubmitComputationExecutionEventHandler> {

	public static Type<ResubmitComputationExecutionEventHandler> TYPE = new Type<ResubmitComputationExecutionEventHandler>();
	private ComputationId computationId;

	public interface ResubmitComputationExecutionEventHandler extends
			EventHandler {
		void onResubmit(ResubmitComputationExecutionEvent event);
	}

	public interface HasResubmitComputationExecutionEventHandler extends
			HasHandlers {
		public HandlerRegistration addResubmitComputationExecutionEventHandler(
				ResubmitComputationExecutionEventHandler handler);
	}

	public ResubmitComputationExecutionEvent(ComputationId computationId) {
		this.computationId = computationId;
	}

	@Override
	protected void dispatch(ResubmitComputationExecutionEventHandler handler) {
		handler.onResubmit(this);
	}

	@Override
	public Type<ResubmitComputationExecutionEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ResubmitComputationExecutionEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ResubmitComputationExecutionEvent event) {
		source.fireEvent(event);
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	@Override
	public String toString() {
		return "ResubmitComputationExecutionEvent [computationId="
				+ computationId + "]";
	}

	
	
}
