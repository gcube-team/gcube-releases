package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface GridRefreshEventHandler extends EventHandler {
	/**
	 * @param gridRefreshEvent
	 */
	void onGridRefresh(GridRefreshEvent gridRefreshEvent);
}
