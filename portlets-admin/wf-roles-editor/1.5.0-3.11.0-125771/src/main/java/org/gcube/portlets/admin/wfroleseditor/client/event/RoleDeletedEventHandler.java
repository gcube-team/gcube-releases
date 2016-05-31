package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RoleDeletedEventHandler extends EventHandler {
  void onRoleDeleted(RoleDeletedEvent event);
}
