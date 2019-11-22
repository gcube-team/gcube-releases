package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface PerformRunTaskEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public interface PerformRunTaskEventHandler extends EventHandler {

	/**
	 * On perform run task.
	 *
	 * @param perforRunTaskEvent the perfor run task event
	 */
	void onPerformRunTask(PerformRunTaskEvent perforRunTaskEvent);
}