package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class PublishOnThreddsCatalogueEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 27, 2017
 */
public class PublishOnThreddsCatalogueEvent extends GwtEvent<PublishOnThreddsCatalogueEventHandler> {
	public static Type<PublishOnThreddsCatalogueEventHandler> TYPE = new Type<PublishOnThreddsCatalogueEventHandler>();

	private FileModel folderToPublish;

	/**
	 * Instantiates a new publish on thredds catalogue event.
	 *
	 * @param folderToPublish the folder to publish
	 */
	public PublishOnThreddsCatalogueEvent(FileModel folderToPublish) {
		this.folderToPublish = folderToPublish;
	}



	/**
	 * @return the folderToPublish
	 */
	public FileModel getFolderToPublish() {

		return folderToPublish;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PublishOnThreddsCatalogueEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PublishOnThreddsCatalogueEventHandler handler) {
		handler.onPublish(this);
	}

}
