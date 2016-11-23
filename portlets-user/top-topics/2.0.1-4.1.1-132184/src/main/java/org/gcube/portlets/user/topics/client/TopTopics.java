package org.gcube.portlets.user.topics.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.topics.client.panel.TopicsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class TopTopics implements EntryPoint {

  public void onModuleLoad() {
	  ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				 RootPanel.get("Trending-Topics-Container").add(new TopicsPanel());	
			}				
			@Override
			public void onFailure(Throwable caught) {					
			}
		});
	 		
  }
}
