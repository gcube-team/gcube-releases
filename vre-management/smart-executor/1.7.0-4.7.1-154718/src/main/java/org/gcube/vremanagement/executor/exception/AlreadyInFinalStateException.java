/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class AlreadyInFinalStateException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7730594422282391883L;
	
	public AlreadyInFinalStateException() {
        super();
    }
	
	public AlreadyInFinalStateException(String message) {
        super(message);
    }
	
	public AlreadyInFinalStateException(Throwable throwable){
		super(throwable);
	}
	
	public AlreadyInFinalStateException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
