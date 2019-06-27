package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ManagePackagesEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface ManagePackagesEventHandler extends EventHandler {

	/**
	 * On manage packages.
	 *
	 * @param managePackagesEvent the manage packages event
	 */
	void onManagePackages(ManagePackagesEvent managePackagesEvent);
	
}