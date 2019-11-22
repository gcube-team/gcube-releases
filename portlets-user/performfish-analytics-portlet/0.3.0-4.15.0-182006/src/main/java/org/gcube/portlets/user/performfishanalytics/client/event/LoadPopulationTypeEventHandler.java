package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadPopulationTypeEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 16, 2019
 */
public interface LoadPopulationTypeEventHandler extends EventHandler {


	/**
	 * On load population type.
	 *
	 * @param loadPopulationEvent the load population event
	 */
	void onLoadPopulationType(LoadPopulationTypeEvent loadPopulationEvent);
}