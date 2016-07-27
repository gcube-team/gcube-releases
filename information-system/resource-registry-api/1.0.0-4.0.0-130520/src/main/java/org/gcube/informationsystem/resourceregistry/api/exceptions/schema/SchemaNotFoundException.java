package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class SchemaNotFoundException extends SchemaException implements ObjectNotFound {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7068170595199626085L;

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
