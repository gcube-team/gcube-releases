/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.event;

import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnsReorderingEvent extends
		GwtEvent<ColumnsReorderingEvent.ColumnsReorderingEventHandler> {

	
	public interface ColumnsReorderingEventHandler extends EventHandler {

		public void onColumnsReordering(ColumnsReorderingEvent event);

	}

	public interface HasColumnsReorderingEventHandler extends HasHandlers {
		public HandlerRegistration addColumnsReorderingEventHandler(
				ColumnsReorderingEventHandler handler);
	}

	public static GwtEvent.Type<ColumnsReorderingEventHandler> TYPE = new Type<ColumnsReorderingEventHandler>();

	@Override
	public Type<ColumnsReorderingEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ColumnsReorderingEventHandler handler) {
		handler.onColumnsReordering(this);
	}

	public static void fire(HasHandlers source,
			ColumnsReorderingConfig columnsReorderingConfig) {
		source.fireEvent(new ColumnsReorderingEvent(columnsReorderingConfig));
	}

	protected ColumnsReorderingConfig columnsReorderingConfig;

	/**
	 * @param tableId
	 */
	public ColumnsReorderingEvent(
			ColumnsReorderingConfig columnsReorderingConfig) {
		this.columnsReorderingConfig = columnsReorderingConfig;
	}

	public ColumnsReorderingConfig getColumnsReorderingConfig() {
		return columnsReorderingConfig;
	}

	public void setColumnsReorderingConfig(
			ColumnsReorderingConfig columnsReorderingConfig) {
		this.columnsReorderingConfig = columnsReorderingConfig;
	}

	@Override
	public String toString() {
		return "ColumnsReorderingEvent [columnsReorderingConfig="
				+ columnsReorderingConfig + "]";
	}

}
