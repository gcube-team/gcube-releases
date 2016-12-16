package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2015
 *
 */
public interface EditUserPermissionEventHandler extends EventHandler {
	/**
	 * @param editUserPermissionEvent
	 */
	void onEditUserPermission(EditUserPermissionEvent editUserPermissionEvent);
}