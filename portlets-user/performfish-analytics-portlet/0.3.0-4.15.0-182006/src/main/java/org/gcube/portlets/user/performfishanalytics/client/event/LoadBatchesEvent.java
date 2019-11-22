package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class LoadPopulationTypeEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class LoadBatchesEvent extends GwtEvent<LoadBatchesEventHandler> {
	public static Type<LoadBatchesEventHandler> TYPE = new Type<LoadBatchesEventHandler>();
	/**
	 * Instantiates a new load batches event.
	 */
	public LoadBatchesEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadBatchesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadBatchesEventHandler handler) {
		handler.onLoadBatches(this);
	}

}
