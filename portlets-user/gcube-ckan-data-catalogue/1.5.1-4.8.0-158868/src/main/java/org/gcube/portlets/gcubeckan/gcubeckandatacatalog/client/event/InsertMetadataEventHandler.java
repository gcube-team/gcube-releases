package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface InsertMetadataEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public interface InsertMetadataEventHandler extends EventHandler {

	/**
	 * On insert metadata.
	 *
	 * @param loadSelecteReleaseEvent the load selecte release event
	 */
	void onInsertMetadata(InsertMetadataEvent loadSelecteReleaseEvent);
}