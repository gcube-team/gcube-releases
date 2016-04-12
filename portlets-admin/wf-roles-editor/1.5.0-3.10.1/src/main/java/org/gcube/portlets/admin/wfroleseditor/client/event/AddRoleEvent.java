package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddRoleEvent extends GwtEvent<AddRoleEventHandler> {
  public static Type<AddRoleEventHandler> TYPE = new Type<AddRoleEventHandler>();
  
  @Override
  public Type<AddRoleEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddRoleEventHandler handler) {
    handler.onAddRole(this);
  }
}
