package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class SyncWithThreddsCatalogueEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 1, 2018
 */
public class SyncWithThreddsCatalogueEvent extends GwtEvent<SyncWithThreddsCatalogueEventHandler> {
	public static Type<SyncWithThreddsCatalogueEventHandler> TYPE = new Type<SyncWithThreddsCatalogueEventHandler>();

	private FileModel folderToPublish;

	/**
	 * Instantiates a new sync with thredds catalogue event.
	 *
	 * @param folderToPublish the folder to publish
	 */
	public SyncWithThreddsCatalogueEvent(FileModel folderToPublish) {
		this.folderToPublish = folderToPublish;
	}



	/**
	 * Gets the folder to publish.
	 *
	 * @return the folderToPublish
	 */
	public FileModel getFolderToPublish() {

		return folderToPublish;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SyncWithThreddsCatalogueEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SyncWithThreddsCatalogueEventHandler handler) {
		handler.onSync(this);
	}

}
