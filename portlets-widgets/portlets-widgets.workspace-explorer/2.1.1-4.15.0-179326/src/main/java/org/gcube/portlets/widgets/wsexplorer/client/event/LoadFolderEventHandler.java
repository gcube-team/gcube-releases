package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadFolderEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 28, 2016
 * @param <T>
 */
public interface LoadFolderEventHandler extends EventHandler {

	/**
	 * On load folder.
	 * @param <T>
	 *
	 * @param loadFolderEvent the load folder event
	 */
	<T> void onLoadFolder(LoadFolderEvent<T> loadFolderEvent);
}