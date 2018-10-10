package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface DeleteCustomFieldEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 8, 2018
 */
public interface DeleteCustomFieldEventHandler extends EventHandler {

  /**
   * On remove entry.
   *
   * @param event the event
   */
  void onRemoveEntry(DeleteCustomFieldEvent event);
}
