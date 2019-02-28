package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadFolderEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 9, 2018
 */
public interface LoadFolderEventHandler extends EventHandler {


	/**
	 * On load folder.
	 *
	 * @param loadFolderEvent the load folder event
	 */
	void onLoadFolder(LoadFolderEvent loadFolderEvent);
}