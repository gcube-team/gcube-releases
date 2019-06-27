package org.gcube.gcat.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ScienceCatalogueException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5449813222333935588L;
	
	public ScienceCatalogueException(String message) {
		super(message);
	}

	public ScienceCatalogueException(Throwable cause) {
		super(cause);
	}
	
	public ScienceCatalogueException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
