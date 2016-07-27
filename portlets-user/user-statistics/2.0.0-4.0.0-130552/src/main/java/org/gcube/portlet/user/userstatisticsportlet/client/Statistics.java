package org.gcube.portlet.user.userstatisticsportlet.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public class Statistics implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				// fill container with incoming statistics
				RootPanel.get("statistics-container").add(new StatisticsPanel());
			}				
			@Override
			public void onFailure(Throwable caught) {	
			}
		});
	}
}
