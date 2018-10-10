/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress;


import com.allen_sauer.gwt.log.client.Log;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class FileUploadProgressCardUpdater implements FileUploadProgressListener {
	
	
	/**
	 * 
	 */
	public FileUploadProgressCardUpdater() {
	}
	

	public void operationComplete() {
		Log.info("File upload completed");	
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails) {
		Log.info("File upload failed");
	}

	public void operationInitializing() {
		Log.info("File upload inizializing");
	}

	public void operationUpdate(float elaborated) {
		Log.info("File uploading: "+elaborated);
	}

}
