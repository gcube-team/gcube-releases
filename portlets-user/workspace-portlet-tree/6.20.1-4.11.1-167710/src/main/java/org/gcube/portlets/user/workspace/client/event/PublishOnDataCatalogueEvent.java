package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class PublishOnDataCatalogueEvent
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class PublishOnDataCatalogueEvent extends GwtEvent<PublishOnDataCatalogueEventHandler> {
	public static Type<PublishOnDataCatalogueEventHandler> TYPE = new Type<PublishOnDataCatalogueEventHandler>();
	
	private String folderId;


	/**
	 * Instantiates a new insert metadata event.
	 */
	public PublishOnDataCatalogueEvent(String folderId) {
		this.folderId = folderId;
	}

	/**
	 * @return the folderId
	 */
	public String getFolderId() {
		return folderId;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PublishOnDataCatalogueEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PublishOnDataCatalogueEventHandler handler) {
		handler.onPublish(this);
	}

}
