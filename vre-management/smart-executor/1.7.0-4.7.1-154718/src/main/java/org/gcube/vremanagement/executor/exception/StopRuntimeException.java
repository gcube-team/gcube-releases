/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class StopRuntimeException extends RuntimeException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7730594422282391883L;
	
	public StopRuntimeException() {
        super();
    }
	
	public StopRuntimeException(String message) {
        super(message);
    }
	
	public StopRuntimeException(Throwable throwable){
		super(throwable);
	}
	
	public StopRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
