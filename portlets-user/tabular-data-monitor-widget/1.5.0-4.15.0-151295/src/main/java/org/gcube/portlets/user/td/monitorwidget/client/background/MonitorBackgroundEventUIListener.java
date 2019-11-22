package org.gcube.portlets.user.td.monitorwidget.client.background;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public interface MonitorBackgroundEventUIListener {
	/**
	 * Called when the task is aborted
	 * 
	 * @param taskId Task id
	 */
	public void requestAborted(String taskId);
	
	
	/**
	 * Called when the task is hidden
	 * 
	 * @param taskId Task id
	 */
	public void requestHidden(String taskId);
	
	
	/**
	 * Called when the task is resumed
	 * 
	 * 
	 * @param taskId Task id
	 */
	public void requestResume(String taskId);
	
	
	
	
	
	
}
