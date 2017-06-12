/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.progress;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This {@link Timer} retrieves {@link OperationProgress} from the specified {@link OperationProgressSource} with the scheduled interval.
 * The retrieved information are spread to the subscribed {@link OperationProgressListener}.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class OperationProgressUpdater extends Timer {
	protected ArrayList<OperationProgressListener> listeners = new ArrayList<OperationProgressListener>();
	protected OperationProgressSource source;
	Logger logger= Logger.getLogger("");
	/**
	 * Creates a new {@link OperationProgressUpdater} with the specified {@link OperationProgressSource}.
	 * @param source the {@link OperationProgress} source.
	 */
	public OperationProgressUpdater(OperationProgressSource source){
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		GWT.log("requesting operation progress");
		logger.log(Level.SEVERE, "run of OperationProgressUpdater timer ");
		source.getProgress(new AsyncCallback<OperationProgress>() {
			
			@Override
			public void onSuccess(OperationProgress status) {
				GWT.log("retrieved OperationStatus: "+status);
				logger.log(Level.SEVERE, "Status : "+ status);
				logger.log(Level.SEVERE, "Status  totalLenght : "+ status.getTotalLenght());
				logger.log(Level.SEVERE, "Status elaboratedLenght: "+ status.getElaboratedLenght());
				logger.log(Level.SEVERE, "IN PROGRESSS********");

				switch (status.getState()) {
				
					case INPROGRESS: {


						fireOperationUpdate(status.getTotalLenght(), status.getElaboratedLenght()); break;
					}
					case COMPLETED: {
						cancel();
						fireOperationComplete(); 
						break;
					}
					case FAILED: {
						cancel();
						fireOperationFailed(null, status.getFailureReason(), status.getFailureDetails()); 
						break;
					}
				}				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				cancel();
				GWT.log("Error retrieving the operation state", caught);
				String message = getStack(caught);
				fireOperationFailed(caught, "Failed getting operation updates", message);
			}
		});
		
	}
	
	protected String getStack(Throwable e)
	{
		String message = e.getMessage()+" -> <br>";
		Throwable c = e.getCause();
		if (c!=null) message += getStack(c);
		
		return message;
	}

	protected void fireOperationUpdate(long total, long elaborated)
	{
		for (OperationProgressListener listener:listeners) listener.operationUpdate(total, elaborated);
	}

	protected void fireOperationComplete()
	{
		for (OperationProgressListener listener:listeners) listener.operationComplete();
	}
	
	protected void fireOperationFailed(Throwable caught, String reason, String failureDetails)
	{
		for (OperationProgressListener listener:listeners) listener.operationFailed(caught, reason, failureDetails);
	}
	
	/**
	 * Add a new {@link OperationProgressListener} to this {@link OperationProgressUpdater}.
	 * @param listener the listener to add.
	 */
	public void addListener(OperationProgressListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes the specified {@link OperationProgressListener} from this {@link OperationProgressUpdater}.
	 * @param listener the listener to remove.
	 */
	public void removeListener(OperationProgressListener listener)
	{
		listeners.remove(listener);
	}
}
