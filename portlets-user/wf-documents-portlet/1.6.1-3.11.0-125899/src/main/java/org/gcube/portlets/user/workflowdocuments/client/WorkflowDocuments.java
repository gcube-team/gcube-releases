package org.gcube.portlets.user.workflowdocuments.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WorkflowDocuments implements EntryPoint {
	/**
	 * 
	 */
	public static final String CONTAINER_DIV = "workflowDocumentsDIV";
	public static final String GET_OID_PARAMETER = "oid";
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		WfDocumentsLibraryServiceAsync rpcService = GWT.create(WfDocumentsLibraryService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(rpcService, eventBus);
		appViewer.go(RootPanel.get(CONTAINER_DIV));
	}
}
