package org.gcube.portlets.user.td.monitorwidget.client.background;

public interface MonitorBackgroundEventUIListener {
	/**
	 * Called when the task is aborted
	 * 
	 */
	public void requestAborted(String taskId);
	
	
	/**
	 * Called when the task is hidden
	 * 
	 */
	public void requestHidden(String taskId);
	
	
	/**
	 * Called when the task is resumed
	 * 
	 */
	public void requestResume(String taskId);
	
	
	
	
	
	
}
