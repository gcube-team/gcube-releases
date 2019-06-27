package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface GetFolderLinkEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 13, 2016
 */
public interface WsGetFolderLinkEventHandler extends EventHandler {

	/**
	 * On get folder link.
	 *
	 * @param getFolderLinkEvent the get folder link event
	 */
	void onGetFolderLink(WsGetFolderLinkEvent getFolderLinkEvent);
}
