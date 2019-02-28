package org.gcube.informationsystem.resourceregistry.api.exceptions.schema;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SchemaAlreadyPresentException extends SchemaException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -6141449434537456516L;

	public SchemaAlreadyPresentException(String message) {
		super(message);
	}

	public SchemaAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public SchemaAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
