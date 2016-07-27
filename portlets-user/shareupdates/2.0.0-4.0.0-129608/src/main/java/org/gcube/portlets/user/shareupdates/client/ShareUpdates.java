package org.gcube.portlets.user.shareupdates.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Massimiliano Assante at ISTI CNR
 * @author Costantino Perciante at ISTI CNR
 */
public class ShareUpdates implements EntryPoint {

	public void onModuleLoad() {
		// start UI and related stuff
		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				RootPanel.get("shareUpdateDiv").add(new ShareUpdateForm());
			}				
			@Override
			public void onFailure(Throwable caught) {					
			}
		});
	}
}
