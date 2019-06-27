package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface CallAlgorithmEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Mar 5, 2019
 */
public interface CallAlgorithmEventHandler extends EventHandler {


	/**
	 * On call.
	 *
	 * @param callAlgorithmEvent the call algorithm event
	 */
	void onCall(CallAlgorithmEvent callAlgorithmEvent);
}