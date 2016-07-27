package gr.uoa.di.madgik.execution.report;

/**
 * Used for asynchronous notification dispatch
 * 
 * @author jgerbe
 *
 */
public abstract class Dispatcher implements Runnable {
	/**
	 * Starts thread to dispatch the notification
	 */
	public void commit() {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
}
