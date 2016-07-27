package org.gcube.portlets.user.csvimportwizard.ws.client.rpc;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("CSVImportServiceWorkspace")
public interface ImportWizardWSService extends RemoteService {
	
	public void startWorkspaceUpload(String sessionId, String workspaceItemId) throws CSVImportServiceException;
	
	public OperationProgress getWorkspaceUploadStatus(String sessionId) throws CSVImportServiceException;
}
