/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * Updates a {@link ProgressBar} progress and text based on {@link CodelistMappingImportProgressListener} events.
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FileUploadProgressBarUpdater implements FileUploadProgressListener {
	private static FileUploadProgressMessages msgs = GWT.create(FileUploadProgressMessages.class);
	private ProgressBar progressBar;
	
	/**
	 * Creates a new {@link ProgressBar} updater.
	 * @param progressBar the {@link ProgressBar} to update.
	 */
	public FileUploadProgressBarUpdater(ProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.updateProgress(0, msgs.pleaseWait());
	}
	
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public void operationComplete() {
		Log.info("File upload complete");
		progressBar.updateProgress(1, msgs.fileUploadCompleted());
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		Log.info("File upload failed");
		progressBar.updateText(msgs.fileUploadFailed());
	}

	public void operationInitializing() {
		Log.info("File upload inizializing");
		progressBar.updateProgress(0, msgs.initializing());
	}

	public void operationUpdate(float elaborated) {
		Log.info("File upload elaborated: "+elaborated);
		if (elaborated>=0 && elaborated<1) {
			Log.trace("progress "+elaborated);
			int elab=new Float(elaborated*100).intValue();
			progressBar.updateProgress(elaborated,elab+msgs.percUploading());
		}
		if (elaborated == 1) progressBar.updateProgress(1, msgs.fileUploadCompleted());
		
	}

}
