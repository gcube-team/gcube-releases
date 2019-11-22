package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface SubmitRequestEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public interface SubmitRequestEventHandler extends EventHandler {


	/**
	 * On submit request.
	 *
	 * @param submitRequestEvent the submit request event
	 */
	void onSubmitRequest(SubmitRequestEvent submitRequestEvent);
}