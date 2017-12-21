/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions.er;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ERException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -2231802464980906972L;

	public ERException(String message) {
		super(message);
	}
	
	public ERException(Throwable cause) {
		super(cause);
	}
	
	public ERException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
