package org.gcube.portlets.user.rstudio_wrapper_portlet.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RStudioWrapper implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final RStudioServiceAsync rstudioService = GWT.create(RStudioService.class);
	private final String CONTAINER_DIV = "RStudio-wrapper-DIV";
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final JavaScriptObject window = newWindow("", "", "");
		rstudioService.retrieveRStudioSecureURL(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get(CONTAINER_DIV).add(new HTML("There were problems contacting the server, please report this issue: " + caught.getMessage()));

			}

			@Override
			public void onSuccess(String result) {
				 RootPanel.get(CONTAINER_DIV).add(new HTML("If no new window appears, please click here to <a href=\""+result+"\" target=\"_blank\">open RStudio</a>"));
				 setWindowTarget(window, result);
			}
		});



	}
	private static native JavaScriptObject newWindow(String url, String name, String features)/*-{
    var window = $wnd.open(url, name, features);
    return window;
}-*/;

	private static native void setWindowTarget(JavaScriptObject window, String target)/*-{
    window.location = target;
}-*/;
	
}
