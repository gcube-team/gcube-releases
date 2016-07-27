package org.gcube.portlets.user.statisticalmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import java.lang.String;
import com.google.gwt.event.shared.HasHandlers;

public class ImportCreatedEvent extends
		GwtEvent<ImportCreatedEvent.ImportCreatedHandler> {

	public static Type<ImportCreatedHandler> TYPE = new Type<ImportCreatedHandler>();
	private String importId;

	public interface ImportCreatedHandler extends EventHandler {
		void onImportCreated(ImportCreatedEvent event);
	}

	public ImportCreatedEvent(String importId) {
		this.importId = importId;
	}

	public String getImportId() {
		return importId;
	}

	@Override
	protected void dispatch(ImportCreatedHandler handler) {
		handler.onImportCreated(this);
	}

	@Override
	public Type<ImportCreatedHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ImportCreatedHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, String importId) {
		source.fireEvent(new ImportCreatedEvent(importId));
	}
}
