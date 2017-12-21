/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface ItemSelectionHandler extends EventHandler {
	
	/**
	 * Called when {@link ItemSelectionEvent} is fired.
	 * @param event the {@link ItemSelectionEvent} that was fired
	 */
	void onSelection(ItemSelectionEvent event);

}
