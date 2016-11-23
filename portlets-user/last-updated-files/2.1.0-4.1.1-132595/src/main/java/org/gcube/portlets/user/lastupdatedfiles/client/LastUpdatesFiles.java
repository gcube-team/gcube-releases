package org.gcube.portlets.user.lastupdatedfiles.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.lastupdatedfiles.client.panel.RecentDocumentsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author massi
 *
 */
public class LastUpdatesFiles implements EntryPoint {

	private static final String LUF_DIV = "LastUpdatesFiles-container";
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				RootPanel.get(LUF_DIV).add(new RecentDocumentsPanel());
			}				
			@Override
			public void onFailure(Throwable caught) {					
			}
		});
	}
}
