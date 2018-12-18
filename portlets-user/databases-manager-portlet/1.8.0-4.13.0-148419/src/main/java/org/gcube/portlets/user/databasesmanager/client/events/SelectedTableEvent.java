package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SelectedTableEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SelectedTableEvent extends GwtEvent<SelectedTableEventHandler> {

	public static Type<SelectedTableEventHandler> TYPE = new Type<SelectedTableEventHandler>();

	private FileModel tableInfo;
	private String selectedTable;
	
	public SelectedTableEvent(FileModel item, String table){
		tableInfo = item;
		selectedTable = table;
	}
	
	@Override
	public Type<SelectedTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedTableEventHandler handler) {
		handler.onSelectedTable(this);
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SELECTED_TABLE_EVENT;
	}
	
	public FileModel getTableInfo(){
		return tableInfo;
	}
	public String getSelectedTable(){
		return selectedTable;
	}
}
