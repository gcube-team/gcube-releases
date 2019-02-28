package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler associated to the DeleteCustomFieldEvent
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface DeleteCustomFieldEventHandler extends EventHandler {
  void onRemoveEntry(DeleteCustomFieldEvent event);
}
