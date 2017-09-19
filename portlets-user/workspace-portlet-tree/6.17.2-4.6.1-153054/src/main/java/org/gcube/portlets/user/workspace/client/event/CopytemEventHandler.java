package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CopytemEventHandler extends EventHandler {
	/**
	 * @param copytemEvent
	 */
	void onCopyItem(CopytemEvent copytemEvent);
}