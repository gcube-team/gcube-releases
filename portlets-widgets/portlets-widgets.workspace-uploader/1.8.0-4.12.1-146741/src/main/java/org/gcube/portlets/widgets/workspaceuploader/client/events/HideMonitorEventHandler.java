package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface HideMonitorEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 14, 2015
 */
public interface HideMonitorEventHandler extends EventHandler {

	/**
	 * On hide monitor.
	 *
	 * @param hideMonitorEvent the hide monitor event
	 */
	void onHideMonitor(HideMonitorEvent hideMonitorEvent);
}