package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.tr.TabularResourceData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Tabular Resource Info Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabularResourceInfoEvent extends
		GwtEvent<TabularResourceInfoEvent.TabularResourceInfoEventHandler> {

	public static Type<TabularResourceInfoEventHandler> TYPE = new Type<TabularResourceInfoEventHandler>();
	private TabularResourceData tabularResourceData;

	public interface TabularResourceInfoEventHandler extends EventHandler {
		void onInfoReceived(TabularResourceInfoEvent event);
	}

	public interface HasTabularResourceInfoEventHandler extends HasHandlers {
		public HandlerRegistration addTabularResourceInfoEventHandler(
				TabularResourceInfoEventHandler handler);
	}

	public TabularResourceInfoEvent(TabularResourceData tabularResourceData) {
		this.tabularResourceData = tabularResourceData;
	}

	@Override
	protected void dispatch(TabularResourceInfoEventHandler handler) {
		handler.onInfoReceived(this);
	}

	@Override
	public Type<TabularResourceInfoEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<TabularResourceInfoEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, TabularResourceInfoEvent event) {
		source.fireEvent(event);
	}

	public TabularResourceData getTabularResourceData() {
		return tabularResourceData;
	}

	@Override
	public String toString() {
		return "TabularResourceInfoEvent [tabularResourceData="
				+ tabularResourceData + "]";
	}

}
