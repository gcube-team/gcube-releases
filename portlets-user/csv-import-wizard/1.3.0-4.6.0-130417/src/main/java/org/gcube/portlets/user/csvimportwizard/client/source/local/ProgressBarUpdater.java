/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.source.local;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressListener;
import org.gcube.portlets.user.csvimportwizard.client.util.Format;

import com.extjs.gxt.ui.client.widget.ProgressBar;

/**
 * Updates a {@link ProgressBar} progress and text based on {@link OperationProgressListener} events.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ProgressBarUpdater implements OperationProgressListener {
	
	protected ProgressBar progressBar;

	/**
	 * Creates a new {@link ProgressBar} updater.
	 * @param progressBar the {@link ProgressBar} to update.
	 */
	public ProgressBarUpdater(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationUpdate(long total, long elaborated) {
		StringBuilder label = new StringBuilder();
		label.append(Format.fileSize(elaborated));
		label.append(" out of ");
		label.append(Format.fileSize(total));
		label.append(" uploaded");
		
		double progress = (total>0)?((double)elaborated/(double)total):0;
		progressBar.updateProgress(progress, label.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationComplete() {
		progressBar.updateProgress(1, "Upload complete.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		progressBar.updateText("Upload failed: "+reason);
	}

}
