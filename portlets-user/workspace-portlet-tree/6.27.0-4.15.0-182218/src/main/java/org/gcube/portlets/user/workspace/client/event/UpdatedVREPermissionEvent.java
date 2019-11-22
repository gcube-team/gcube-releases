package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class UpdatedVREPermissionEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 17, 2019
 */
public class UpdatedVREPermissionEvent extends GwtEvent<UpdatedVREPermissionEventHandler> implements GuiEventInterface {
	public static Type<UpdatedVREPermissionEventHandler> TYPE = new Type<UpdatedVREPermissionEventHandler>();

	private FileModel vreFolder = null;

	/**
	 * Instantiates a new updated VRE permission event.
	 *
	 * @param folder the folder
	 */
	public UpdatedVREPermissionEvent(FileModel folder) {
		this.vreFolder = folder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<UpdatedVREPermissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(UpdatedVREPermissionEventHandler handler) {
		handler.onUpdateVREPermissions(this);

	}
	
	/**
	 * Gets the vre folder.
	 *
	 * @return the vre folder
	 */
	public FileModel getVreFolder() {
		return vreFolder;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.UPDATED_VRE_PERMISSION;
	}
}