package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class SchemaNotFoundException extends SchemaException implements NotFound {
	
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
