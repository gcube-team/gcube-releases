package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ReloadReleasesEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface ReloadReleasesEventHandler extends EventHandler {
	
	/**
	 * On relead releases.
	 *
	 * @param reloadReleasesEvent the reload releases event
	 */
	void onReleadReleases(ReloadReleasesEvent reloadReleasesEvent);
}