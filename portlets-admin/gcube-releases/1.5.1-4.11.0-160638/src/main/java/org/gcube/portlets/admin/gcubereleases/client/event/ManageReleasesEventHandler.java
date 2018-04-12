package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ManageReleasesEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface ManageReleasesEventHandler extends EventHandler {

	/**
	 * On manage releases.
	 *
	 * @param manageReleasesEvent the manage releases event
	 */
	void onManageReleases(ManageReleasesEvent manageReleasesEvent);
	
}