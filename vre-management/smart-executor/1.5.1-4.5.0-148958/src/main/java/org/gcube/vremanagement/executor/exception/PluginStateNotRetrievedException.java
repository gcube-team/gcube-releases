/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class PluginStateNotRetrievedException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5828105692475375250L;
	
	public PluginStateNotRetrievedException() {
        super();
    }
	
	public PluginStateNotRetrievedException(String message) {
        super(message);
    }
	
	public PluginStateNotRetrievedException(Throwable throwable){
		super(throwable);
	}
	
	public PluginStateNotRetrievedException(String message, Throwable cause) {
        super(message, cause);
    }

}
