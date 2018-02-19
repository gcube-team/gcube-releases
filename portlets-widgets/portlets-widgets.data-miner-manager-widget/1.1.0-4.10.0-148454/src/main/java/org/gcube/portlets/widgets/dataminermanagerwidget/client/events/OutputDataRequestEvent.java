package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;


import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Output Data Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OutputDataRequestEvent extends
		GwtEvent<OutputDataRequestEvent.OutputDataRequestEventHandler> {

	public static Type<OutputDataRequestEventHandler> TYPE = new Type<OutputDataRequestEventHandler>();
	private ComputationId computationId;

	public interface OutputDataRequestEventHandler extends EventHandler {
		void onOutputRequest(OutputDataRequestEvent event);
	}

	public interface HasOutputDataRequestEventHandler extends HasHandlers {
		public HandlerRegistration addOutputDataRequestEventHandler(
				OutputDataRequestEventHandler handler);
	}

	public OutputDataRequestEvent(ComputationId computationId) {
		this.computationId = computationId;
	}

	@Override
	protected void dispatch(OutputDataRequestEventHandler handler) {
		handler.onOutputRequest(this);
	}

	@Override
	public Type<OutputDataRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<OutputDataRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, OutputDataRequestEvent event) {
		source.fireEvent(event);
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	@Override
	public String toString() {
		return "OutputDataRequestEvent [computationId=" + computationId + "]";
	}

	

}
