package org.gcube.portlets.user.reportgenerator.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RemovedUserCommentEventHandler  extends EventHandler {
	 void onRemovedComment(RemovedUserCommentEvent event);
}
