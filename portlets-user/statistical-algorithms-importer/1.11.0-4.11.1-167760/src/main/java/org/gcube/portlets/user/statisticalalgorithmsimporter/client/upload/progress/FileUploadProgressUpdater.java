/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress;

import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class FileUploadProgressUpdater extends Timer {

	protected ArrayList<FileUploadProgressListener> listeners = new ArrayList<FileUploadProgressListener>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Log.debug("requesting operation progress");
		StatAlgoImporterServiceAsync.INSTANCE.getFileUploadMonitor(new AsyncCallback<FileUploadMonitor>() {

			public void onFailure(Throwable caught) {
				cancel();
				Log.error("Error retrieving the operation state", caught);
				String message = getStack(caught);
				fireOperationFailed(caught, "Failed getting operation updates", message);
			}

			public void onSuccess(FileUploadMonitor result) {
				Log.info("retrieved FileUploadMonitor: " + result.getState());
				switch (result.getState()) {
				case STARTED:
					Log.debug("File Upload Started");
					break;
				case INPROGRESS:
					Log.debug("Progress: " + result.getElaboratedLenght() + " of " + result.getTotalLenght());
					fireOperationUpdate(result.getPercentDone());
					break;
				case FAILED:
					Log.debug("File Upload Failed");
					cancel();
					fireOperationFailed(new Throwable("File Upload Failed"), result.getFailureReason(),
							result.getFailureDetails());
					break;
				case COMPLETED:
					cancel();
					Log.debug("File Upload Completed");
					fireOperationComplete();
					break;
				default:
					break;
				}

			}

		});

	}

	protected String getStack(Throwable e) {
		String message = e.getLocalizedMessage() + " -> <br>";
		Throwable c = e.getCause();
		if (c != null)
			message += getStack(c);
		return message;
	}

	protected void fireOperationInitializing() {
		for (FileUploadProgressListener listener : listeners)
			listener.operationInitializing();
	}

	protected void fireOperationUpdate(float elaborated) {
		for (FileUploadProgressListener listener : listeners)
			listener.operationUpdate(elaborated);
	}

	protected void fireOperationComplete() {
		for (FileUploadProgressListener listener : listeners)
			listener.operationComplete();
	}

	protected void fireOperationFailed(Throwable caught, String failure, String failureDetails) {
		for (FileUploadProgressListener listener : listeners)
			listener.operationFailed(caught, failure, failureDetails);
	}

	/**
	 * 
	 * @param listener
	 *            listener
	 */
	public void addListener(FileUploadProgressListener listener) {
		listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 *            listener
	 */
	public void removeListener(FileUploadProgressListener listener) {
		listeners.remove(listener);
	}
}
