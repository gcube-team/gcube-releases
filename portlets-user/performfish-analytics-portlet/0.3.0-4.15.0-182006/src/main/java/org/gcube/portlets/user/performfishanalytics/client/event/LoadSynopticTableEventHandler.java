package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadSynopticTableEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 12, 2019
 */
public interface LoadSynopticTableEventHandler extends EventHandler {


	/**
	 * On load synoptic table.
	 *
	 * @param loadSynopticTableEvent the load synoptic table event
	 */
	void onLoadSynopticTable(LoadSynopticTableEvent loadSynopticTableEvent);
}