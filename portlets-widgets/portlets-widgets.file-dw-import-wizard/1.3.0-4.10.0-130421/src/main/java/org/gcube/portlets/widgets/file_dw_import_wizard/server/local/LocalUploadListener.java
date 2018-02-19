/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server.local;

import org.apache.commons.fileupload.ProgressListener;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;


public class LocalUploadListener implements ProgressListener {

	protected OperationProgress operationProgress;

	public LocalUploadListener(OperationProgress operationProgress) {
		this.operationProgress = operationProgress;
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(long pBytesRead, long pContentLength, int pItems) {
		operationProgress.setTotalLenght(pContentLength);
		operationProgress.setElaboratedLenght(pBytesRead);
	}

}
