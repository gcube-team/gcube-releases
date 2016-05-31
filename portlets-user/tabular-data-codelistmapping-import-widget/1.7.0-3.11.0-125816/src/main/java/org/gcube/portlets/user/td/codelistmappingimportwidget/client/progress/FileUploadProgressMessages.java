package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface FileUploadProgressMessages extends Messages {

	//
	@DefaultMessage("Please Wait...")
	String pleaseWait();
	
	@DefaultMessage("File upload completed.")
	String fileUploadCompleted();
	
	@DefaultMessage("File Upload Failed")
	String fileUploadFailedHead();

	@DefaultMessage("File upload failed.")
	String fileUploadFailed();
	
	@DefaultMessage("Initializing...")
	String initializing();
	
	@DefaultMessage("% Uploading...")
	String percUploading();
	
	@DefaultMessage("Completing...")
	String completing();
	
	@DefaultMessage("Failed getting operation updates")
	String failedGettingOperarionUpdateds();
	

}
