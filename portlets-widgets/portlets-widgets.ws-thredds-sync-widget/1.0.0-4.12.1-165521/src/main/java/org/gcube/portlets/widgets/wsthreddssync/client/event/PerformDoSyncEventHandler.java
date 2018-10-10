package org.gcube.portlets.widgets.wsthreddssync.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc
/**
 * The Interface PerformDoSyncEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public interface PerformDoSyncEventHandler extends EventHandler {
	

	/**
	 * On change status.
	 *
	 * @param courseChangeStatusEvent the course change status event
	 */
	void onPerformDoSync(PerformDoSyncEvent courseChangeStatusEvent);
}