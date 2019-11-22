package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 30, 2014
 *
 */
public interface UpdateWorkspaceSizeEventHandler extends EventHandler {

	void onUpdateWorkspaceSize(UpdateWorkspaceSizeEvent updateWorkspaceSizeEvent);
}