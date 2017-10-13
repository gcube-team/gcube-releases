package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaRegionType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaRequestEventType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Data Miner Work Area Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataMinerWorkAreaRequestEvent
		extends
		GwtEvent<DataMinerWorkAreaRequestEvent.DataMinerWorkAreaRequestEventHandler> {

	public static Type<DataMinerWorkAreaRequestEventHandler> TYPE = new Type<DataMinerWorkAreaRequestEventHandler>();
	private DataMinerWorkAreaRequestEventType dataMinerWorkAreaRequestEventType;
	private DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType;

	public interface DataMinerWorkAreaRequestEventHandler extends EventHandler {
		void onRequest(DataMinerWorkAreaRequestEvent event);
	}

	public interface HasDataMinerWorkAreaRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addDataMinerWorkAreaRequestEventHandler(
				DataMinerWorkAreaRequestEventHandler handler);
	}

	public DataMinerWorkAreaRequestEvent(
			DataMinerWorkAreaRequestEventType dataMinerWorkAreaRequestEventType,
			DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType) {
		this.dataMinerWorkAreaRequestEventType = dataMinerWorkAreaRequestEventType;
		this.dataMinerWorkAreaRegionType = dataMinerWorkAreaRegionType;

	}

	@Override
	protected void dispatch(DataMinerWorkAreaRequestEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<DataMinerWorkAreaRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DataMinerWorkAreaRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			DataMinerWorkAreaRequestEvent event) {
		source.fireEvent(event);
	}

	public DataMinerWorkAreaRequestEventType getDataMinerWorkAreaRequestEventType() {
		return dataMinerWorkAreaRequestEventType;
	}

	public DataMinerWorkAreaRegionType getDataMinerWorkAreaRegionType() {
		return dataMinerWorkAreaRegionType;
	}

	@Override
	public String toString() {
		return "DataMinerWorkAreaRequestEvent [dataMinerWorkAreaRequestEventType="
				+ dataMinerWorkAreaRequestEventType
				+ ", dataMinerWorkAreaRegionType="
				+ dataMinerWorkAreaRegionType + "]";
	}

}
