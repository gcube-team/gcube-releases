package org.gcube.portlets.widgets.userselection.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SelectedUserEventHandler extends EventHandler {
  void onSelectedUser(SelectedUserEvent event);
}
