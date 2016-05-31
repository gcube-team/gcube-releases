package org.gcube.portlets.user.questions.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Questions implements EntryPoint {
  
  
  public void onModuleLoad() {
	  RootPanel.get("questionsManagersDiv").add(new VREManagersPanel());
  }
}
