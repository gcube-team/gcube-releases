package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface PublishOnThreddsCatalogueEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 27, 2017
 */
public interface PublishOnThreddsCatalogueEventHandler extends EventHandler {


	/**
	 * On publish.
	 *
	 * @param loadSelecteReleaseEvent the load selecte release event
	 */
	void onPublish(PublishOnThreddsCatalogueEvent loadSelecteReleaseEvent);
}