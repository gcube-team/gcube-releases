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
public interface HasDataLoadHandlers extends HasHandlers {

	/**
	 * Adds a {@link DataLoadEvent} handler.
	 * 
	 * @param handler the handler
	 * @return the registration for the event
	 */
	public HandlerRegistration addDataLoadHandler(DataLoadHandler handler);

}
