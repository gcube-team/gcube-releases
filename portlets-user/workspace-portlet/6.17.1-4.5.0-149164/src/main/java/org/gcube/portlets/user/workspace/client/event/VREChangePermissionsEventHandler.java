package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface VREChangePermissionsEventHandler extends EventHandler {
	/**
	 * @param vreChangePermissionsEvent
	 */
	void onChangePermissionsOpen(
			VREChangePermissionsEvent vreChangePermissionsEvent);
}