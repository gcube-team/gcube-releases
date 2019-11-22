package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface CheckItemLockedBySyncEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 28, 2018
 */
public interface CheckItemLockedBySyncEventHandler extends EventHandler {

	/**
	 * On check item locked by sync.
	 *
	 * @param checkItemLockedBySyncEvent the check item locked by sync event
	 */
	void onCheckItemLockedBySync(CheckItemLockedBySyncEvent checkItemLockedBySyncEvent);
}