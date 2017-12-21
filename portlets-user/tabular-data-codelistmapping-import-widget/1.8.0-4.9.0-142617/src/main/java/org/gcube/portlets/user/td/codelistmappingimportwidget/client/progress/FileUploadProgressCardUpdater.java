/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;


import com.allen_sauer.gwt.log.client.Log;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
