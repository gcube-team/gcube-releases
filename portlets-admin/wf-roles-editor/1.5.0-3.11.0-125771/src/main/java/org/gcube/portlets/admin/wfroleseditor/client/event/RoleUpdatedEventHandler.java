package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RoleUpdatedEventHandler extends EventHandler{
  void onRoleUpdated(RoleUpdatedEvent event);
}
