package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Added resource handler interface
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface AddResourceEventHandler extends EventHandler {
	void onAddedResource(AddResourceEvent addResourceEvent);
}
