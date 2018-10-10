package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadRootEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public interface LoadRootEventHandler extends EventHandler {

	/**
	 * On load root.
	 *
	 * @param loadRootEvent the load root event
	 */
	void onLoadRoot(LoadRootEvent loadRootEvent);
}