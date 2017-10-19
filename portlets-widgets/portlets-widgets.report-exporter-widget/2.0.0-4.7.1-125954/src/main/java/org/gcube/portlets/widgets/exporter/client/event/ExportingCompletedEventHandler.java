package org.gcube.portlets.widgets.exporter.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ExportingCompletedEventHandler extends EventHandler {
	void onExportFinished(ExportingCompletedEvent event);

}
