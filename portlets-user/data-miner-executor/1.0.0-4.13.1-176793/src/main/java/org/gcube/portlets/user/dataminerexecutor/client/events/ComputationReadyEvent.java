package org.gcube.portlets.user.dataminerexecutor.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Computation Ready Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationReadyEvent extends
		GwtEvent<ComputationReadyEvent.ComputationReadyEventHandler> {

	public static Type<ComputationReadyEventHandler> TYPE = new Type<ComputationReadyEventHandler>();
	private Operator operator;

	public interface ComputationReadyEventHandler extends EventHandler {
		void onReady(ComputationReadyEvent event);
	}

	public interface HasComputationReadyEventHandler extends HasHandlers {
		public HandlerRegistration addComputationReadyEventHandler(
				ComputationReadyEventHandler handler);
	}

	public ComputationReadyEvent(Operator operator) {
		super();
		this.operator = operator;
	}

	@Override
	protected void dispatch(ComputationReadyEventHandler handler) {
		handler.onReady(this);
	}

	@Override
	public Type<ComputationReadyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ComputationReadyEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ComputationReadyEvent event) {
		source.fireEvent(event);
	}

	public static Type<ComputationReadyEventHandler> getTYPE() {
		return TYPE;
	}

	public Operator getOperator() {
		return operator;
	}

	
	@Override
	public String toString() {
		return "ComputationReadyEvent [operator=" + operator + "]";
	}

}
