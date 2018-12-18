package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class SchemaNotFoundException extends SchemaException {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1441446827386524456L;

	public SchemaNotFoundException(String message) {
		super(message);
	}

	public SchemaNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public SchemaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
