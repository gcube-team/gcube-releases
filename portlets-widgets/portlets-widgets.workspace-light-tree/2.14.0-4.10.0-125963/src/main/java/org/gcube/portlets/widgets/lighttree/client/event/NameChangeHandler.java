/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface NameChangeHandler extends EventHandler {
	
	/**
	 * Called when {@link NameChangeEvent} is fired.
	 * @param event the {@link NameChangeEvent} that was fired
	 */
	void onNameChange(NameChangeEvent event);

}
