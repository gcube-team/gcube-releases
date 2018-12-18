
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface DeleteConfigurationEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         May 9, 2018
 */
public interface DeleteConfigurationEventHandler extends EventHandler {

	/**
	 * On remove configuration.
	 *
	 * @param deleteConfigurationEvent
	 *            the delete configuration event
	 */
	void onRemoveConfiguration(DeleteConfigurationEvent deleteConfigurationEvent);
}
