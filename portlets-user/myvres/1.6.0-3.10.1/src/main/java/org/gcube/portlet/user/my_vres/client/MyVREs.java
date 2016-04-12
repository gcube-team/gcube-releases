package org.gcube.portlet.user.my_vres.client;

import org.gcube.portlet.user.my_vres.client.resources.TourResources;
import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.GuidedTourResourceProvider;
import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MyVREs implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("myVREsDIV").add(new VresPanel());
		showGuidedTour();
	}
	
	private void showGuidedTour() {
		GWT.runAsync(GCUBEGuidedTour.class, new RunAsyncCallback() {
			public void onSuccess() {

				GCUBEGuidedTour.showTour(MyVREs.class.getName(), new GuidedTourResourceProvider() {
					@Override
					public GuidedTourResource getResource() {
						TourResources resources = GWT.create(TourResources.class);
						return resources.quickTour();
					}
				});
			}
			public void onFailure(Throwable caught) {
				Window.alert("Could not check show tour");
			}
		});
	}
}
