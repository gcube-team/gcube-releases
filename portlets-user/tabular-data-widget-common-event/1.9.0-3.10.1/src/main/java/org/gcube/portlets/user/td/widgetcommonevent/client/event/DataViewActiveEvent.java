package org.gcube.portlets.user.td.widgetcommonevent.client.event;



import org.gcube.portlets.user.td.widgetcommonevent.client.type.DataViewActiveType;
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
public class DataViewActiveEvent extends
		GwtEvent<DataViewActiveEvent.DataViewActiveEventHandler> {

	public static Type<DataViewActiveEventHandler> TYPE = new Type<DataViewActiveEventHandler>();
	private DataViewActiveType dataViewActiveType;
	private DataView dataView;
	private DataView oldDataView;
	
	public interface DataViewActiveEventHandler extends EventHandler {
		void onDataViewActive(DataViewActiveEvent event);
	}

	public interface HasDataViewActiveEventHandler extends HasHandlers {
		public HandlerRegistration addDataViewActiveEventHandler(
				DataViewActiveEventHandler handler);
	}
	
	
	
	public DataViewActiveEvent() {
	}
	
	
	@Override
	protected void dispatch(DataViewActiveEventHandler handler) {
		handler.onDataViewActive(this);
	}

	@Override
	public Type<DataViewActiveEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DataViewActiveEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			DataViewActiveEvent dataViewActiveEvent) {
		source.fireEvent(dataViewActiveEvent);
	}

	

	public DataViewActiveType getDataViewActiveType() {
		return dataViewActiveType;
	}


	public void setDataViewActiveType(DataViewActiveType dataViewActiveType) {
		this.dataViewActiveType = dataViewActiveType;
	}


	public DataView getDataView() {
		return dataView;
	}


	public void setDataView(DataView dataView) {
		this.dataView = dataView;
	}


	public DataView getOldDataView() {
		return oldDataView;
	}


	public void setOldDataView(DataView oldDataView) {
		this.oldDataView = oldDataView;
	}


	@Override
	public String toString() {
		return "DataViewActiveEvent [dataViewActiveType=" + dataViewActiveType
				+ ", dataView=" + dataView + ", oldDataView=" + oldDataView
				+ "]";
	}


	
	
	
	
	
	

}
