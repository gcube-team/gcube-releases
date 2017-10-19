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
public interface HasNameChangeHandlers extends HasHandlers {

	/**
	 * Adds a {@link NameChangeEvent} handler.
	 * 
	 * @param handler the handler
	 * @return the registration for the event
	 */
	public HandlerRegistration addNameChangeHandler(NameChangeHandler handler);

}
