package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface ShowListOfTaskConfigurationsEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 16, 2018
 */
public interface ShowListOfTaskConfigurationsEventHandler extends EventHandler {


	/**
	 * On show list of task configurations.
	 *
	 * @param showListOfTaskConfigurationsEvent the show list of task configurations event
	 */
	void onShowListOfTaskConfigurations(
		ShowListOfTaskConfigurationsEvent showListOfTaskConfigurationsEvent);
}