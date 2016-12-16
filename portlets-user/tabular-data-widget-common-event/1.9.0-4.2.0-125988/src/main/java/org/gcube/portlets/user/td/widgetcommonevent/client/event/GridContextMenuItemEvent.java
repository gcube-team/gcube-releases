package org.gcube.portlets.user.td.widgetcommonevent.client.event;


import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.GridOperationId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * Used to manage the events of menu grid
 */
public class GridContextMenuItemEvent extends GwtEvent<GridContextMenuItemEvent.GridContextMenuItemEventHandler> {

	public static Type<GridContextMenuItemEventHandler> TYPE = new Type<GridContextMenuItemEventHandler>();
	private GridOperationId gridOperationId;
	private ArrayList<String> rows;	
    private ArrayList<RowRaw> rowsRaw;	
	private CellData cellData;

	public interface GridContextMenuItemEventHandler extends EventHandler {	
		void onGridContextMenuItemEvent(GridContextMenuItemEvent event);
	}
	
	public interface HasGridContextMenuItemEventHandler extends HasHandlers{
		public HandlerRegistration addGridHasContextMenuItemEventHandler(GridContextMenuItemEventHandler handler);
	}
	
	
	public static void fire(HasHandlers source, GridContextMenuItemEvent gridContextMenuItemEvent) {
		source.fireEvent(gridContextMenuItemEvent);
	}

	public GridContextMenuItemEvent(GridOperationId gridOperationId) {
		this.gridOperationId=gridOperationId;
	}
	
	
	public GridContextMenuItemEvent(GridOperationId gridOperationId,CellData cellData) {
		this.gridOperationId=gridOperationId;
		this.cellData=cellData;
	}
	
	
	public static Type<GridContextMenuItemEventHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GridContextMenuItemEventHandler handler) {
		handler.onGridContextMenuItemEvent(this);
	}

	@Override
	public Type<GridContextMenuItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	public GridOperationId getContextMenuItemType() {
		return gridOperationId;
	}

	public GridOperationId getGridOperationId() {
		return gridOperationId;
	}

	public void setGridOperationId(GridOperationId gridOperationId) {
		this.gridOperationId = gridOperationId;
	}

	public ArrayList<String> getRows() {
		return rows;
	}

	public void setRows(ArrayList<String> rows) {
		this.rows = rows;
	}

	
	public CellData getCellData() {
		return cellData;
	}

	public void setCellData(CellData cellData) {
		this.cellData = cellData;
	}
	
	
	
	public ArrayList<RowRaw> getRowsRaw() {
		return rowsRaw;
	}

	public void setRowsRaw(ArrayList<RowRaw> rowsRaw) {
		this.rowsRaw = rowsRaw;
	}

	@Override
	public String toString() {
		return "GridContextMenuItemEvent [gridOperationId=" + gridOperationId
				+ ", rows=" + rows + ", rowsRaw=" + rowsRaw + ", cellData="
				+ cellData + "]";
	}

	

	
	
	
	
	
}