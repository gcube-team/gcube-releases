package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.MessageModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GridMessageUnSelectedEvent extends GwtEvent<GridMessageUnSelectedEventHandler> {
  public static Type<GridMessageUnSelectedEventHandler> TYPE = new Type<GridMessageUnSelectedEventHandler>();

  private MessageModel targetMessage = null;
  
	public GridMessageUnSelectedEvent(ModelData target) {
	this.targetMessage = (MessageModel) target;
	}

	@Override
	public Type<GridMessageUnSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GridMessageUnSelectedEventHandler handler) {
		handler.onGridMessageUnSelected(this);
		
	}

	public MessageModel getTargetMessage() {
		return targetMessage;
	}
}