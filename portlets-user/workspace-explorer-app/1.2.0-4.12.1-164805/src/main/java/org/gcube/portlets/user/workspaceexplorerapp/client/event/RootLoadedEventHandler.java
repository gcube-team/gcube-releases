package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface RootLoadedEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public interface RootLoadedEventHandler extends EventHandler {



	/**
	 * On root loaded.
	 *
	 * @param rootLoadedEvent the root loaded event
	 */
	void onRootLoaded(RootLoadedEvent rootLoadedEvent);
}