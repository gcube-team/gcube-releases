/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.source.local;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressSource;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An {@link OperationProgressSource} for the local upload.
 * The {@link OperationProgress} is retrieved through an RPC call.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class LocalUploadProgressSource implements OperationProgressSource {
	
	protected CSVImportSession session;

	/**
	 * Creates a new {@link OperationProgressSource} for local upload.
	 * @param session the import session.
	 */
	public LocalUploadProgressSource(CSVImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		CSVImportService.Util.getInstance().getLocalUploadStatus(session.getId(), callback);
	}

}
