/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadMonitor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This {@link Timer} retrieves {@link OperationProgress} from the specified
 * {@link OperationProgressSource} with the scheduled interval. The retrieved
 * information are spread to the subscribed
 * {@link CodelistMappingImportProgressListener}.
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FileUploadProgressUpdater extends Timer {

	private ArrayList<FileUploadProgressListener> listeners = new ArrayList<FileUploadProgressListener>();
	private static FileUploadProgressMessages msgs = GWT
			.create(FileUploadProgressMessages.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Log.debug("requesting operation progress");
		TDGWTServiceAsync.INSTANCE
				.getFileUploadMonitor(new AsyncCallback<FileUploadMonitor>() {

					public void onFailure(Throwable caught) {
						cancel();
						Log.error("Error retrieving the operation state",
								caught);
						String message = getStack(caught);
						fireOperationFailed(caught,
								msgs.failedGettingOperarionUpdateds(), message);
					}

					public void onSuccess(FileUploadMonitor result) {
						Log.info("retrieved FileUploadMonitor: "
								+ result.getState());
						switch (result.getState()) {
						case STARTED:
							Log.debug("File Upload Started");
							break;
						case INPROGRESS:
							Log.debug("Progress: "
									+ result.getElaboratedLenght() + " of "
									+ result.getTotalLenght());
							fireOperationUpdate(result.getPercentDone());
							break;
						case FAILED:
							Log.debug("File Upload Failed");
							cancel();
							fireOperationFailed(
									new Throwable(msgs.fileUploadFailedHead()),
									result.getFailureReason(),
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

	protected void fireOperationFailed(Throwable caught, String failure,
			String failureDetails) {
		for (FileUploadProgressListener listener : listeners)
			listener.operationFailed(caught, failure, failureDetails);
	}

	/**
	 * Add a new {@link CodelistMappingImportProgressListener} to this
	 * {@link FileUploadProgressUpdater}.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addListener(FileUploadProgressListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the specified {@link CodelistMappingImportProgressListener} from
	 * this {@link FileUploadProgressUpdater}.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(FileUploadProgressListener listener) {
		listeners.remove(listener);
	}
}
