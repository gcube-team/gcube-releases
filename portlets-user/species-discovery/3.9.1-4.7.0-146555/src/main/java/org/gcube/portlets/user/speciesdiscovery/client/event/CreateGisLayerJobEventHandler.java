/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface CreateGisLayerJobEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public interface CreateGisLayerJobEventHandler extends EventHandler {

	/**
	 * On create gis layer job.
	 *
	 * @param createGisLayerJobEvent the create gis layer job event
	 */
	public void onCreateGisLayerJob(
		CreateGisLayerJobEvent createGisLayerJobEvent);

}
