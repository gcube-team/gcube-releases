package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ReloadReleasesEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface ShowClickReportEventHandler extends EventHandler {
	

	/**
	 * On show click report.
	 *
	 * @param showClickReportEvent the show click report event
	 */
	void onShowClickReport(ShowClickReportEvent showClickReportEvent);
}