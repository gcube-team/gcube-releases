package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class RoleDeletedEvent extends GwtEvent<RoleDeletedEventHandler>{
  public static Type<RoleDeletedEventHandler> TYPE = new Type<RoleDeletedEventHandler>();
  
  @Override
  public Type<RoleDeletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RoleDeletedEventHandler handler) {
    handler.onRoleDeleted(this);
  }
}
