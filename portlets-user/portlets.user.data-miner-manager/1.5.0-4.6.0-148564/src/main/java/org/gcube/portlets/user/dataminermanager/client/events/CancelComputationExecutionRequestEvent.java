package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Cancel Computation Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CancelComputationExecutionRequestEvent
		extends
		GwtEvent<CancelComputationExecutionRequestEvent.CancelComputationExecutionRequestEventHandler> {

	public static Type<CancelComputationExecutionRequestEventHandler> TYPE = new Type<CancelComputationExecutionRequestEventHandler>();
	private ComputationId computationId;

	public interface CancelComputationExecutionRequestEventHandler extends
			EventHandler {
		void onCancel(CancelComputationExecutionRequestEvent event);
	}

	public interface HasCancelComputationExecutionRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addCancelComputationExecutionRequestEventHandler(
				CancelComputationExecutionRequestEventHandler handler);
	}

	public CancelComputationExecutionRequestEvent(ComputationId computationId) {
		this.computationId = computationId;
	}

	@Override
	protected void dispatch(
			CancelComputationExecutionRequestEventHandler handler) {
		handler.onCancel(this);
	}

	@Override
	public Type<CancelComputationExecutionRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<CancelComputationExecutionRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			CancelComputationExecutionRequestEvent event) {
		source.fireEvent(event);
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	@Override
	public String toString() {
		return "CancelComputationExecutionRequestEvent [computationId="
				+ computationId + "]";
	}

}
