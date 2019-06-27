package org.gcube.common.gxrest.response.outbound;

/**
 * Interface for error codes.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public interface ErrorCode {
	
	/**
	 * Identifier of the code.
	 */
	public int getId();

	/**
	 * The message associated to the code
	 * @return the message
	 */
	public String getMessage();
	
}
