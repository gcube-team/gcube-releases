package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface FileVersioningEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2017
 */
public interface VersioningHistoryShowEventHandler extends EventHandler {

	/**
	 * On file versioning.
	 *
	 * @param fileVersioningEvent the file versioning event
	 */
	void onFileVersioning(VersioningHistoryShowEvent fileVersioningEvent);
}