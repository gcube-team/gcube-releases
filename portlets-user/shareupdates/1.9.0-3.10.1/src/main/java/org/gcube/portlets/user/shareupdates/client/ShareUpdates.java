package org.gcube.portlets.user.shareupdates.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.shared.GWT;
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

		// check if jQuery is available
		boolean jQueryLoaded = isjQueryLoaded();

		if(jQueryLoaded)
			GWT.log("Injecting : http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js");
		else{
			ScriptInjector.fromUrl("http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js")
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();
		}

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

	/**
	 * Checks if jQuery is loaded.
	 *
	 * @return true, if jQuery is loaded, false otherwise
	 */
	private native boolean isjQueryLoaded() /*-{

		return (typeof $wnd['jQuery'] !== 'undefined');

	}-*/;
}
