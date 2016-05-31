/**
 * 
 */
package org.gcube.data.tm.publishers;

import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;

/**
 * 
 * A {@link GCUBEScheduledHandler} which tolerates failure for a maximum number of times. 
 * @author Fabio Simeoni
 *
 */
public class ResilientScheduler extends GCUBEScheduledHandler<Void> {

	private int attempts=3;
	private long delay=1;
	
	/** Creates an instance with a given execution mode, time interval, and scheduled handler. 
	 * 
	 * @param interval the time interval in seconds.
	 * @param mode the execution mode.
	 * */
	@SuppressWarnings("unchecked")
	public ResilientScheduler(long interval, Mode mode) {	
		super(interval,mode);
		
	}
	
	/**{@inheritDoc}*/
	@Override
	protected synchronized boolean repeat(Exception exception, int exceptionCount) {
		
		if (exception==null) return false;
		
		if (exceptionCount>=getAttempts()) {
			logger.error("could not publish source profile (final attempt)", exception);
			return false;
		}
		else {
			logger.warn("could not publish source profile (attempt "+exceptionCount+" of "+getAttempts(),exception);
			if (getInterval()<getDelay()) //increases delay
				setInterval(getDelay());
			return true;
		}
		
	}

	/**
	 * Returns the number of publication attempts.
	 * @return the attempts.
	 */
	public synchronized int getAttempts() {
		return attempts;
	}
		
		/**
	 * Sets the number of publication attempts;
	 * @param attempts the attempts.
	 */
	public synchronized void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	/**
	 * Returns the interval between publication attempts.
	 * @return the delay.
	 */
	public synchronized long getDelay() {
		return delay;
	}

	/**
	 * Sets the interval between publication attempts.
	 * @param delay the delay
	 */
	public synchronized void setDelay(long delay) {
		this.delay = delay;
	}
}
