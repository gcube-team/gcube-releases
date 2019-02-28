package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * On share link button press event
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface ShareLinkEventHandler extends EventHandler  {
	void onShareLink(ShareLinkEvent event);
}
