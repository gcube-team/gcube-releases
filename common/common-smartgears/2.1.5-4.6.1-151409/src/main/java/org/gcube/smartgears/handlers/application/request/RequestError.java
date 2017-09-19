package org.gcube.smartgears.handlers.application.request;

import javax.servlet.http.HttpServletResponse;


/**
 * Known error types.
 * <p>
 * Each type can throw a corresponding {@link RequestException}.
 * 
 * @author Fabio Simeoni
 *
 */
public enum RequestError {

	/**
	 * The error raised when requests are made to failed applications.
	 */
	application_failed_error(HttpServletResponse.SC_GONE,"this resource is not currently available"),
	
	/**
	 * The error raised when requests are made to stopped applications.
	 */
	application_unavailable_error(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "this resource is permanently available"),
	
	
	
	/**
	 * The error raised when requests are made to unknown resources.
	 */
	resource_notfound_error(HttpServletResponse.SC_NOT_FOUND, "no gCube resource at this URI"),
	
	
	/**
	 * The error raised when requests require illegal resource state transitions.
	 */
	illegal_state_error(HttpServletResponse.SC_CONFLICT, "this resource cannot assume the required state"),
	
	
	/**
	 * The error raised when requests are made with unsupported HTTP methods.
	 */
	method_unsupported_error(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "this resource does not support this method"),
	
	/**
	 * The error raised when requests are genrically invalid.
	 */
	invalid_request_error(HttpServletResponse.SC_BAD_REQUEST, "this resource cannot process this request because it is malformed"),
	
	request_not_authorized_error(HttpServletResponse.SC_UNAUTHORIZED, "this resource cannot process this request because it needs authorization"),
	
	internal_server_error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unexpected error"),

	
	/**
	 * The error raised when requests carry an unsupported media type.
	 */
	incoming_contenttype_unsupported_error(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "this resource cannot consume this media type"),
	
	
	/**
	 * The error raised when requests request an unsupported media type.
	 */
	outgoing_contenttype_unsupported_error(HttpServletResponse.SC_NOT_ACCEPTABLE, "this resource cannot produce this media type"),
	

	/**
	 * An error raised by managed applications.
	 */
	application_error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "this resource has incurred in a generic error");
	
	
	private final int code;
	private final String msg;
	
	private RequestError(int code,String msg) {
		this.code=code;
		this.msg=msg;
	}
		
	public int code() {
		return code;
	}
	
	public String message() {
		return msg;
	}
	
	public void fire() {
		throw toException();
	}
	
	public void fire(String msg) {
		throw toException(msg);
	}
	
	public void fire(Throwable cause) {
		throw toException(cause);
	}
	
	public void fire(String msg,Throwable cause) {
		throw toException(msg,cause);
	}
	
	public RequestException toException() {
		return new RequestException(this);
	}
	
	public RequestException toException(String msg) {
		return new RequestException(this, msg);
	}
	
	public RequestException toException(Throwable cause) {
		return new RequestException(this,cause);
	}
	
	public RequestException toException(String msg,Throwable cause) {
		return new RequestException(this,cause,msg);
	}
	
	
}
