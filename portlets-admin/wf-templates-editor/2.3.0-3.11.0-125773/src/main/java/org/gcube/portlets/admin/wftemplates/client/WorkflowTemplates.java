package org.gcube.portlets.admin.wftemplates.client;


import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.GuidedTourResourceProvider;
import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * <code> WorkflowTemplates </code> class is the entrypoint component of this webapp
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version Feb 2012 (1.0) 
 */
public class WorkflowTemplates implements EntryPoint {
	public static final String PORTLET_DIV = "WfTemplates";
	
	interface Resources extends ClientBundle {
		  @Source("org/gcube/portlets/admin/wftemplates/client/resources/WelcomeTour.xml")
		  GuidedTourResource quickTour();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		WfTemplatesServiceAsync rpcService = GWT.create(WfTemplatesService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(rpcService, eventBus);
		
		//starts the application
		appViewer.go(RootPanel.get(PORTLET_DIV));
		
		GCUBEGuidedTour.showTour(WorkflowTemplates.class.getName(), new GuidedTourResourceProvider() {
			 
			@Override
			public GuidedTourResource getResource() {
				Resources resources = GWT.create(Resources.class);
				return resources.quickTour();
			}
		});
	}
}
