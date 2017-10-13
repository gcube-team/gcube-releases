/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface HasPopupHandlers extends HasHandlers {

	/**
	 * Adds a {@link PopupEvent} handler.
	 * 
	 * @param handler the handler
	 * @return the registration for the event
	 */
	public HandlerRegistration addPopupHandler(PopupHandler handler);

}
