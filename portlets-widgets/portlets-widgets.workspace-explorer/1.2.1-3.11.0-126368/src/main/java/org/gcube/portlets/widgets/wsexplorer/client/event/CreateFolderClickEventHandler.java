package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface ClickItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public interface CreateFolderClickEventHandler extends EventHandler {

	/**
	 * On click.
	 *
	 * @param createFolderClickEvent the more info show event
	 */
	void onClick(CreateFolderClickEvent createFolderClickEvent);
}