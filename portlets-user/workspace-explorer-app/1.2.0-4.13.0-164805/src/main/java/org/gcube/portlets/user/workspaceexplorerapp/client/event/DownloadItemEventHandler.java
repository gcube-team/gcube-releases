package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface DownloadItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 23, 2016
 */
public interface DownloadItemEventHandler extends EventHandler {

	/**
	 * On download item.
	 *
	 * @param downloadItemEvent the download item event
	 */
	void onDownloadItem(DownloadItemEvent downloadItemEvent);
}