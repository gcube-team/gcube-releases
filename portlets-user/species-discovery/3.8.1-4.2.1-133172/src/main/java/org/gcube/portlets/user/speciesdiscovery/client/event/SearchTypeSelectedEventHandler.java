/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 17, 2013
 *
 */
public interface SearchTypeSelectedEventHandler extends EventHandler {

	/**
	 * @param searchTypeSelectedEvent
	 */
	public void onSearchTypeSelected(SearchTypeSelectedEvent searchTypeSelectedEvent);

}
