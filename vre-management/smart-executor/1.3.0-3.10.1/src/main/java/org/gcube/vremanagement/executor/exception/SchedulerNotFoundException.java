/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import org.quartz.SchedulerException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SchedulerNotFoundException extends SchedulerException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7108678230246937588L;

	public SchedulerNotFoundException() {
        super();
    }
	
	public SchedulerNotFoundException(String message) {
        super(message);
    }
	
	public SchedulerNotFoundException(Throwable throwable){
		super(throwable);
	}
	
	public SchedulerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
