package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface LoadMySpecialFolderEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public interface LoadMySpecialFolderEventHandler extends EventHandler {
	
	/**
	 * On load my special folder.
	 *
	 * @param loadMySpecialFolderEvent the load my special folder event
	 */
	void onLoadMySpecialFolder(LoadMySpecialFolderEvent loadMySpecialFolderEvent);
}