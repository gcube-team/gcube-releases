package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface NewInsertReleaseEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface NewInsertReleaseEventHandler extends EventHandler {
	
	/**
	 * On new insert release.
	 *
	 * @param manageReleasesEvent the manage releases event
	 */
	void onNewInsertRelease(NewInsertReleasesEvent manageReleasesEvent);
}