package org.gcube.portlets.user.td.widgetcommonevent.client.event;


import org.gcube.portlets.user.td.widgetcommonevent.client.type.GridHeaderColumnMenuItemType;

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
public class GridHeaderColumnMenuItemEvent extends GwtEvent<GridHeaderColumnMenuItemEvent.GridHeaderColumnMenuItemEventHandler> {

	public static Type<GridHeaderColumnMenuItemEventHandler> TYPE = new Type<GridHeaderColumnMenuItemEventHandler>();
	private GridHeaderColumnMenuItemType itemType;
	private int columnSelected;
	private String operationId;

	public interface GridHeaderColumnMenuItemEventHandler extends EventHandler {	
		void onGridHeaderColumnMenuItemEvent(GridHeaderColumnMenuItemEvent event);
	}
	
	public interface HasGridHeaderColumnMenuItemEventHandler extends HasHandlers{
		public HandlerRegistration addGridHasHeaderColumnMenuItemEventHandler(GridHeaderColumnMenuItemEventHandler handler);
	}
	
	public GridHeaderColumnMenuItemType getItemType() {
		return itemType;
	}

	public void setItemType(GridHeaderColumnMenuItemType itemType) {
		this.itemType = itemType;
	}

	public static void fire(HasHandlers source, GridHeaderColumnMenuItemType itemType,String operationId, int columnSelected) {
		source.fireEvent(new GridHeaderColumnMenuItemEvent(itemType,operationId,columnSelected));
	}

	public GridHeaderColumnMenuItemEvent(GridHeaderColumnMenuItemType itemType, String operationId, int columnSelected) {
		this.itemType = itemType;
		this.columnSelected = columnSelected;
		this.operationId=operationId;
	}

	
	public static Type<GridHeaderColumnMenuItemEventHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GridHeaderColumnMenuItemEventHandler handler) {
		handler.onGridHeaderColumnMenuItemEvent(this);
	}

	@Override
	public Type<GridHeaderColumnMenuItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	public GridHeaderColumnMenuItemType getHeaderColumnMenuItemType() {
		return itemType;
	}

	public int getColumnSelected() {
		return columnSelected;
	}
		
	public String getOperationId() {
		return operationId;
	}

	@Override
	public String toString() {
		return "GridHeaderColumnMenuItemEvent [itemType=" + itemType
				+ ", columnSelected=" + columnSelected + ", operationId="
				+ operationId + "]";
	}
	
	
		
	
}