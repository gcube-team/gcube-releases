package org.gcube.portlets.user.statisticalmanager.client.events;

import org.gcube.portlets.user.statisticalmanager.client.bean.ImportStatus;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class ImportTerminatedEvent extends
		GwtEvent<ImportTerminatedEvent.ImportTerminatedHandler> {

	public static Type<ImportTerminatedHandler> TYPE = new Type<ImportTerminatedHandler>();
	private ImportStatus importStatus;

	public interface ImportTerminatedHandler extends EventHandler {
		void onImportTerminated(ImportTerminatedEvent event);
	}

	public ImportTerminatedEvent(ImportStatus importStatus) {
		this.importStatus = importStatus;
	}

	public ImportStatus getImportStatus() {
		return importStatus;
	}

	@Override
	protected void dispatch(ImportTerminatedHandler handler) {
		handler.onImportTerminated(this);
	}

	@Override
	public Type<ImportTerminatedHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ImportTerminatedHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ImportStatus importStatus) {
		source.fireEvent(new ImportTerminatedEvent(importStatus));
	}
}
