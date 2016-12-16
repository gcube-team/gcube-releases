package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class InsertMetadataEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class InsertMetadataEvent extends GwtEvent<InsertMetadataEventHandler> {
	public static Type<InsertMetadataEventHandler> TYPE = new Type<InsertMetadataEventHandler>();


	/**
	 * Instantiates a new insert metadata event.
	 */
	public InsertMetadataEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<InsertMetadataEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(InsertMetadataEventHandler handler) {
		handler.onInsertMetadata(this);
	}

}
