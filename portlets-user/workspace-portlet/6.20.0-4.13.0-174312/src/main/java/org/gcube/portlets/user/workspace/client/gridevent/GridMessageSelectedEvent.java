package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.MessageModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GridMessageSelectedEvent extends GwtEvent<GridMessageSelectedEventHandler> {
  public static Type<GridMessageSelectedEventHandler> TYPE = new Type<GridMessageSelectedEventHandler>();

  private MessageModel targetMessage = null;
  
	public GridMessageSelectedEvent(ModelData target) {
	this.targetMessage = (MessageModel) target;
	}

	@Override
	public Type<GridMessageSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GridMessageSelectedEventHandler handler) {
		handler.onGridMessageSelected(this);	
	}

	public MessageModel getTargetMessage() {
		return targetMessage;
	}
}