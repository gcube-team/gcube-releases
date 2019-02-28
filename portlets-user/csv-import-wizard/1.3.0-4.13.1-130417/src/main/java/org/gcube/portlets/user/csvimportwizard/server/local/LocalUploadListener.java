/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.local;

import org.apache.commons.fileupload.ProgressListener;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
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
