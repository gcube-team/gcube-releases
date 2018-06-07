/**
 * 
 */
package org.gcube.informationsystem.model.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class InvalidEntity extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3836178304976048880L;

	public InvalidEntity(String message) {
		super(message);
	}
	
	public InvalidEntity(Throwable cause) {
		super(cause);
	}
	
	public InvalidEntity(String message, Throwable cause) {
		super(message, cause);
	}
	
}
