package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 14, 2014
 *
 */
public class UpdatedVREPermissionEvent extends GwtEvent<UpdatedVREPermissionEventHandler> implements GuiEventInterface {
	public static Type<UpdatedVREPermissionEventHandler> TYPE = new Type<UpdatedVREPermissionEventHandler>();

	private String vreFolderId = null;

	public UpdatedVREPermissionEvent(String folderId) {
		this.vreFolderId = folderId;
	}

	@Override
	public Type<UpdatedVREPermissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdatedVREPermissionEventHandler handler) {
		handler.onUpdateVREPermissions(this);

	}

	public String getVreFolderId() {
		return vreFolderId;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.UPDATED_VRE_PERMISSION;
	}
}