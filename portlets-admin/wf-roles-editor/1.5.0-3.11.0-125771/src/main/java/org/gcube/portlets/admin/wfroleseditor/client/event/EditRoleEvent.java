package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditRoleEvent extends GwtEvent<EditRoleEventHandler>{
  public static Type<EditRoleEventHandler> TYPE = new Type<EditRoleEventHandler>();
  private final String id;
  
  public EditRoleEvent(String id) {
    this.id = id;
  }
  
  public String getId() { return id; }
  
  @Override
  public Type<EditRoleEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRoleEventHandler handler) {
    handler.onEditRole(this);
  }
}
