package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaEventType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaRegionType;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.DataMinerWorkArea;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Data Miner Work Area Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerWorkAreaEvent extends
		GwtEvent<DataMinerWorkAreaEvent.DataMinerWorkAreaEventHandler> {

	public static Type<DataMinerWorkAreaEventHandler> TYPE = new Type<DataMinerWorkAreaEventHandler>();
	private DataMinerWorkAreaEventType dataMinerWorkAreaEventType;
	private DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType;
	private DataMinerWorkArea dataMinerWorkArea;

	public interface DataMinerWorkAreaEventHandler extends EventHandler {
		void onChange(DataMinerWorkAreaEvent event);
	}

	public interface HasDataMinerWorkAreaEventHandler extends HasHandlers {
		public HandlerRegistration addDataMinerWorkAreaEventHandler(
				DataMinerWorkAreaEventHandler handler);
	}

	public DataMinerWorkAreaEvent(
			DataMinerWorkAreaEventType dataMinerWorkAreaEventType,
			DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType,
			DataMinerWorkArea dataMinerWorkArea) {
		this.dataMinerWorkAreaEventType = dataMinerWorkAreaEventType;
		this.dataMinerWorkAreaRegionType = dataMinerWorkAreaRegionType;
		this.dataMinerWorkArea = dataMinerWorkArea;
	}

	@Override
	protected void dispatch(DataMinerWorkAreaEventHandler handler) {
		handler.onChange(this);
	}

	@Override
	public Type<DataMinerWorkAreaEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DataMinerWorkAreaEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			DataMinerWorkAreaEvent dataMinerWorkAreaEvent) {
		source.fireEvent(dataMinerWorkAreaEvent);
	}

	public DataMinerWorkAreaEventType getDataMinerWorkAreaEventType() {
		return dataMinerWorkAreaEventType;
	}

	public DataMinerWorkArea getDataMinerWorkArea() {
		return dataMinerWorkArea;
	}

	public DataMinerWorkAreaRegionType getDataMinerWorkAreaRegionType() {
		return dataMinerWorkAreaRegionType;
	}

	@Override
	public String toString() {
		return "DataMinerWorkAreaEvent [dataMinerWorkAreaEventType="
				+ dataMinerWorkAreaEventType + ", dataMinerWorkAreaRegionType="
				+ dataMinerWorkAreaRegionType + ", dataMinerWorkArea="
				+ dataMinerWorkArea + "]";
	}

}
