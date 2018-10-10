package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface ExecuteDataMinerTaskEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 25, 2018
 */
public interface ExecuteDataMinerTaskEventHandler extends EventHandler {

	/**
	 * On execute dm task.
	 *
	 * @param executeDataMinerTaskEvent the execute data miner task event
	 */
	void onExecuteDMTask(ExecuteDataMinerTaskEvent executeDataMinerTaskEvent);
}