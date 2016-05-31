package org.gcube.portlets.admin.wfroleseditor.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditRoleCancelledEvent extends GwtEvent<EditRoleCancelledEventHandler>{
  public static Type<EditRoleCancelledEventHandler> TYPE = new Type<EditRoleCancelledEventHandler>();
  
  @Override
  public Type<EditRoleCancelledEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRoleCancelledEventHandler handler) {
    handler.onEditRoleCancelled(this);
  }
}
