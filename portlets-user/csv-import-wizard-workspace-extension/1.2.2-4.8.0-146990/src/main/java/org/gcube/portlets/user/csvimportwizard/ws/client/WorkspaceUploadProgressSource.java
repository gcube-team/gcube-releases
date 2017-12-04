/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.ws.client;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressSource;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An {@link OperationProgressSource} for the local upload.
 * The {@link OperationProgress} is retrieved through an RPC call.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class WorkspaceUploadProgressSource implements OperationProgressSource {
	
	protected CSVImportSession session;

	/**
	 * Creates a new {@link OperationProgressSource} for local upload.
	 * @param session the import session.
	 */
	public WorkspaceUploadProgressSource(CSVImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		ImportWizardWorkspace.SERVICE.getWorkspaceUploadStatus(session.getId(), callback);
	}

}
