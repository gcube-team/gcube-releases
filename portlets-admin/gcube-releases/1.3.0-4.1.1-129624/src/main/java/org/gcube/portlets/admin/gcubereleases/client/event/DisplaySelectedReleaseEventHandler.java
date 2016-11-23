package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface DisplaySelectedReleaseEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface DisplaySelectedReleaseEventHandler extends EventHandler {
	
	/**
	 * On select release.
	 *
	 * @param loadSelecteReleaseEvent the load selecte release event
	 */
	void onSelectRelease(DisplaySelectedReleaseEvent loadSelecteReleaseEvent);
}