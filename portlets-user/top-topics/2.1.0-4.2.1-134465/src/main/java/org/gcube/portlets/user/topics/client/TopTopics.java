package org.gcube.portlets.user.topics.client;

import org.gcube.portlets.user.topics.client.panel.TopicsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class TopTopics implements EntryPoint {

  public void onModuleLoad() {
	  RootPanel.get("Trending-Topics-Container").add(new TopicsPanel());	
	 		
  }
}
