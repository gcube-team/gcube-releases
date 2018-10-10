package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.EventHandler;


// TODO: Auto-generated Javadoc
/**
 * The Interface SelectedWorkspaceItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2018
 */
public interface SelectedWorkspaceItemEventHandler extends EventHandler {
	
	/**
	 * On create folder.
	 *
	 * @param selectedWorkspaceItemEvent the create folder event
	 */
	void onSelectedItem(SelectedWorkspaceItemEvent selectedWorkspaceItemEvent);
}