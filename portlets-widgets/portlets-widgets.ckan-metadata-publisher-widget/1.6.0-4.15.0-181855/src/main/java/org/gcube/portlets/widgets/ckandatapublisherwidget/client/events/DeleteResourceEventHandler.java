package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * The delete event handler
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface DeleteResourceEventHandler extends EventHandler{
	void onDeletedResource(DeleteResourceEvent deleteResourceEvent);
}
