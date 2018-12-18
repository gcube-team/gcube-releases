package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 6, 2013
 *
 */
public interface SearchItemByIdEventHandler extends EventHandler {
	/**
	 * @param searchItemByIdEvent
	 */
	void onSearchItemById(SearchItemByIdEvent searchItemByIdEvent);
}
