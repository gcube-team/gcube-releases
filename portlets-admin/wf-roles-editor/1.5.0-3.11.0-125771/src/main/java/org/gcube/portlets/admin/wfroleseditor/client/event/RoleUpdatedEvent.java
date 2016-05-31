package org.gcube.portlets.admin.wfroleseditor.client.event;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;

import com.google.gwt.event.shared.GwtEvent;

public class RoleUpdatedEvent extends GwtEvent<RoleUpdatedEventHandler>{
  public static Type<RoleUpdatedEventHandler> TYPE = new Type<RoleUpdatedEventHandler>();
  private final WfRole updatedRole;
  
  public RoleUpdatedEvent(WfRole updatedRole) {
    this.updatedRole = updatedRole;
  }
  
  public WfRole getUpdatedRole() { return updatedRole; }
  

  @Override
  public Type<RoleUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RoleUpdatedEventHandler handler) {
    handler.onRoleUpdated(this);
  }
}
