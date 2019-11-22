package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public interface GetSharedLinkEventHandler extends EventHandler {
	
	void onGetLink(GetShareLinkEvent getLinkEvent);
}
