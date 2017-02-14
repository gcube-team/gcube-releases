package org.gcube.portlets.user.statisticalmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import java.lang.String;
import com.google.gwt.event.shared.HasHandlers;

public class TablesGridGotDirtyEvent extends
		GwtEvent<TablesGridGotDirtyEvent.TablesGridGotDirtyHandler> {

	public static Type<TablesGridGotDirtyHandler> TYPE = new Type<TablesGridGotDirtyHandler>();
	private String tableId;

	public interface TablesGridGotDirtyHandler extends EventHandler {
		void onTablesGridGotDirty(TablesGridGotDirtyEvent event);
	}

	public TablesGridGotDirtyEvent(String tableId) {
		this.tableId = tableId;
	}

	public String getTableId() {
		return tableId;
	}

	@Override
	protected void dispatch(TablesGridGotDirtyHandler handler) {
		handler.onTablesGridGotDirty(this);
	}

	@Override
	public Type<TablesGridGotDirtyHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<TablesGridGotDirtyHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, String tableId) {
		source.fireEvent(new TablesGridGotDirtyEvent(tableId));
	}
}
