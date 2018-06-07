/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.event;

import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableReadyEvent extends GwtEvent<TableReadyEvent.TableReadyEventHandler> {
	
	public interface HasTableReadyEventHandler extends HasHandlers {
		public HandlerRegistration addTableReadyEventHandler(
				TableReadyEventHandler handler);
	}
	
	public interface TableReadyEventHandler extends EventHandler {
		
		public void onTableReady(TableReadyEvent event);

	}
	
	
	public static void fire(HasHandlers source,TableDefinition tableDefinition) {
		source.fireEvent(new TableReadyEvent(tableDefinition));
	}

	public static GwtEvent.Type<TableReadyEventHandler> TYPE = new Type<TableReadyEventHandler>();

	protected TableDefinition tableDefinition;
	
	
	/**
	 * @param tableDefinition
	 */
	public TableReadyEvent(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
	}
	
	
	@Override
	protected void dispatch(TableReadyEventHandler handler) {
		handler.onTableReady(this);	
	}

	@Override
	public Type<TableReadyEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the tableDefinition
	 */
	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	@Override
	public String toString() {
		return "TableReadyEvent [tableDefinition=" + tableDefinition + "]";
	}

	
}
