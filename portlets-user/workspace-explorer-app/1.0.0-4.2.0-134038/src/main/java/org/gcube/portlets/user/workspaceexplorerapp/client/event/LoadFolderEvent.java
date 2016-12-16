package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class LoadFolderEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class LoadFolderEvent extends GwtEvent<LoadFolderEventHandler> {
	public static Type<LoadFolderEventHandler> TYPE = new Type<LoadFolderEventHandler>();
	private Item item;

	/**
	 * Instantiates a new load folder event.
	 *
	 * @param selected the selected
	 */
	public LoadFolderEvent(Item selected) {
		this.item = selected;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadFolderEventHandler handler) {
		handler.onLoadFolder(this);
	}

	/**
	 * Gets the target folder.
	 *
	 * @return the target folder
	 */
	public Item getTargetFolder() {
		return item;
	}

	/**
	 * Sets the target folder.
	 *
	 * @param item the new target folder
	 */
	public void setTargetFolder(Item item) {
		this.item = item;
	}
}
