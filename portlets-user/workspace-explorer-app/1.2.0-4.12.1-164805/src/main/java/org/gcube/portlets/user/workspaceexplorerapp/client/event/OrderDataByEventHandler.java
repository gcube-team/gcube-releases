/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Class OrderDataByEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 18, 2016
 */
public interface OrderDataByEventHandler extends EventHandler {

	/**
	 * On load root.
	 *
	 * @param orderDataByEvent the label
	 */
	void onOrderDataBy(OrderDataByEvent orderDataByEvent);
}
