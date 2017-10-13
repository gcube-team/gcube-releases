/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface DataLoadHandler extends EventHandler {
	
	/**
	 * Called when {@link DataLoadEvent} is fired.
	 * @param event the {@link DataLoadEvent} that was fired
	 */
	void onDataLoad(DataLoadEvent event);

}
