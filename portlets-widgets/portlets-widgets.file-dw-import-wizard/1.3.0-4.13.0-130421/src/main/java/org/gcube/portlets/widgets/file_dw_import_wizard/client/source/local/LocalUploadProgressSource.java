/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An {@link OperationProgressSource} for the local upload.
 * The {@link OperationProgress} is retrieved through an RPC call.
 */
public class LocalUploadProgressSource implements OperationProgressSource {
	Logger logger= Logger.getLogger("");
	protected ImportSession session;

	/**
	 * Creates a new {@link OperationProgressSource} for local upload.
	 * @param session the import session.
	 */
	public LocalUploadProgressSource(ImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		logger.log(Level.SEVERE, "Inside LocalUploadProgressSources");
		ImportService.Utility.getInstance().getLocalUploadStatus(session.getId(), callback);
	}

}
