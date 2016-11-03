/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.fileimport;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;

import com.google.gwt.user.client.rpc.AsyncCallback;


public class ImportProgressSource implements OperationProgressSource {

	protected ImportSession session;

	/**
	 * @param sessionId
	 */
	public ImportProgressSource(ImportSession session) {
		this.session = session;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getProgress(AsyncCallback<OperationProgress> callback) {
		ImportService.Utility.getInstance().getImportStatus(session.getId(), callback);
	}

}
