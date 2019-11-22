package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface PerformFishFieldFormChangedEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 27, 2019
 */
public interface PerformFishFieldFormChangedEventHandler extends EventHandler {

	/**
	 * On field form changed.
	 *
	 * @param performFishFieldFormChangedEvent the perform fish field form changed event
	 */
	void onFieldFormChanged(
		PerformFishFieldFormChangedEvent performFishFieldFormChangedEvent);
}