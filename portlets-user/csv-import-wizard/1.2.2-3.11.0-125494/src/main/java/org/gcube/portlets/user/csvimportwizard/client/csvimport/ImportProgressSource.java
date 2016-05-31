/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.csvimport;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressSource;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ImportProgressSource implements OperationProgressSource {

	protected CSVImportSession session;

	/**
	 * @param sessionId
	 */
	public ImportProgressSource(CSVImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		CSVImportService.Util.getInstance().getImportStatus(session.getId(), callback);
	}

}
