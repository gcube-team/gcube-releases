/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.event;

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
public class GridReadyEvent extends GwtEvent<GridReadyEvent.GridReadyEventHandler> {
	
	public interface GridReadyEventHandler extends EventHandler {
		
		public void onGridReady(GridReadyEvent event);

	}

	public interface HasGridReadyEventHandler extends HasHandlers {
		public HandlerRegistration addGridReadyEventHandler(
				GridReadyEventHandler handler);
	}
	
	public static GwtEvent.Type<GridReadyEventHandler> TYPE = new Type<GridReadyEventHandler>();

	@Override
	public Type<GridReadyEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GridReadyEventHandler handler) {
		handler.onGridReady(this);	
	}

	public static void fire(HasHandlers source) {
		source.fireEvent(new GridReadyEvent());
	}
	
	
	/**
	 * @param tableDefinition
	 */
	public GridReadyEvent() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableReadyEvent ");
		return builder.toString();
	}
}
