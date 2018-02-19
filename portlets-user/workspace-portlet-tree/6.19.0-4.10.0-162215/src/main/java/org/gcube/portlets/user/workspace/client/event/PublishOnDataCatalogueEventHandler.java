package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface PublishOnDataCatalogueEventHandler
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface PublishOnDataCatalogueEventHandler extends EventHandler {

	/**
	 * On insert metadata.
	 *
	 * @param loadSelecteReleaseEvent the load selecte release event
	 */
	void onPublish(PublishOnDataCatalogueEvent loadSelecteReleaseEvent);
}