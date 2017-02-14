/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressListener;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.util.Format;

import com.extjs.gxt.ui.client.widget.ProgressBar;




/**
 * Updates a {@link ProgressBar} progress and text based on {@link OperationProgressListener} events.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ProgressBarUpdater implements OperationProgressListener {
	
	protected ProgressBar progressBar;
	 Logger logger = Logger.getLogger("");
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
	@Override
	public void operationUpdate(long total, long elaborated) {
		logger.log(Level.SEVERE, "ProgressBarUpdater operationUpdate");
		logger.log(Level.SEVERE, "total "+ total+" elaborated "+ elaborated);
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
	@Override
	public void operationComplete() {
		progressBar.updateProgress(1, "Upload complete.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		progressBar.updateText("Upload failed: "+reason);
	}

}
