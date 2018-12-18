package org.gcube.portlets.user.notifications.client;

import org.gcube.portlets.user.notifications.client.view.NotificationsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class Notifications implements EntryPoint {
 
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  RootPanel.get("notificationsDIV").add(new NotificationsPanel());
  }
}
