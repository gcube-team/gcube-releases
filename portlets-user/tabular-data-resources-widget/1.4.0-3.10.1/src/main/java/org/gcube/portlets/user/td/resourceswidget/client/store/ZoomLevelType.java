package org.gcube.portlets.user.td.resourceswidget.client.store;

import org.gcube.portlets.user.td.resourceswidget.client.charts.ChartViewerMessages;

import com.google.gwt.core.client.GWT;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum ZoomLevelType {
	Fit("Fit"),
	P50("50%"),
	P75("75%"),
	P100("100%"),
	P200("200%"),
	MaxZoom("Max");
	
	private static ChartViewerMessages msgs=GWT.create(ChartViewerMessages.class);
	private final String id;

	
	/**
	 * @param text
	 */
	private ZoomLevelType(final String id) {
		this.id = id;
	}

	
	public String getIdI18N(){
		return msgs.zoomLevelType(this);
	}
		
	@Override
	public String toString() {
		return id;
	}

}