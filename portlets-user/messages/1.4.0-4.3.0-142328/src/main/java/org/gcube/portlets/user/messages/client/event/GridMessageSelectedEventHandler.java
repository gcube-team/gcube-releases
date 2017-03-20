package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface GridMessageSelectedEventHandler extends EventHandler {
	void onGridMessageSelected(GridMessageSelectedEvent gridMessageSelectedEvent);
}
