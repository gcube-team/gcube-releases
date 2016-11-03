/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ScopeNotMatchException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 120445106456933848L;
	
	public ScopeNotMatchException() {
        super();
    }
	
	public ScopeNotMatchException(String message) {
        super(message);
    }
	
	public ScopeNotMatchException(Throwable throwable){
		super(throwable);
	}
	
	public ScopeNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
