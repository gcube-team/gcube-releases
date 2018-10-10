package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface TaskComputationFinishedEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 7, 2018
 */
public interface TaskComputationFinishedEventHandler extends EventHandler {

	/**
	 * On task finished.
	 *
	 * @param taskComputationTerminatedEvent the task computation terminated event
	 */
	void onTaskFinished(
		TaskComputationFinishedEvent taskComputationTerminatedEvent);
}