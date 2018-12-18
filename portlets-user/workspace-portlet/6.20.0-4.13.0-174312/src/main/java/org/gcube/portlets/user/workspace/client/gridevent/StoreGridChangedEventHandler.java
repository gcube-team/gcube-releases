package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface StoreGridChangedEventHandler extends EventHandler {

	/**
	 * @param storeGridChangedEvent
	 */
	void onStoreChanged(StoreGridChangedEvent storeGridChangedEvent);
}
