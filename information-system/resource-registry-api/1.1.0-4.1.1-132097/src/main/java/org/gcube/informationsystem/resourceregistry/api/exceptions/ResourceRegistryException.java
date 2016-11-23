/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ResourceRegistryException extends Exception {

	/**
	 *  Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8321258637131836003L;
	
	public ResourceRegistryException(String message) {
		super(message);
	}
	
	public ResourceRegistryException(Throwable cause) {
		super(cause);
	}
	
	public ResourceRegistryException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
