package org.gcube.portlets.widgets.netcdfbasicwidgets.client.event;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;

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
public class NetCDFDataEvent extends GwtEvent<NetCDFDataEvent.NetCDFDataEventHandler> {

	public static Type<NetCDFDataEventHandler> TYPE = new Type<NetCDFDataEventHandler>();

	private NetCDFData netCDFData;

	public interface NetCDFDataEventHandler extends EventHandler {
		void onNetCDFDataReady(NetCDFDataEvent event);
	}

	public interface HasNetCDFDataEventHandler extends HasHandlers {
		public HandlerRegistration addNetCDFDataEventHandler(NetCDFDataEventHandler handler);
	}

	public NetCDFDataEvent(NetCDFData netCDFData) {
		this.netCDFData = netCDFData;
	}

	@Override
	protected void dispatch(NetCDFDataEventHandler handler) {
		handler.onNetCDFDataReady(this);
	}

	@Override
	public Type<NetCDFDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<NetCDFDataEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, NetCDFDataEvent event) {
		source.fireEvent(event);
	}

	public NetCDFData getNetCDFData() {
		return netCDFData;
	}

	@Override
	public String toString() {
		return "NetCDFDataEvent [netCDFData=" + netCDFData + "]";
	}

}
