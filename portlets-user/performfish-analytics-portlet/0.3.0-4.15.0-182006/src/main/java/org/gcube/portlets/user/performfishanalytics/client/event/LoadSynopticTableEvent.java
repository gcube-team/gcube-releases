package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class LoadSynopticTableEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 12, 2019
 */
public class LoadSynopticTableEvent extends GwtEvent<LoadSynopticTableEventHandler> {
	public static Type<LoadSynopticTableEventHandler> TYPE = new Type<LoadSynopticTableEventHandler>();
	private boolean annualAnalysis;
	
	/**
	 * Instantiates a new load batches event.
	 */
	public LoadSynopticTableEvent(boolean annualAnalysis) {
		this.annualAnalysis = annualAnalysis;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadSynopticTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadSynopticTableEventHandler handler) {
		handler.onLoadSynopticTable(this);
	}
	
	public boolean isAnnualAnalysis() {
		return annualAnalysis;
	}

}
