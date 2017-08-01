package org.gcube.portlets.widgets.pickitem.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface PickedItemEventHandler extends EventHandler {
  void onSelectedItem(PickedItemEvent event);
}
