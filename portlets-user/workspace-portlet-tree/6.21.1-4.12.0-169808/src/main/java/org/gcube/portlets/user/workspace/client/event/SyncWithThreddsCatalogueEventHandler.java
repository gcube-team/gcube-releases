package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

// TODO: Auto-generated Javadoc
/**
 * The Interface SyncWithThreddsCatalogueEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 1, 2018
 */
public interface SyncWithThreddsCatalogueEventHandler extends EventHandler {

	/**
	 * On sync.
	 *
	 * @param synWithThreddsEvent the syn with thredds event
	 */
	void onSync(SyncWithThreddsCatalogueEvent synWithThreddsEvent);
}