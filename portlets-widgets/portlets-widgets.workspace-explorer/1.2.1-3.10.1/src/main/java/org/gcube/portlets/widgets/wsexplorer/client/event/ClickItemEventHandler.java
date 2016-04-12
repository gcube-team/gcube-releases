package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface ClickItemEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public interface ClickItemEventHandler extends EventHandler {
	
	/**
	 * On click.
	 *
	 * @param moreInfoShowEvent the more info show event
	 */
	void onClick(ClickItemEvent moreInfoShowEvent);
}