package org.gcube.data.analysis.dataminermanagercl.server.monitor;

/**
 * Defines a listener for operation progress.
 *
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface DMMonitorListener {

	
	
	/**
	 * Called when the operation is starting.
	 */
	public void accepted();
	
	/**
	 * Called when there is a progress for the operation.
	 * @param elaborated the elaborated part.
	 */
	public void cancelled();
	
	/**
	 * Called when the operation is complete
	 * @param percentage 
	 * @param endDate 
	 */
	public void complete(double percentage);
	
	
	/**
	 * Called when the operation is failed
	 * @param exception 
	 * @param string 
	 */
	public void failed(String message, Exception exception);
	
	
	/**
	 * Called when the operation is running
	 * @param percentage 
	 */
	public void running(double percentage);
	
	
}
