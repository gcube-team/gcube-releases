/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.fileimport;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressListener;

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
	@Override
	public void operationUpdate(long total, long elaborated) {
		if (elaborated == 0) progressBar.updateProgress(0, "initializing...");
		if (elaborated>0 && elaborated<total) {
			double progress = (total>0)?((double)elaborated/(double)total):0;
			progressBar.updateProgress(progress, "importing...");
		}
		if (elaborated == total) progressBar.updateProgress(1, "completing...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operationComplete() {
		progressBar.updateProgress(1, "Import complete.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		progressBar.updateText("Import failed: "+reason);
	}

}
