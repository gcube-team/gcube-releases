package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.ImportCodeDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Import Code Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ImportCodeEvent extends
		GwtEvent<ImportCodeEvent.ImportCodeEventHandler> {

	public static Type<ImportCodeEventHandler> TYPE = new Type<ImportCodeEventHandler>();
	private ImportCodeDescription importCodeDescription;

	public interface ImportCodeEventHandler extends EventHandler {
		void onImportCode(ImportCodeEvent event);
	}

	public interface HasImportCodeEventHandler extends HasHandlers {
		public HandlerRegistration addImportCodeEventHandler(
				ImportCodeEventHandler handler);
	}

	public ImportCodeEvent(ImportCodeDescription importCodeDescription) {
		this.importCodeDescription = importCodeDescription;
	}

	@Override
	protected void dispatch(ImportCodeEventHandler handler) {
		handler.onImportCode(this);
	}

	@Override
	public Type<ImportCodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ImportCodeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ImportCodeEvent importCodeEvent) {
		source.fireEvent(importCodeEvent);
	}

	public ImportCodeDescription getImportCodeDescription() {
		return importCodeDescription;
	}

	@Override
	public String toString() {
		return "ImportCodeEvent [importCodeDescription="
				+ importCodeDescription + "]";
	}

	
	
	
	
}
