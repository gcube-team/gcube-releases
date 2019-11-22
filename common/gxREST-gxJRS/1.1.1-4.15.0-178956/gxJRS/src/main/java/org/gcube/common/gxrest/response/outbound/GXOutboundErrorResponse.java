package org.gcube.common.gxrest.response.outbound;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An outbound error response message for applications.
 *
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXOutboundErrorResponse {

	private GXOutboundErrorResponse() {}

	/**
	 * Throws the exception to the client.
	 * @param exception
	 */
	public static void throwException(Exception exception) {
		throw new WebStreamException(exception);
	}

	/**
	 * Throws the exception to the client.
	 * @param exception
	 */
	public static void throwException(Exception exception, Response.Status status) {
		throw new WebStreamException(exception, status);
	}

	/**
	 * Throws the exception to the client.
	 * @param exception
	 * @param keepLines number of lines in the stacktrace to keep (max is 5)
	 */
	public static void throwExceptionWithTrace(Exception exception, int keepLines, Response.Status status) {
		throw new WebStreamException(exception, keepLines, status);
	}

	/**
	 * Throws the exception to the client.
	 * @param exception
	 * @param keepLines number of lines in the stacktrace to keep (max is 5)
	 * @param type the media type associated to the response
	 */
	public static void throwExceptionWithTrace(Exception exception, int keepLines, Response.Status status, MediaType type) {
		throw new WebStreamException(exception, keepLines, status, type);
	}

	/**
	 * Throws the exception to the client.
	 * @param exception
	 * @param keepLines number of lines in the stacktrace to keep (max is 5)
	 */
	public static void throwExceptionWithTrace(Exception exception, int keepLines) {
		throw new WebStreamException(exception, keepLines);
	}

	/**
	 * Throws the error code to the client.
	 * @param code
	 */
	public static void throwErrorCode(ErrorCode code) {
		throw new WebCodeException(code);
	}

	/**
	 * Returns the error code to the client with the HTTP status.
	 * @param code
	 * @param status
	 */
	public static void throwErrorCode(ErrorCode code, Response.Status status) {
		throw new WebCodeException(code, status);
	}

	/**
	 * Returns the HTTP status to the client as error.
	 * @param status
	 * @param message
	 */
	public static void throwHTTPErrorStatus(Response.Status status, String message) {
		if (status.getStatusCode() < 400)
			throw new IllegalArgumentException("Error status must be >= 400.");
		throw new WebApplicationException(message, status.getStatusCode());
	}
}
