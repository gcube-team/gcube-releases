package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface CopyItemsEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 10, 2018
 */
public interface CopyItemsEventHandler extends EventHandler {


	/**
	 * On copy items.
	 *
	 * @param copytemEvent the copytem event
	 */
	void onCopyItems(CopyItemsEvent copytemEvent);
}