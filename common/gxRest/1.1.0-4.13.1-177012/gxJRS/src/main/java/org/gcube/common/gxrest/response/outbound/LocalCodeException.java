
package org.gcube.common.gxrest.response.outbound;

/**
 * A local exception wrapping an {@link ErrorCode}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class LocalCodeException extends Exception implements ErrorCode {
	
	private static final long serialVersionUID = 1872093579811881630L;
	private final int id;
	private final String message;

	public LocalCodeException(ErrorCode code) {
		super();
		this.id = code.getId();
		this.message = code.getMessage();
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}
}
