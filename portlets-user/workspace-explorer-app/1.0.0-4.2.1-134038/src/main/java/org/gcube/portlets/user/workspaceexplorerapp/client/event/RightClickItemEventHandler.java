package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface RightClickItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 23, 2016
 */
public interface RightClickItemEventHandler extends EventHandler {


	/**
	 * On click.
	 *
	 * @param rightClickItemEvent the right click item event
	 */
	void onClick(RightClickItemEvent rightClickItemEvent);
}