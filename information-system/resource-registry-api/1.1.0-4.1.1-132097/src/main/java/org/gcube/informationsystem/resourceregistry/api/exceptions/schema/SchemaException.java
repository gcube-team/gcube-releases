package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class SchemaException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8119058749936021156L;

	public SchemaException(String message) {
		super(message);
	}

	public SchemaException(Throwable cause) {
		super(cause);
	}
	
	public SchemaException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
