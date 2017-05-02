package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 18, 2014
 *
 */
public interface UpdatedVREPermissionEventHandler extends EventHandler {

	/**
	 * @param updatedVREPermissionEvent
	 */
	void onUpdateVREPermissions(UpdatedVREPermissionEvent updatedVREPermissionEvent);
}
