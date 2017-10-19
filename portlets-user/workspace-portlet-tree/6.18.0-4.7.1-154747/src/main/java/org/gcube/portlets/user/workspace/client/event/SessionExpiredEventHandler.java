package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 4, 2013
 *
 */
public interface SessionExpiredEventHandler extends EventHandler {

	/**
	 * @param sessionExpiredEvent
	 */
	void onSessionExpired(SessionExpiredEvent sessionExpiredEvent);
}