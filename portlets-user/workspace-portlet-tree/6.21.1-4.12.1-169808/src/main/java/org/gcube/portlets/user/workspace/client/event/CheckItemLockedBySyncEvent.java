package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


public class CheckItemLockedBySyncEvent extends GwtEvent<CheckItemLockedBySyncEventHandler>{
	public static Type<CheckItemLockedBySyncEventHandler> TYPE = new Type<CheckItemLockedBySyncEventHandler>();

	private FileModel item;

	public CheckItemLockedBySyncEvent(FileModel item) {
		this.item = item;
	}

	@Override
	public Type<CheckItemLockedBySyncEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CheckItemLockedBySyncEventHandler handler) {
		handler.onCheckItemLockedBySync(this);

	}

	/**
	 * @return the itemId
	 */
	public FileModel getItem() {

		return item;
	}

}
