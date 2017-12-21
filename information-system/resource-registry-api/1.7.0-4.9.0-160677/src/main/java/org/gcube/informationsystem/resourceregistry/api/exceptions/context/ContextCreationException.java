/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextCreationException extends ContextException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7235498402448768270L;

	public ContextCreationException(String message) {
		super(message);
	}
	
	public ContextCreationException(Throwable cause) {
		super(cause);
	}
	
	public ContextCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
