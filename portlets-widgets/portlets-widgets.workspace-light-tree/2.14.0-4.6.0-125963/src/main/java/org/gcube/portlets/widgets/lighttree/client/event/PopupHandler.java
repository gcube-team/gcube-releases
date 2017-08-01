/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface PopupHandler extends EventHandler {
	
	/**
	 * Called when {@link PopupEvent} is fired.
	 * @param event the {@link DataLoadEvent} that was fired
	 */
	void onPopup(PopupEvent event);

}
