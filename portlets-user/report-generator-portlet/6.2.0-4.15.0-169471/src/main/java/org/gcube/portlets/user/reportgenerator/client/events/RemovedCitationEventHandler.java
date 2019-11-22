package org.gcube.portlets.user.reportgenerator.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RemovedCitationEventHandler extends EventHandler {
	void onRemovedCitation(RemovedCitationEvent event);
}
