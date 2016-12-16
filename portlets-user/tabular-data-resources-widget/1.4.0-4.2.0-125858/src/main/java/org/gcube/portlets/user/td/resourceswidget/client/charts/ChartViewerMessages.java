package org.gcube.portlets.user.td.resourceswidget.client.charts;

import org.gcube.portlets.user.td.resourceswidget.client.store.ZoomLevelType;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ChartViewerMessages extends Messages {

	@DefaultMessage("Chart")
	String dialogHead();

	@DefaultMessage("This resource does not have valid internal URI!")
	String errorInvalidInternalURI();

	@DefaultMessage("Error retrieving uri from resolver: ")
	String errorRetrievingUriFromResolverFixed();

	@DefaultMessage("Zoom In")
	String btnZoomInToolTip();

	@DefaultMessage("Zoom Out")
	String btnZoomOutToolTip();

	@DefaultMessage("Move")
	String btnMoveToolTip();

	@DefaultMessage("Zoom Level")
	String comboZoomLevelEmptyText();

	@DefaultMessage("Open in new window")
	String btnOpenInWindowToolTip();

	@DefaultMessage("")
	@AlternateMessage({ "Fit", "Fit", "P50", "50%", "P75", "75%", "P100",
			"100%", "P200", "200%", "MaxZoom", "Max" })
	String zoomLevelType(@Select ZoomLevelType zoomLevelType);

}
