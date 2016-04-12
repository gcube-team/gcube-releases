package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.DataViewRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataView;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DataViewRequestEvent extends
		GwtEvent<DataViewRequestEvent.DataViewRequestEventHandler> {

	public static Type<DataViewRequestEventHandler> TYPE = new Type<DataViewRequestEventHandler>();
	private DataViewRequestType dataViewRequestType;
	private DataView dataView;

	public interface DataViewRequestEventHandler extends EventHandler {
		void onDataViewRequest(DataViewRequestEvent event);
	}

	public interface HasDataViewRequestEventHandler extends HasHandlers {
		public HandlerRegistration addDataViewRequestEventHandler(
				DataViewRequestEventHandler handler);
	}

	public DataViewRequestEvent() {
	}

	@Override
	protected void dispatch(DataViewRequestEventHandler handler) {
		handler.onDataViewRequest(this);
	}

	@Override
	public Type<DataViewRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DataViewRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			DataViewRequestEvent dataViewRequestEvent) {
		source.fireEvent(dataViewRequestEvent);
	}

	public DataViewRequestType getDataViewRequestType() {
		return dataViewRequestType;
	}

	public void setDataViewRequestType(DataViewRequestType dataViewRequestType) {
		this.dataViewRequestType = dataViewRequestType;
	}

	public DataView getDataView() {
		return dataView;
	}

	public void setDataView(DataView dataView) {
		this.dataView = dataView;
	}

	@Override
	public String toString() {
		return "DataViewRequestEvent [dataViewRequestType="
				+ dataViewRequestType + ", dataView=" + dataView + "]";
	}

}
