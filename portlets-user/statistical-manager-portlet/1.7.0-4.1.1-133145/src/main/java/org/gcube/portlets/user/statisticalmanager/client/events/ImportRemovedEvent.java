package org.gcube.portlets.user.statisticalmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea.ImportStatusPanel;
import com.google.gwt.event.shared.HasHandlers;

public class ImportRemovedEvent extends
		GwtEvent<ImportRemovedEvent.ImportRemovedHandler> {

	public static Type<ImportRemovedHandler> TYPE = new Type<ImportRemovedHandler>();
	private ImportStatusPanel importStatusPanel;

	public interface ImportRemovedHandler extends EventHandler {
		void onImportRemoved(ImportRemovedEvent event);
	}

	public ImportRemovedEvent(ImportStatusPanel importStatusPanel) {
		this.importStatusPanel = importStatusPanel;
	}

	public ImportStatusPanel getImportStatusPanel() {
		return importStatusPanel;
	}

	@Override
	protected void dispatch(ImportRemovedHandler handler) {
		handler.onImportRemoved(this);
	}

	@Override
	public Type<ImportRemovedHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ImportRemovedHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ImportStatusPanel importStatusPanel) {
		source.fireEvent(new ImportRemovedEvent(importStatusPanel));
	}
}
