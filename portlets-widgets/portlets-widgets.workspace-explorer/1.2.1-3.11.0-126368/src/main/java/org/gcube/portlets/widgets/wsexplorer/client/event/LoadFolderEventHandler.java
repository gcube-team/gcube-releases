package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 23, 2013
 *
 */
public interface LoadFolderEventHandler extends EventHandler {
	/**
	 * @param accountingHistoryEvent
	 */
	void onLoadFolder(LoadFolderEvent loadFolderEvent);
}