package org.gcube.portlets.user.td.monitorwidget.client;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public interface MonitorDialogEventUIListener {
	/**
	 * Called when the operation is aborted
	 * 
	 */
	public void requestAborted();
	
	
	/**
	 * Called when the operation is put in the background
	 * 
	 */
	public void requestPutInBackground();
	
	
}
