package org.gcube.portlets.user.dataminerexecutor.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Output Data Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OutputDataEvent extends
		GwtEvent<OutputDataEvent.OutputDataEventHandler> {
	public static Type<OutputDataEventHandler> TYPE = new Type<OutputDataEventHandler>();
	public OutputData outputData;

	public interface OutputDataEventHandler extends EventHandler {
		void onOutput(OutputDataEvent event);
	}

	public interface HasOutputDataEventHandler extends HasHandlers {
		public HandlerRegistration addOutputDataEventHandler(
				OutputDataEventHandler handler);
	}

	public OutputDataEvent(OutputData outputData) {
		this.outputData = outputData;
	}

	@Override
	protected void dispatch(OutputDataEventHandler handler) {
		handler.onOutput(this);
	}

	@Override
	public Type<OutputDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<OutputDataEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, OutputDataEvent event) {
		source.fireEvent(event);
	}

	public OutputData getOutputData() {
		return outputData;
	}

	@Override
	public String toString() {
		return "OutputDataEvent [outputData=" + outputData + "]";
	}

}
