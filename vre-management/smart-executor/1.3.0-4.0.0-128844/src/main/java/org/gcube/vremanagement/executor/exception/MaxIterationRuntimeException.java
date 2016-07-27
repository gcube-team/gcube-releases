/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class MaxIterationRuntimeException extends RuntimeException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7730594422282391883L;
	
	public MaxIterationRuntimeException() {
        super();
    }
	
	public MaxIterationRuntimeException(String message) {
        super(message);
    }
	
	public MaxIterationRuntimeException(Throwable throwable){
		super(throwable);
	}
	
	public MaxIterationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
