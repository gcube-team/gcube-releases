package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class InsertMetadataEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class EditMetadataEvent extends GwtEvent<EditMetadataEventHandler> {
	public static Type<EditMetadataEventHandler> TYPE = new Type<EditMetadataEventHandler>();


	/**
	 * Instantiates a new insert metadata event.
	 */
	public EditMetadataEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<EditMetadataEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(EditMetadataEventHandler handler) {
		handler.onEditMetadata(this);
	}

}
