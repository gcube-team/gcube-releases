package org.gcube.portlets.admin.software_upload_wizard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SoftwareUploadWizard implements EntryPoint {
	
	public void onModuleLoad() {
		String scope = "/gcube/devsec";
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(eventBus,scope);
		appViewer.go();
	}
}
