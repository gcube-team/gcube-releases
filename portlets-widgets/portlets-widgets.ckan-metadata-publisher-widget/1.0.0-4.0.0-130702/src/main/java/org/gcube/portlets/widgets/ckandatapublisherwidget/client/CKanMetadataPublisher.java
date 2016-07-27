package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.CreateDatasetForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CKanMetadataPublisher implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// remove comment to the below line for testing the widget
		//startExample();

	}

	@SuppressWarnings("unused")
	private void startExample() {
		
		String idFolderWorkspace = "d3a37eb9-1589-4c95-a9d0-c473a02d4f0f";
		String owner = "costantino.perciante";
		HandlerManager eventBus = new HandlerManager(null);
		RootPanel.get("ckan-metadata-publisher-div").add(new CreateDatasetForm(idFolderWorkspace ,owner, eventBus ));

	}
}
