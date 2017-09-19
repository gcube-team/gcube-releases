/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.workspace;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An {@link OperationProgressSource} for the local upload.
 * The {@link OperationProgress} is retrieved through an RPC call.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class WorkspaceUploadProgressSource implements OperationProgressSource {
	
	protected ImportSession session;

	/**
	 * Creates a new {@link OperationProgressSource} for local upload.
	 * @param session the import session.
	 */
	public WorkspaceUploadProgressSource(ImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		ImportService.Utility.getInstance().getWorkspaceUploadStatus(session.getId(), callback);
	}

}
