package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface MoveItemsEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 4, 2018
 */
public interface MoveItemsEventHandler extends EventHandler {

	/**
	 * On move items.
	 *
	 * @param pasteItemEvent the paste item event
	 */
	void onMoveItems(MoveItemsEvent pasteItemEvent);
}