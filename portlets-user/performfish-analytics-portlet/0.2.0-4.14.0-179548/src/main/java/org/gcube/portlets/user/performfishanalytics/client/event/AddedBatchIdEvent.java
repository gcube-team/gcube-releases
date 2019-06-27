package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class AddedBatchIdEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 28, 2019
 */
public class AddedBatchIdEvent extends GwtEvent<AddedBatchIdEventHandler> {
	public static Type<AddedBatchIdEventHandler> TYPE = new Type<AddedBatchIdEventHandler>();


	/**
	 * Instantiates a new added batch id event.
	 */
	public AddedBatchIdEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<AddedBatchIdEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(AddedBatchIdEventHandler handler) {
		handler.onAddedBatchId(this);
	}
}
