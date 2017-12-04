/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface HasItemSelectionHandlers extends HasHandlers {

	/**
	 * Adds a {@link SelectionEvent} handler.
	 * 
	 * @param handler the handler
	 * @return the registration for the event
	 */
	public HandlerRegistration addSelectionHandler(ItemSelectionHandler handler);

}
