package org.gcube.portlets.user.gcubeloggedin.client;

import org.gcube.portlets.user.gcubeloggedin.client.ui.AboutView;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GCubeLoggedin implements EntryPoint {
	private final LoggedinServiceAsync loggedinService = GWT.create(LoggedinService.class);
	
	private VerticalPanel main_panel = new VerticalPanel();
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		main_panel.setWidth("100%");
		main_panel.add(AboutView.getLoadingHTML());
		// Associate the new panel with the HTML host page.
		RootPanel.get("LoggedinDiv").add(main_panel);
		
		loggedinService.getSelectedRE(Location.getHref(), new AsyncCallback<VObject>() {
			public void onFailure(Throwable caught) {
				main_panel.add(new HTML("<div style=\"height: 450px; text-align:center; vertical-align:text-top;\">"
						+ "<p>Sorry there was a problem on the server, please reload this page</p></div>" ));

			}

			public void onSuccess(VObject result) {
			
				main_panel.clear();
				main_panel.add(new AboutView(result, loggedinService));
			}			
		});
	}	
	
}
