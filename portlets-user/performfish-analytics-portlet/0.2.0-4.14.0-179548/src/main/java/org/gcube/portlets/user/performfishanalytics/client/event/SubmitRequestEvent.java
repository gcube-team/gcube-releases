package org.gcube.portlets.user.performfishanalytics.client.event;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class SubmitRequestEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 27, 2019
 */
public class SubmitRequestEvent extends GwtEvent<SubmitRequestEventHandler> {
	public static Type<SubmitRequestEventHandler> TYPE = new Type<SubmitRequestEventHandler>();
	private DataMinerAlgorithms chartType;


	/**
	 * Instantiates a new submit request event.
	 *
	 * @param chartType the chart type
	 */
	public SubmitRequestEvent(DataMinerAlgorithms chartType) {
		this.chartType = chartType;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SubmitRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SubmitRequestEventHandler handler) {
		handler.onSubmitRequest(this);
	}


	/**
	 * Gets the chart type.
	 *
	 * @return the chartType
	 */
	public DataMinerAlgorithms getChartType() {

		return chartType;
	}


}
