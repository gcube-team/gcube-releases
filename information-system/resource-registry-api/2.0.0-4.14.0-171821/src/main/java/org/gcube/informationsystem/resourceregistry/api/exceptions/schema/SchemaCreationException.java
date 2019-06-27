/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SchemaCreationException extends SchemaException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7235498402448768270L;

	public SchemaCreationException(String message) {
		super(message);
	}
	
	public SchemaCreationException(Throwable cause) {
		super(cause);
	}
	
	public SchemaCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
