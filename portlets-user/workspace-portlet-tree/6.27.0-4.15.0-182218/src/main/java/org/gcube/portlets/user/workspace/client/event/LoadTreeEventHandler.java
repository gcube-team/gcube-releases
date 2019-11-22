package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface LoadTreeEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 31, 2019
 */
public interface LoadTreeEventHandler extends EventHandler {

	/**
	 * Do load tree.
	 *
	 * @param loadTreeEvent the load tree event
	 */
	void doLoadTree(LoadTreeEvent loadTreeEvent);
}