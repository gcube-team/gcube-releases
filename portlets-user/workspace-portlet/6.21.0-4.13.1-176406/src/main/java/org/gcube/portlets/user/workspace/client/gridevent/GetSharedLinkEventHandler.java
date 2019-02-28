package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface GetSharedLinkEventHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 5, 2018
 */
public interface GetSharedLinkEventHandler extends EventHandler {

	/**
	 * On get link.
	 *
	 * @param getLinkEvent the get link event
	 */
	void onGetLink(GetShareLinkEvent getLinkEvent);
}
