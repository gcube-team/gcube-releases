package org.gcube.portlets.user.csvimportwizard.ws.client.rpc;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ImportWizardWSServiceAsync {

	void startWorkspaceUpload(String sessionId, String workspaceItemId, AsyncCallback<Void> callback);

	void getWorkspaceUploadStatus(String sessionId, AsyncCallback<OperationProgress> callback);
}
