package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddTemplateEventHandler  extends EventHandler {
  void onAddTemplates(AddTemplateEvent templateAddEvent);
}