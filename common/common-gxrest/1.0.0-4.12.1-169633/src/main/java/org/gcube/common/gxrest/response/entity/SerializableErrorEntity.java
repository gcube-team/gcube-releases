package org.gcube.common.gxrest.response.entity;

import org.gcube.common.gxrest.response.outbound.ErrorCode;

/**
 * An entity that can be serialized in a {@link WebCodeException}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class SerializableErrorEntity {
	
	private int id = -1;
	private String message;
	private String exceptionClass;
	private String encodedTrace = "";

	public SerializableErrorEntity() {}
	
	/**
	 * @param id
	 * @param message
	 */
	public SerializableErrorEntity(ErrorCode errorCode) {
		this.id = errorCode.getId();
		this.message = errorCode.getMessage();
	}

	/**
	 * 
	 * @param e
	 */
	public SerializableErrorEntity(Exception e) {
		this.exceptionClass = e.getClass().getCanonicalName();
		this.message = e.getMessage();
	}
	
	/**
	 * 
	 * @param e
	 */
	public SerializableErrorEntity(Exception e, int lines) {
		this.exceptionClass = e.getClass().getCanonicalName();
		this.message = e.getMessage();
		this.encodedTrace = StackTraceEncoder.encodeTrace(e.getStackTrace(), lines);
	}

	public int getId() {
		return this.id;
	}


	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the full qualified name of the embedded {@link Exception}
	 */
	public String getExceptionClass() {
		return this.exceptionClass;
	}
	
	/**
	 * @return the encoded stacktrace
	 */
	public String getEncodedTrace() {
		return encodedTrace;
	}

	/**
	 * Checks if a stacktrace is available.
	 * @return true if a stacktrace is serialized in the entity.
	 */
	public boolean hasStackTrace() {
		return !this.encodedTrace.isEmpty();
	}
}
