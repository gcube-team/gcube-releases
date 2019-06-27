package org.gcube.portlets.widgets.wsthreddssync.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface PerformDoUnSyncEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public interface PerformDoUnSyncEventHandler extends EventHandler {


	/**
	 * On perform do un sync.
	 *
	 * @param performDoUnSyncEvent the perform do un sync event
	 */
	void onPerformDoUnSync(PerformDoUnSyncEvent performDoUnSyncEvent);
}