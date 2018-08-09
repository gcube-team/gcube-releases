package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc
/**
 * The Interface DeleteWorkspaceItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 16, 2018
 */
public interface DeleteWorkspaceItemEventHandler extends EventHandler {
	

	/**
	 * On remove ws item.
	 *
	 * @param removeWorkspaceItemEvent the remove workspace item event
	 */
	void onRemoveWsItem(DeleteWorkspaceItemEvent removeWorkspaceItemEvent);
}