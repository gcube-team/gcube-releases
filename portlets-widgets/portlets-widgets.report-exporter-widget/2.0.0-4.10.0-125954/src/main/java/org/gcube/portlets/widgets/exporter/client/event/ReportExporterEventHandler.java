package org.gcube.portlets.widgets.exporter.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ReportExporterEventHandler extends EventHandler {
	void onCompletedExport(ReportExporterEvent event);

}
