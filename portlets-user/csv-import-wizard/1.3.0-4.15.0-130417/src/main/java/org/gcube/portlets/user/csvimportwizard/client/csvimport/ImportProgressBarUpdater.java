/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.csvimport;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressListener;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.ProgressBar;

/**
 * Updates a {@link ProgressBar} progress and text based on {@link OperationProgressListener} events.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ImportProgressBarUpdater implements OperationProgressListener {
	
	protected ProgressBar progressBar;

	/**
	 * Creates a new {@link ProgressBar} updater.
	 * @param progressBar the {@link ProgressBar} to update.
	 */
	public ImportProgressBarUpdater(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void operationUpdate(long total, long elaborated) {
		Log.trace("total: "+total+" elaborated: "+elaborated);
		if (elaborated == 0) progressBar.updateProgress(0, "initializing...");
		if (elaborated>0 && elaborated<total) {
			double progress = (total>0)?((double)elaborated/(double)total):0;
			Log.trace("progress "+progress);
			progressBar.updateProgress(progress, "importing...");
		}
		if (elaborated == total) progressBar.updateProgress(1, "completing...");
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void operationComplete() {
		progressBar.updateProgress(1, "Import complete.");
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		progressBar.updateText("Import failed: "+reason);
	}

}
