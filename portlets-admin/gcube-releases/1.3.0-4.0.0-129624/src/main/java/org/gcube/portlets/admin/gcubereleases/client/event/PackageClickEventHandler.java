package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ReloadReleasesEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface PackageClickEventHandler extends EventHandler {
	
	/**
	 * On click event.
	 *
	 * @param packageClickEvent the package click event
	 */
	void onClickEvent(PackageClickEvent packageClickEvent);
}