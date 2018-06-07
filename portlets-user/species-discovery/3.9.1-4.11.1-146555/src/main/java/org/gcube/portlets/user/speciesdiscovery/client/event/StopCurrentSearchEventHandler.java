/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface AbortCurrentSearchEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2017
 */
public interface StopCurrentSearchEventHandler extends EventHandler {

	/**
	 * On abort current search.
	 *
	 * @param abortCurrentSearchEvent the abort current search event
	 */
	public void onAbortCurrentSearch(
		StopCurrentSearchEvent abortCurrentSearchEvent);

}
