/**
 * 
 */
package org.gcube.informationsystem.model.reference.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class InvalidFacet extends InvalidEntity {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8925686090014254511L;

	public InvalidFacet(String message) {
		super(message);
	}
	
	public InvalidFacet(Throwable cause) {
		super(cause);
	}
	
	public InvalidFacet(String message, Throwable cause) {
		super(message, cause);
	}
	
}
