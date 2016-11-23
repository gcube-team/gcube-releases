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
	 * @param <T> the generic type
	 * @param moreInfoShowEvent the more info show event
	 */
	<T> void onClick(ClickItemEvent<T> moreInfoShowEvent);
}