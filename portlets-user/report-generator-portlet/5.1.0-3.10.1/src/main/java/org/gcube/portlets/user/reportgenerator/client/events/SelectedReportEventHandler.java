package org.gcube.portlets.user.reportgenerator.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SelectedReportEventHandler extends EventHandler {
	 void onReportSelected(SelectedReportEvent event);
}
