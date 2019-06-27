package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;

public interface VREChangePermissionsEventHandler extends EventHandler {
	/**
	 * @param vreChangePermissionsEvent
	 */
	void onChangePermissionsOpen(
			VREChangePermissionsEvent vreChangePermissionsEvent);
}