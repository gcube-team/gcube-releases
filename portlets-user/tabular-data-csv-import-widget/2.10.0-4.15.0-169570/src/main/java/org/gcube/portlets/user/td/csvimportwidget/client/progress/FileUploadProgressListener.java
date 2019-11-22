/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.progress;

/**
 * Defines a listener for operation progress.
 *
 * @author "Giancarlo Panichi"
 * 
 *
 */
public interface FileUploadProgressListener {

	public void operationInitializing();

	public void operationUpdate(float elaborated);

	public void operationComplete();

	public void operationFailed(Throwable caught, String reason, String failureDetails);
}
