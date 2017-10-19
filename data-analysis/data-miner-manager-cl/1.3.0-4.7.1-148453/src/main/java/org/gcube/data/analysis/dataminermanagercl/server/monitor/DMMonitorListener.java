package org.gcube.data.analysis.dataminermanagercl.server.monitor;

/**
 * Defines a listener for operation progress.
 *
 * @author Giancarlo Panichi
 * 
 *
 */
public interface DMMonitorListener {

	/**
	 * Called when the operation is starting.
	 */
	public void accepted();

	/**
	 * Called when there is a progress for the operation.
	 * 
	 */
	public void cancelled();

	/**
	 * Called when the operation is complete
	 * 
	 * @param percentage
	 *            percentage
	 */
	public void complete(double percentage);

	/**
	 * Called when the operation is failed
	 *
	 * @param message
	 *            message
	 * @param exception
	 *            exception
	 */
	public void failed(String message, Exception exception);

	/**
	 * Called when the operation is running
	 * 
	 * @param percentage
	 *            percentage
	 */
	public void running(double percentage);

}
