package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface ShowCreateTaskConfigurationDialogEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 15, 2018
 */
public interface ShowCreateTaskConfigurationDialogEventHandler extends EventHandler {

	/**
	 * On show create configuration.
	 *
	 * @param showCreateTaskConfigurationEvent the show create task configuration event
	 */
	void onShowCreateConfiguration(
		ShowCreateTaskConfigurationDialogEvent showCreateTaskConfigurationEvent);
}