package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 30, 2014
 *
 */
public class UpdateWorkspaceSizeEvent extends GwtEvent<UpdateWorkspaceSizeEventHandler> implements GuiEventInterface{
	public static Type<UpdateWorkspaceSizeEventHandler> TYPE = new Type<UpdateWorkspaceSizeEventHandler>();


	public UpdateWorkspaceSizeEvent() {
		
	}

	@Override
	public Type<UpdateWorkspaceSizeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateWorkspaceSizeEventHandler handler) {
		handler.onUpdateWorkspaceSize(this);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.UPDATE_WORKSPACE_SIZE;
	}

}
