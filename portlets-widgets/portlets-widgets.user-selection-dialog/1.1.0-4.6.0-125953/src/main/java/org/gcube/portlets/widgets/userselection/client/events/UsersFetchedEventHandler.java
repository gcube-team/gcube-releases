package org.gcube.portlets.widgets.userselection.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface UsersFetchedEventHandler extends EventHandler {
  void onUsersFetched(UsersFetchedEvent event);
}
