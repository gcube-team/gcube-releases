package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadFocusEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 7, 2019
 */
public interface LoadFocusEventHandler extends EventHandler {


	/**
	 * On load focus event.
	 *
	 * @param loadFocusEvent the load focus event
	 */
	void onLoadFocusEvent(LoadFocusEvent loadFocusEvent);
}