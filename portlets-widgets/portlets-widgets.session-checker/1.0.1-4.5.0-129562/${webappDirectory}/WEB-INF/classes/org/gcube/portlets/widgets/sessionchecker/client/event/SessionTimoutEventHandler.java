package org.gcube.portlets.widgets.sessionchecker.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SessionTimoutEventHandler extends EventHandler {
  void onSessionExpiration(SessionTimeoutEvent event);
}
