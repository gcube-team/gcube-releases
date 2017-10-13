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
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CloseTableEvent extends
		GwtEvent<CloseTableEvent.CloseTableEventHandler> {

	public interface CloseTableEventHandler extends EventHandler {

		public void onCloseTable(CloseTableEvent event);

	}

	public interface HasCloseTableEventHandler extends HasHandlers {
		public HandlerRegistration addCloseTableEventHandler(
				CloseTableEventHandler handler);
	}
	
	public static GwtEvent.Type<CloseTableEventHandler> TYPE = new Type<CloseTableEventHandler>();

	
	@Override
	public Type<CloseTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CloseTableEventHandler handler) {
		handler.onCloseTable(this);
	}

	public static void fire(HasHandlers source) {
		source.fireEvent(new CloseTableEvent());
	}

	@Override
	public String toString() {
		return "CloseTableEvent []";
	}
	
	
	

}
