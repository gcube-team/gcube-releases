package org.gcube.portlets.admin.wfdocviewer.client;



import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 *
 */
public class WorkflowDocumentsViewer implements EntryPoint {
	/**
	 * 
	 */
	public static final String CONTAINER_DIV = "wfdocviewerDIV";
	interface Resources extends ClientBundle {
		  @Source("org/gcube/portlets/admin/wfdocviewer/client/resources/WelcomeTour.xml")
		  GuidedTourResource quickTour();
	}

	/**
	 * This is the entry point method.
	 */	
	public void onModuleLoad() {
		
		WorkflowDocServiceAsync rpcService = GWT.create(WorkflowDocService.class);
	    HandlerManager eventBus = new HandlerManager(null);
	    AppController appViewer = new AppController(rpcService, eventBus);
	    appViewer.go(RootPanel.get(CONTAINER_DIV));
	    
//	    GWT.runAsync(new RunAsyncCallback() {
//			@Override
//			public void onSuccess() {
//				GCUBEGuidedTour.showTour(WorkflowDocumentsViewer.class.getName(), new GuidedTourResourceProvider() {
//					@Override
//					public GuidedTourResource getResource() {
//						Resources resources = GWT.create(Resources.class);
//						return resources.quickTour();
//					}
//				});
//			}
//			
//			@Override
//			public void onFailure(Throwable reason) {
//				
//			}
//		});
		
	}
}
