/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class InvalidPluginStateEvolutionException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7730594422282391883L;
	
	public InvalidPluginStateEvolutionException() {
        super();
    }
	
	public InvalidPluginStateEvolutionException(String message) {
        super(message);
    }
	
	public InvalidPluginStateEvolutionException(Throwable throwable){
		super(throwable);
	}
	
	public InvalidPluginStateEvolutionException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
