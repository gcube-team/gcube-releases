package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface InsertMetadataEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public interface EditMetadataEventHandler extends EventHandler {

	/**
	 * @param editMetadataEvent
	 */
	void onEditMetadata(EditMetadataEvent editMetadataEvent);

}