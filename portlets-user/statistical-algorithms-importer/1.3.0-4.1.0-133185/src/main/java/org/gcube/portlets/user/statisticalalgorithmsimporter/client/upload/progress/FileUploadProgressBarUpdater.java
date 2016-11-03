/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress;


import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * Updates a {@link ProgressBar} progress and text based on {@link FileUploadProgressListener} events.
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FileUploadProgressBarUpdater implements FileUploadProgressListener {
	
	protected ProgressBar progressBar;
	
	/**
	 * Creates a new {@link ProgressBar} updater.
	 * @param progressBar the {@link ProgressBar} to update.
	 */
	public FileUploadProgressBarUpdater(ProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.updateProgress(0, "Please Wait...");
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void operationComplete() {
		Log.info("File upload complete");
		progressBar.updateProgress(1, "File upload completed.");
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		Log.info("File upload failed");
		progressBar.updateText("File upload failed.");
	}

	public void operationInitializing() {
		Log.info("File upload inizializing");
		progressBar.updateProgress(0, "Initializing...");
	}

	public void operationUpdate(float elaborated) {
		Log.info("File upload elaborated: "+elaborated);
		if (elaborated>=0 && elaborated<1) {
			Log.trace("progress "+elaborated);
			int elab=new Float(elaborated*100).intValue();
			progressBar.updateProgress(elaborated,elab+"% Uploading...");
		}
		if (elaborated == 1) progressBar.updateProgress(1, "Completing...");
		
	}

}
