package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface AddedBatchIdEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 28, 2019
 */
public interface AddedBatchIdEventHandler extends EventHandler {

	/**
	 * On added batch id.
	 *
	 * @param checkValidBatchIdEvent the check valid batch id event
	 */
	void onAddedBatchId(AddedBatchIdEvent checkValidBatchIdEvent);
}