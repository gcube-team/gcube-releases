package org.gcube.portlets.user.dataminerexecutor.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InvocationModelEvent
		extends GwtEvent<InvocationModelEvent.InvocationModelEventHandler> {

	public static Type<InvocationModelEventHandler> TYPE = new Type<InvocationModelEventHandler>();
	private Operator operator;
	
	public interface InvocationModelEventHandler extends EventHandler {
		void onInvocation(InvocationModelEvent event);
	}

	public interface HasInvocationModelEventHandler extends HasHandlers {
		public HandlerRegistration addInvocationModelEventHandler(
				InvocationModelEventHandler handler);
	}

	
	public InvocationModelEvent(Operator operator) {
		this.operator=operator;
	}

	@Override
	protected void dispatch(InvocationModelEventHandler handler) {
		handler.onInvocation(this);
	}

	@Override
	public Type<InvocationModelEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<InvocationModelEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, InvocationModelEvent event) {
		source.fireEvent(event);
	}

	public Operator getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return "InvocationModelEvent [operator=" + operator + "]";
	}

	
}
