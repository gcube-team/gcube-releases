/**
 * 
 */
package org.gcube.portlets.user.gcubegisviewer.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface HasSaveHandlers extends HasHandlers {

	/**
	 * Adds a {@link SaveEvent} handler.
	 * 
	 * @param handler the handler
	 * @return the registration for the event
	 */
	public HandlerRegistration addSaveHandler(SaveHandler handler);

}
