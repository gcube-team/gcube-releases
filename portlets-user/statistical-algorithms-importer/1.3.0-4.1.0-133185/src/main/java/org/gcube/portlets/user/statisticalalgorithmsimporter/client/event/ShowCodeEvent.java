package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.ImportCodeDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Show Code Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ShowCodeEvent extends
		GwtEvent<ShowCodeEvent.ShowCodeEventHandler> {

	public static Type<ShowCodeEventHandler> TYPE = new Type<ShowCodeEventHandler>();
	private ImportCodeDescription importCodeDescription;

	public interface ShowCodeEventHandler extends EventHandler {
		void onShowCode(ShowCodeEvent event);
	}

	public interface HasShowCodeEventHandler extends HasHandlers {
		public HandlerRegistration addShowCodeEventHandler(
				ShowCodeEventHandler handler);
	}

	public ShowCodeEvent(ImportCodeDescription importCodeDescription) {
		this.importCodeDescription = importCodeDescription;
	}

	@Override
	protected void dispatch(ShowCodeEventHandler handler) {
		handler.onShowCode(this);
	}

	@Override
	public Type<ShowCodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ShowCodeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ShowCodeEvent importCodeEvent) {
		source.fireEvent(importCodeEvent);
	}

	public ImportCodeDescription getImportCodeDescription() {
		return importCodeDescription;
	}

	@Override
	public String toString() {
		return "ShowCodeEvent [importCodeDescription=" + importCodeDescription
				+ "]";
	}

	
	

	
	
	
	
}
