package org.gcube.portlets.widgets.netcdfbasicwidgets.client.event;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Select Area Dialog Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SampleVariableDataEvent extends GwtEvent<SampleVariableDataEvent.SampleVariableDataEventHandler> {

	public static Type<SampleVariableDataEventHandler> TYPE = new Type<SampleVariableDataEventHandler>();

	private VariableData variableData;
	private NetCDFValues sampleValues;

	public interface SampleVariableDataEventHandler extends EventHandler {
		void onSample(SampleVariableDataEvent event);
	}

	public interface HasSampleVariableDataEventHandler extends HasHandlers {
		public HandlerRegistration addSampleVariableDataEventHandler(SampleVariableDataEventHandler handler);
	}

	public SampleVariableDataEvent(VariableData variableData, NetCDFValues sampleValues) {
		this.variableData = variableData;
		this.sampleValues = sampleValues;
	}

	@Override
	protected void dispatch(SampleVariableDataEventHandler handler) {
		handler.onSample(this);
	}

	@Override
	public Type<SampleVariableDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SampleVariableDataEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, SampleVariableDataEvent event) {
		source.fireEvent(event);
	}

	public VariableData getVariableData() {
		return variableData;
	}

	public NetCDFValues getSampleValues() {
		return sampleValues;
	}

	@Override
	public String toString() {
		return "SampleVariableDataEvent [variableData=" + variableData + ", sampleValues=" + sampleValues + "]";
	}

}
