package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class SchemaException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 7661052329866006361L;

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
