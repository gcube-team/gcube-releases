package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface CreatedTaskConfigurationEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 15, 2018
 */
public interface CreatedTaskConfigurationEventHandler extends EventHandler {


	/**
	 * On created configuration.
	 *
	 * @param createTaskConfigurationEvent the create task configuration event
	 */
	void onCreatedConfiguration(
		CreatedTaskConfigurationEvent createTaskConfigurationEvent);
}