package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GridElementUnSelectedEvent extends GwtEvent<GridElementUnSelectedEventHandler> {
  public static Type<GridElementUnSelectedEventHandler> TYPE = new Type<GridElementUnSelectedEventHandler>();
  
  @Override
  public Type<GridElementUnSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GridElementUnSelectedEventHandler handler) {
    handler.onGridElementUnSelected(this);
  }
}