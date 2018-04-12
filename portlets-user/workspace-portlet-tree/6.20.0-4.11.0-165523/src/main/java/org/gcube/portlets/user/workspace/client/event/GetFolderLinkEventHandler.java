package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface GetFolderLinkEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 13, 2016
 */
public interface GetFolderLinkEventHandler extends EventHandler {

	/**
	 * On get folder link.
	 *
	 * @param getFolderLinkEvent the get folder link event
	 */
	void onGetFolderLink(GetFolderLinkEvent getFolderLinkEvent);
}
