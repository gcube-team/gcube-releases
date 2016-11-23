/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.event;

import org.gcube.portlets.user.tdwx.shared.model.TableId;

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
public class OpenTableEvent extends GwtEvent<OpenTableEvent.OpenTableEventHandler> {
	
	public interface HasOpenTableEventHandler extends HasHandlers {
		public HandlerRegistration addOpenTableEventHandler(
				OpenTableEventHandler handler);
	}
	
	public interface OpenTableEventHandler extends EventHandler {
		
		public void onOpenTable(OpenTableEvent event);

	}
	
	public static void fire(HasHandlers source,TableId tableId) {
		source.fireEvent(new OpenTableEvent(tableId));
	}

	public static GwtEvent.Type<OpenTableEventHandler> TYPE = new Type<OpenTableEventHandler>();

	protected TableId tableId;
	
	/**
	 * @param tableId
	 */
	public OpenTableEvent(TableId tableId) {
		this.tableId = tableId;
	}
	
	@Override
	protected void dispatch(OpenTableEventHandler handler) {
		handler.onOpenTable(this);	
	}

	@Override
	public Type<OpenTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the tableId
	 */
	public TableId getTableId() {
		return tableId;
	}

	@Override
	public String toString() {
		return "OpenTableEvent [tableId=" + tableId + "]";
	}

	
}
