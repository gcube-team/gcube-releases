/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SchedulePersistenceException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3261726979079756047L;

	public SchedulePersistenceException() {
        super();
    }
	
	public SchedulePersistenceException(String message) {
        super(message);
    }
	
	public SchedulePersistenceException(Throwable throwable){
		super(throwable);
	}
	
	public SchedulePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
