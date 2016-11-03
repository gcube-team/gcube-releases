package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface NotifyLogoutEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public interface NotifyLogoutEventHandler extends EventHandler {


	/**
	 * On logout.
	 *
	 * @param editMetadataEvent the edit metadata event
	 */
	void onLogout(NotifyLogoutEvent editMetadataEvent);

}