package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaElementType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Refresh DataMiner Work Area Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class RefreshDataMinerWorkAreaEvent extends
		GwtEvent<RefreshDataMinerWorkAreaEvent.RefreshDataMinerWorkAreaEventHandler> {

	public static Type<RefreshDataMinerWorkAreaEventHandler> TYPE = new Type<RefreshDataMinerWorkAreaEventHandler>();
	private DataMinerWorkAreaElementType dataMinerWorkAreaElementType;

	public interface RefreshDataMinerWorkAreaEventHandler extends EventHandler {
		void onRefresh(RefreshDataMinerWorkAreaEvent event);
	}

	public interface HasRefreshDataMinerWorkAreaEventHandler extends HasHandlers {
		public HandlerRegistration addRefreshDataMinerWorkAreaEventHandler(
				RefreshDataMinerWorkAreaEventHandler handler);
	}

	public RefreshDataMinerWorkAreaEvent(
			DataMinerWorkAreaElementType dataMinerWorkAreaElementType) {
		this.dataMinerWorkAreaElementType = dataMinerWorkAreaElementType;
	}

	@Override
	protected void dispatch(RefreshDataMinerWorkAreaEventHandler handler) {
		handler.onRefresh(this);
	}

	@Override
	public Type<RefreshDataMinerWorkAreaEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<RefreshDataMinerWorkAreaEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, RefreshDataMinerWorkAreaEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public DataMinerWorkAreaElementType getDataMinerWorkAreaElementType() {
		return dataMinerWorkAreaElementType;
	}

	@Override
	public String toString() {
		return "RefreshDataMinerWorkAreaEvent [dataMinerWorkAreaElementType="
				+ dataMinerWorkAreaElementType + "]";
	}

	

}
